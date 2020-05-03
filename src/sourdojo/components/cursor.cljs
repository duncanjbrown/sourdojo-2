(ns sourdojo.components.cursor
  (:require [re-frame.core :as rf]
            [goog.string]
            [sourdojo.components.step-buttons :as step-buttons]
            [sourdojo.components.add-note :as add-note]
            [sourdojo.components.add-photo :as add-photo]))

(defn- unique-photo-filename
  []
  (clojure.string/join "-" [@(rf/subscribe [:current-bake-id]) (goog.string/getRandomString)]))

(defn- starting-cursor []
  [:div
   [step-buttons/render @(rf/subscribe [:current-state])]])

(defn- in-progress-cursor []
  [:div
    [step-buttons/render @(rf/subscribe [:current-state])]
    [add-note/render]
    [add-photo/render unique-photo-filename]])

(defn render [current-state]
  [:li#cursor
   (if (= :origin current-state)
     (starting-cursor)
     (in-progress-cursor))])
