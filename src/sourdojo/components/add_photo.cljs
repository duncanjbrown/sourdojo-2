(ns sourdojo.components.add-photo
  (:require [re-frame.core :as rf]
            [clojure.string]))

(defn- create-photo-event
  [filename]
  {:type :photo :filename filename :time (js/Date.)})

(defn- handle-photo-capture
  [event filename-generator]
  (let [jsfile (-> event .-target .-files (aget 0))
;;      mime-type (.-type jsfile)
        extension "png" ;; TODO make this work
        filename-with-extension (str "images/" (clojure.string/join "." [(filename-generator) extension]))]
    (rf/dispatch [:save-photo {:filename filename-with-extension :file jsfile}])
    (rf/dispatch [:add-photo (create-photo-event filename-with-extension)])))

(defn render
  [filename-generator]
  (fn []
    [:form.add-photo
      [:label.add-photo--button {:for "photo"} "Take photo"]
      [:input.add-photo--input {:id "photo"
                                :type :file
                                :accept "image/*"
                                :capture true
                                :on-change #(handle-photo-capture % filename-generator)}]]))
