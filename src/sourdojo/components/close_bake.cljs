(ns sourdojo.components.close-bake
  (:require [re-frame.core :as rf]))

(defn render
  []
  (fn []
    [:form.add-note {:on-submit (fn [e]
                                  (.preventDefault e)
                                  (rf/dispatch [:close-bake]))}
     [:input.add-note--button {:type "submit" :value "Close bake"}]]))
