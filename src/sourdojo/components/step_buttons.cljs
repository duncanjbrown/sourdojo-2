(ns sourdojo.components.step-buttons
  (:require [re-frame.core :as rf]
            [sourdojo.bake-state-machine :as bake-states]))

(defn- create-step-event
  [step]
  {:type :step :step step :time (js/Date.)})

(defn- handle-transition
  [current-state transition]
  (let
    [new-state (get-in bake-states/states [current-state transition])]
    (rf/dispatch [:transition! new-state (create-step-event transition)])))

(defn- transition-button
  [current-state transition]
  [:button {:class "actions--button"
            :type "button"
            :on-click #(handle-transition current-state transition)}
    (bake-states/translate transition)])

(defn render
  [state]
  (when-let [available-actions (bake-states/transitions-from state)]
    (into [:div.actions] (map #(transition-button state %) available-actions))))
