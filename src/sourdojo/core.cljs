(ns sourdojo.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [sourdojo.subs]
            [sourdojo.events]
            [sourdojo.components.add-note :as add-note]
            [sourdojo.components.add-photo :as add-photo]
            ;; [sourdojo.components.step-buttons :as step-buttons]
            [sourdojo.components.current-state :as current-state]
            [sourdojo.components.step-buttons :as step-buttons]
            [sourdojo.components.timeline :as timeline]))

;; (defn current-bake []
;;   (let [state (r/cursor app-state [:state])
;;         events (r/cursor app-state [:events])]
;;     [:div
;;       [state-display/render @state]
;;       [step-buttons/render state events]
;;       [add-note/render events]
;;       [add-photo/render events]
;;       [timeline/render @events]
;;       [:a {:href (rfe/href ::about)} "About"]]))

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
