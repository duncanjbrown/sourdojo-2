(ns sourdojo.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [sourdojo.subs]
            [sourdojo.events]
            [sourdojo.components.add-note :as add-note]
            [sourdojo.components.add-photo :as add-photo]
            [sourdojo.components.current-state :as current-state]
            [sourdojo.components.step-buttons :as step-buttons]
            [sourdojo.components.timeline :as timeline]))

(defn app []
  [:div
    [current-state/render @(rf/subscribe [:current-state])]
    [step-buttons/render @(rf/subscribe [:current-state])]
    [add-note/render]
    [add-photo/render]
    [timeline/render @(rf/subscribe [:steps])]])

(defn start! []
  (r/render [app] (. js/document getElementById "app")))

(defn main []
  (rf/dispatch-sync [:initialise-bake])
  (start!))
