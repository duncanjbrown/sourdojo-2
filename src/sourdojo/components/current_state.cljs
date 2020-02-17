(ns sourdojo.components.current-state
  (:require [sourdojo.bake-state-machine :as bake-states]))

(defn render
  [state]
  [:div.state
    [:h3.state--pre-header "current state"]
    [:h2.state--state-header (bake-states/translate state)]])
