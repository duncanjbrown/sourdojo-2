(ns sourdojo.components.bake-list
  (:require [reagent.core :as reagent]
            ["react-flip-move" :as flip-move]))

(defn- bake
  [bake]
  [:li.past-bake (:id bake)])

(defn render
  [bakes]
  [:ul#past-bakes
   (into [(reagent/adapt-react-class flip-move) {:enterAnimation "fade"
                                                 :leaveAnimation "fade"}]
         (doall (map bake bakes)))])
