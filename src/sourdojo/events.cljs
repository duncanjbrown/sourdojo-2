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
                  (firestore/set-doc (str "bakes/" id) bake :bake-updated-in-firestore)
                  (firestore/add-doc "bakes" bake :bake-created-in-firestore))
                context))))

(reg-fx
 :upload-to-firestore-storage
 (fn [{:keys [filename file]}]
   (firebase-storage/save-image filename file)))

(reg-fx
 :firestore-load
 (fn [[path callback-event]]
  (firestore/get-doc path callback-event)))

(defn firestore-bake->local-bake
  [firestore-bake]
  (let [steps
        (map (fn [step]
               (-> step
                (update :time #(.toDate %))
                (update :type keyword)
                (update-existing :step keyword)))
             (:steps firestore-bake))
        photos (:photo (group-by :type steps))]
    (doall
     (map #(firebase-storage/load-image-url % :load-image-url) (map :filename photos)))
    (-> firestore-bake
      (assoc :steps (vec steps))
      (update :state keyword))))

(reg-event-fx
 :load-image-url
 (fn [{:keys [db]} [_ filename url]]
   {:db (assoc-in db [:photo-urls filename] url)}))

(reg-event-fx
 :load-bake-in-progress
 (fn [{:keys [db]} [_ firestore-bake]]
   {:db (assoc db :current-bake (firestore-bake->local-bake firestore-bake))}))

(reg-event-fx
 :load-user
 (fn [{:keys [db]} [_ firestore-user]]
   (let [user-id (get-in db [:user :id])]
    (if-let [bake-in-progress (:current-bake firestore-user)]
      {:firestore-load [(str "bakes/" bake-in-progress) :load-bake-in-progress]}
      {:firestore-load-past-bakes user-id}))))

(reg-fx
 :firestore-load-past-bakes
 (fn [user-id]
   (firestore/query "bakes" ["user-id" "==" user-id] :load-past-bakes)))

(defn firestore-bake->past-bake
  [firestore-bake]
  (println (keys firestore-bake))
  {:id (:id firestore-bake)})

(reg-event-fx
 :load-past-bakes
 (fn [{:keys [db]} [_ results]]
   {:db (assoc db :past-bakes (map firestore-bake->past-bake results))}))

(reg-event-fx
 :firestore-ok
 (fn [_ [_ path]]
   (println (str "Firestore updated: " path))
   {}))

(reg-event-fx
 :bake-created-in-firestore
 [save-bake] ;; push the ID back up to Firestore :/
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
   (let [current-user-id (get-in db [:user :id])]
    {:db (assoc db :current-bake (merge bake/new-bake {:user-id current-user-id}))})))

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
              (assoc-in [:photo-urls filename] object-url))
      :upload-to-firestore-storage {:file jsfile :filename filename}})))

(reg-event-fx
 :close-bake
 (fn [{:keys [db]} [_]]
   (let [current-user-id (get-in db [:user :id])]
    {:db (assoc db :current-bake nil)
     :save-current-bake-on-user [nil current-user-id]
     :firestore-load-past-bakes current-user-id})))
