(ns sourdojo.components.timeline
  (:require [re-frame.core :as rf]
            [sourdojo.bake-state-machine :as bake-states]
            [cljs-time.coerce :as time]
            [cljs-time.format :as timeformat]))

(defn- format-date
  [jstime]
  (let [datetime (time/from-date jstime)
        formatter (timeformat/formatter "h:mma, d MMMM Y")]
    (timeformat/unparse formatter datetime)))

(defn- event
  [event]
  (cond
    (= :note (:type event))
    [:div
      [:p.timeline-event--date (format-date (:time event))]
      [:p.timeline-event--content (:note event)]]
    (= :photo (:type event))
    [:div
      [:p.timeline-event--date (format-date (:time event))]
      [:img.timeline-event--image {:src (:url event)}]]
    (= :step (:type event))
    [:div
      [:p.timeline-event--date (format-date (:time event))]
      [:h4.timeline-event--title (bake-states/translate (:step event))]]))

(defn render
  [steps]
  (into [:ul#timeline] (map #(vector :li.timeline-event (event %)) (reverse steps))))
