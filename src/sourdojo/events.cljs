(ns sourdojo.events
  (:require
   [sourdojo.db :as db :refer [initial-db]]
   [sourdojo.bake :as bake]
   [sourdojo.bake-state-machine :as bake-states]
   [re-frame.core :refer [reg-event-db reg-event-fx]]))

(reg-event-fx
 :initialise-db
 (fn [{:keys [db]} _]
   {:db initial-db}))

(reg-event-fx
 :initialise-bake
 (fn [{:keys [db]} _]
   {:db (assoc db :current-bake bake/new-bake)}))

(reg-event-fx
 :transition!
 (fn [{:keys [db]} [_ current-state transition]]
   (let
     [new-state (get-in bake-states/states [current-state transition])
      step (create-step-event transition)]
    {:db (->
            db
            (assoc-in [:current-bake :state] new-state)
            (update-in [:current-bake :steps] conj step))})))

(defn- create-step-event
  [step]
  {:type :step :step step :time (js/Date.)})
