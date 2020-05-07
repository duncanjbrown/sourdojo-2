(ns sourdojo.components.step-buttons
  (:require [re-frame.core :as rf]
            [sourdojo.bake-state-machine :as bake-states]))

(defn- transition-button
  [transition]
  [:button {:class "actions--button"
            :type "button"
            :on-click #(rf/dispatch [:transition! transition])}
   (bake-states/translate transition)])

(defn render
  [state]
  (when-let [available-actions (bake-states/transitions-from state)]
    (into [:div.actions] (map #(transition-button %) available-actions))))
