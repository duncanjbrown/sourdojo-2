(ns sourdojo.core
  (:require [reagent.dom :as r]
            [re-frame.core :as rf]
            [sourdojo.firebase.core :as firebase]
            [sourdojo.firebase.auth :as firebase-auth]
            [sourdojo.subs]
            [sourdojo.events]
            [sourdojo.env :as env]
            [sourdojo.components.header :as header]
            [sourdojo.components.timeline :as timeline]
            [sourdojo.components.bake-list :as bake-list]))

(defn app []
  [:div
   [header/render]
   (if @(rf/subscribe [:current-bake])
    [timeline/render @(rf/subscribe [:steps]) @(rf/subscribe [:current-state])]
    [:button {:class "actions--button"
              :type "button"
              :on-click #(rf/dispatch [:initialise-bake])}
     "Start!"])
   [bake-list/render @(rf/subscribe [:past-bakes])]])

(defn start! []
  (r/render [app] (. js/document getElementById "app")))

(defn main []
  (firebase/init! (:firebase env/config))
  (firebase-auth/init!)
  (rf/dispatch-sync [:initialise-db])
  (start!))
