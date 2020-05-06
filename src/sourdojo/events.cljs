(ns sourdojo.events
  (:require
   [sourdojo.db :as db :refer [initial-db]]
   [sourdojo.firebase.firestore :as firestore]
   [sourdojo.firebase.storage :as firebase-storage]
   [sourdojo.bake :as bake]
   [re-frame.core :refer [reg-event-fx reg-fx]]))

(def save-bake
  (re-frame.core/->interceptor
   :id      :save-bake
   :after   (fn [context]
              (let [new-bake-value (get-in context [:effects :db :current-bake])]
                (assoc-in context [:effects :save-current-bake-to-firestore] new-bake-value)))))

(reg-fx
 :upload-to-firestore-storage
 (fn [{:keys [filename file]}]
   (firebase-storage/save-image filename file)))

(reg-fx
 :save-current-bake-to-firestore
 (fn [bake]
   (if-let [id (:id bake)]
     (firestore/set-doc (str "bakes/" id) bake :current-bake)
     (firestore/add-doc "bakes" bake :current-bake))))

(reg-event-fx
 :firestore-ok
 (fn [{:keys [db]} [_ action path-or-id hook]]
   (println (str action " " path-or-id " " hook))
   (when (and (= action :add) (= hook :current-bake))
     {:db (assoc-in db [:current-bake :id] path-or-id)})))

(reg-event-fx
 :signed-in
 (fn [{:keys [db]} [_ user]]
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
 [save-bake]
 (fn [{:keys [db]} _]
   {:db (assoc db :current-bake bake/new-bake)}))

(reg-event-fx
 :transition!
 [save-bake]
 (fn [{:keys [db]} [_ new-state step]]
   {:db (->
         db
         (assoc-in [:current-bake :state] new-state)
         (update-in [:current-bake :steps] conj step))}))

(reg-event-fx
 :add-note
 [save-bake]
 (fn [{:keys [db]} [_ note]]
   {:db (update-in db [:current-bake :steps] conj note)}))

(reg-event-fx
 :save-photo
 (fn [{:keys [db]} [_ {:keys [file filename] :as file-map}]]
   {:upload-to-firestore-storage file-map
    :db (assoc-in db [:cache filename] (js/URL.createObjectURL file))}))

(reg-event-fx
 :add-photo
 [save-bake]
 (fn [{:keys [db]} [_ photo]]
   {:db (update-in db [:current-bake :steps] conj photo)}))
