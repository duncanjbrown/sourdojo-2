(ns sourdojo.components.add-photo
  (:require [re-frame.core :as rf]))

(defn render
  []
  (fn []
    [:form.add-photo
     [:label.add-photo--button {:for "photo"} "Take photo"]
     [:input.add-photo--input {:id "photo"
                               :type :file
                               :accept "image/*"
                               :capture true
                               :on-change #(rf/dispatch [:add-photo (-> % .-target .-files (aget 0))])}]]))
