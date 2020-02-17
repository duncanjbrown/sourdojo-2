(ns sourdojo.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn app []
  [:h1 "Hello"])

(defn start! []
  (reagent/render [app] (. js/document getElementById "app")))

(defn main []
  (start!))
