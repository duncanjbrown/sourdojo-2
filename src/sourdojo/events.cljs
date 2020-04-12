(ns sourdojo.events
  (:require
   [sourdojo.db :as db :refer [initial-db]]
   [sourdojo.bake :as bake]
   [sourdojo.bake-state-machine :as bake-states]
   [re-frame.core :refer [reg-event-db reg-event-fx]]))

(def save-to-firestore
  (re-frame.core/->interceptor
   :id      :save-to-firestore
   :after   (fn [context]
              (let [new-bake-value (get-in context [:effects :db :current-bake])]
                (assoc-in context [:effects :dispatch] [:save-to-firestore new-bake-value])))))

(reg-event-fx
 :save-to-firestore
 (fn [_ [_ bake]]
   (println bake)))

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
 (fn [{:keys [db]} _]
   {:db initial-db}))

(reg-event-fx
 :initialise-bake
 [save-to-firestore]
 (fn [{:keys [db]} _]
   {:db (assoc db :current-bake bake/new-bake)}))

(reg-event-fx
 :transition!
 [save-to-firestore]
 (fn [{:keys [db]} [_ new-state step]]
   {:db (->
         db
         (assoc-in [:current-bake :state] new-state)
         (update-in [:current-bake :steps] conj step))}))

(reg-event-fx
 :add-note
 [save-to-firestore]
 (fn [{:keys [db]} [_ note]]
   {:db (update-in db [:current-bake :steps] conj note)}))

(reg-event-fx
 :add-photo
 [save-to-firestore]
 (fn [{:keys [db]} [_ photo]]
   {:db (update-in db [:current-bake :steps] conj photo)}))
