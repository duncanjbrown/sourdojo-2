(ns sourdojo.components.step-buttons
  (:require [re-frame.core :as rf]
            [sourdojo.bake-state-machine :as bake-states]))

(defn- transition-button
  [current-state transition]
  [:button {:class "actions--button"
            :type "button"
            :on-click #(rf/dispatch [:transition! current-state transition])}
    (bake-states/translate transition)])

(defn render
  [state]
  (when-let [available-actions (bake-states/transitions-from state)]
    (into [:div.actions] (map #(transition-button state %) available-actions))))
