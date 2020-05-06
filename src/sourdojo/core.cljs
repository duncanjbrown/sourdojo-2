(ns sourdojo.core
  (:require [reagent.dom :as r]
            [re-frame.core :as rf]
            [sourdojo.firebase.core :as firebase]
            [sourdojo.firebase.auth :as firebase-auth]
            [sourdojo.subs]
            [sourdojo.events]
            [sourdojo.env :as env]
            [sourdojo.components.header :as header]
            [sourdojo.components.timeline :as timeline]))

(defn app []
  [:div
   [header/render]
   [timeline/render @(rf/subscribe [:steps]) @(rf/subscribe [:current-state])]])

(defn start! []
  (r/render [app] (. js/document getElementById "app")))

(defn main []
  (firebase/init! (:firebase env/config))
  (firebase-auth/init!)
  (rf/dispatch-sync [:initialise-db])
  (rf/dispatch-sync [:initialise-bake])
  (start!))
