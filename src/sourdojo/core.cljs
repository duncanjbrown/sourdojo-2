(ns sourdojo.core
  (:require [reagent.dom :as r]
            [re-frame.core :as rf]
            [sourdojo.firebase.core :as firebase]
            [sourdojo.firebase.auth :as firebase-auth]
            [sourdojo.subs]
            [sourdojo.events]
            [sourdojo.env :as env]
            [sourdojo.components.user-info :as user-info]
            [sourdojo.components.add-note :as add-note]
            [sourdojo.components.add-photo :as add-photo]
            [sourdojo.components.current-state :as current-state]
            [sourdojo.components.step-buttons :as step-buttons]
            [sourdojo.components.timeline :as timeline]))

(defn app []
  [:div
    [user-info/render @(rf/subscribe [:user])]
    [current-state/render @(rf/subscribe [:current-state])]
    [step-buttons/render @(rf/subscribe [:current-state])]
    [add-note/render]
    [add-photo/render]
    [timeline/render @(rf/subscribe [:steps])]])

(defn start! []
  (r/render [app] (. js/document getElementById "app")))

(defn main []
  (firebase/init! (:firebase env/config))
  (firebase-auth/init!)
  (rf/dispatch-sync [:initialise-bake])
  (start!))
