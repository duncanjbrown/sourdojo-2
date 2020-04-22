(ns sourdojo.components.timeline
  (:require [re-frame.core :as rf]
            [sourdojo.components.cursor :as cursor]
            [sourdojo.bake-state-machine :as bake-states]
            [reagent.core :as reagent]
            [cljs-time.coerce :as time]
            [cljs-time.format :as timeformat]
            ["react-flip-move" :as flip-move]))

(defn- format-date
  [jstime]
  (let [datetime (time/from-date jstime)
        formatter (timeformat/formatter "h:mma, d MMMM Y")]
    (timeformat/unparse formatter datetime)))

(defn- event
  [event]
  (case (:type event)
    :note
    [:div
      [:p.timeline-event--date (format-date (:time event))]
      [:p.timeline-event--content (:note event)]]
    :photo
    [:div
      [:p.timeline-event--date (format-date (:time event))]
      [:img.timeline-event--image {:src (:url event)}]]
    :step
    [:div
      [:p.timeline-event--date (format-date (:time event))]
      [:h4.timeline-event--title (bake-states/translate (:step event))]]))

(defn render
  [steps current-state]
  [:ul#timeline
    [(reagent/adapt-react-class flip-move) {:enterAnimation "fade"
                                            :leaveAnimation "fade"}
      (for [s steps]
        ^{:key (:time s)} [:li.timeline-event (event s)])
      ^{:key "cursor"} [cursor/render current-state]]])
