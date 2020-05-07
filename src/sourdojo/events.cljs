(ns sourdojo.events
  (:require
   [sourdojo.db :as db :refer [initial-db]]
   [sourdojo.firebase.firestore :as firestore]
   [sourdojo.firebase.storage :as firebase-storage]
   [sourdojo.bake :as bake]
   [sourdojo.bake-state-machine :as bake-states]
   [re-frame.core :refer [reg-event-fx reg-fx inject-cofx reg-cofx]]
   [medley.core :refer [update-existing]]
   [clojure.string]))

(def save-bake
  (re-frame.core/->interceptor
   :id      :save-bake
   :after   (fn [context]
              (let [bake (get-in context [:effects :db :current-bake])]
                (if-let [id (:id bake)]
                  (firestore/set-doc (str "bakes/" id) bake :current-bake)
                  (firestore/add-doc "bakes" bake :create-bake))
                context))))

(reg-fx
 :upload-to-firestore-storage
 (fn [{:keys [filename file]}]
   (firebase-storage/save-image filename file)))

(reg-fx
 :firestore-load
 (fn [[path hook]]
  (firestore/get-doc path hook)))

(defn firestore-bake->local-bake
  [firestore-bake]
  (let [steps
        (map (fn [step]
               (-> step
                (update :time #(.toDate %))
                (update :type keyword)
                (update-existing :step keyword)))
             (:steps firestore-bake))]
    (-> firestore-bake
      (assoc :steps (vec steps))
      (update :state keyword))))

(reg-event-fx
 :firestore-get-ok
 (fn [{:keys [db]} [_ result hook]]
  (cond
   (= hook :load-user)
   (if-let [bake-in-progress (:current-bake result)]
    {:firestore-load [(str "bakes/" bake-in-progress) :load-bake-in-progress]})
   (= hook :load-bake-in-progress)
   {:db (assoc db :current-bake (firestore-bake->local-bake result))})))

(defn firestore-ok
  [_ [_ action path-or-id hook]]
  (println (str "Firebase saved OK: " action " " path-or-id " " hook))
  (when (= hook :create-bake)
    {:dispatch [:bake-created path-or-id]}))

(reg-event-fx
 :firestore-ok
 firestore-ok)

(reg-event-fx
 :bake-created
 (fn [{:keys [db]} [_ bake-id]]
   (let [current-user-id (get-in db [:user :id])]
    {:db (assoc-in db [:current-bake :id] bake-id)
     :save-current-bake-on-user [bake-id current-user-id]})))

(reg-fx
 :save-current-bake-on-user
 (fn [[bake-id current-user-id]]
  (firestore/set-doc-with-merge (str "users/" current-user-id) {:current-bake bake-id})))

(reg-event-fx
 :signed-in
 (fn [{:keys [db]} [_ user]]
  (firestore/get-doc (str "users/" (:id user)) :load-user)
  {:db (assoc db :user user)}))

(reg-event-fx
 :signed-out
 (fn [{:keys [db]} _]
   {:db (assoc db :user false)}))

(reg-event-fx
 :initialise-db
 (fn [_ _]
   {:db initial-db}))

(reg-event-fx
 :initialise-bake
 (fn [{:keys [db]} _]
   {:db (assoc db :current-bake bake/new-bake)}))

(reg-cofx
 :now
 (fn [cofx _]
   (assoc cofx :now (js/Date.))))

(defn do-transition
  [{:keys [db now]} [_ transition]]
  (let [current-state (get-in db [:current-bake :state])
        new-state (get-in bake-states/states [current-state transition])
        step {:type :step :step transition :time now}]
     {:db (->
           db
           (assoc-in [:current-bake :state] new-state)
           (update-in [:current-bake :steps] conj step))}))

(reg-event-fx
 :transition!
 [save-bake (inject-cofx :now)]
 do-transition)

(reg-event-fx
 :add-note
 [save-bake (inject-cofx :now)]
 (fn [{:keys [db now]} [_ contents]]
   (let [note {:type :note :note contents :time now}]
    {:db (update-in db [:current-bake :steps] conj note)})))

(reg-event-fx
 :add-photo
 [save-bake (inject-cofx :now)]
 (fn [{:keys [db now]} [_ jsfile]]
   (let [current-bake-id (get-in db [:current-bake :id])
         base-filename (clojure.string/join "-"
                        [current-bake-id (goog.string/getRandomString)])
         extension "png"
         filename (str "images/"
                       base-filename
                       "."
                       extension)
         object-url (js/URL.createObjectURL jsfile)
         photo-event {:type :photo :filename filename :time now}]
     {:db (-> db
              (update-in [:current-bake :steps] conj photo-event)
              (assoc-in [:cache filename] object-url))
      :upload-to-firestore-storage {:file jsfile :filename filename}})))
