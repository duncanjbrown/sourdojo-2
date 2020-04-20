(ns sourdojo.components.header
  (:require [sourdojo.components.user-info :as user-info]
            [re-frame.core :as rf]))

(defn render []
  [:div
   [:h1 "Sourdojo"]
   [user-info/render @(rf/subscribe [:user])]])
