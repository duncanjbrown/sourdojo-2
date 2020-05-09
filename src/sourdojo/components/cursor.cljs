(ns sourdojo.components.cursor
  (:require [re-frame.core :as rf]
            [goog.string]
            [clojure.string]
            [sourdojo.components.step-buttons :as step-buttons]
            [sourdojo.components.add-note :as add-note]
            [sourdojo.components.add-photo :as add-photo]
            [sourdojo.components.close-bake :as close-bake]))

(defn render [current-state]
  [:li#cursor
   [:div
    [step-buttons/render @(rf/subscribe [:current-state])]
    [add-note/render]
    [add-photo/render]
    [close-bake/render]]])
