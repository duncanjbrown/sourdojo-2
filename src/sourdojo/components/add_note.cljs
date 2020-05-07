(ns sourdojo.components.add-note
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [reagent.core :as reagent]))

(defn render
  []
  (let [note-contents (reagent/atom "")]
    (fn []
      [:form.add-note {:on-submit (fn [e]
                                    (.preventDefault e)
                                    (when-not (string/blank? @note-contents)
                                      (rf/dispatch [:add-note @note-contents])
                                      (reset! note-contents "")))}
       [:textarea.add-note--textarea {:name "note"
                                      :value @note-contents
                                      :on-change #(reset! note-contents (-> % .-target .-value))}]
       [:input.add-note--button {:type "submit" :value "Save note"}]])))
