(ns sourdojo.components.add-note
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [reagent.core :as reagent]))

(defn- create-note-event
  [note-contents]
  {:type :note :note note-contents :time (js/Date.)})

(defn render
  []
  (let [note-contents (reagent/atom "")]
    (fn []
      [:form.add-note {:on-submit #((.preventDefault %)
                                    (when-not (string/blank? @note-contents)
                                      (rf/dispatch [:add-note (create-note-event @note-contents)])
                                      (reset! note-contents "")))}
       [:textarea.add-note--textarea {:name "note"
                                      :value @note-contents
                                      :on-change #(reset! note-contents (-> % .-target .-value))}]
       [:input.add-note--button {:type "submit" :value "Save note"}]])))
