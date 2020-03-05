(ns sourdojo.components.add-photo
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn- create-photo-event
  [photo-url]
  {:type :photo :step :photo :url photo-url :time (js/Date.)})

(defn- handle-photo-capture
  [event]
  (let [jsfile (-> event .-target .-files (aget 0))
        url (js/URL.createObjectURL jsfile)]
    (rf/dispatch [:add-photo (create-photo-event url)])))

(defn render
  []
  (let [photo-data (reagent/atom "")]
    (fn []
      [:form.add-photo
        [:label.add-photo--button {:for "photo"} "Take photo"]
        [:input.add-photo--input {:id "photo"
                                  :type :file
                                  :accept "image/*"
                                  :capture true
                                  :on-change handle-photo-capture}]])))
