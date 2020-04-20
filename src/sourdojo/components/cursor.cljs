(ns sourdojo.components.cursor
  (:require [re-frame.core :as rf]
            [sourdojo.components.step-buttons :as step-buttons]
            [sourdojo.components.add-note :as add-note]
            [sourdojo.components.add-photo :as add-photo]))

(defn- starting-cursor []
  [:div
   [step-buttons/render @(rf/subscribe [:current-state])]])

(defn- in-progress-cursor []
  [:div
    [step-buttons/render @(rf/subscribe [:current-state])]
    [add-note/render]
    [add-photo/render]])

(defn render [current-state]
  [:li#cursor
   (if (= :origin current-state)
     (starting-cursor)
     (in-progress-cursor))])
