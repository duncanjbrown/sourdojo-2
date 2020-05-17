(ns sourdojo.components.timeline
  (:require [re-frame.core :as rf]
            [sourdojo.components.cursor :as cursor]
            [sourdojo.bake-state-machine :as bake-states]
            [reagent.core :as reagent]
            [cljs-time.core :as time]
            [cljs-time.format :as timeformat]
            ["react-flip-move" :as flip-move]))

(defn- stringify-interval
  [interval])

(time/default-time-zone)
(time/now)
(time/now)

(def interval (time/interval (time/from-default-time-zone (time/date-time 2020 5 17 20)) (time/to-default-time-zone (time/now))))

(time/in-hours interval)

(defn- format-date
  [jstime]
  ()
  (let [datetime (time/from-default-time-zone jstime)
        formatter (timeformat/formatter "h:mma")
        interval (-> time/now
                     (time/interval datetime))]
    (timeformat/unparse formatter datetime)))

(defn- photo-src
  [{:keys [filename]}]
  (let [urls @(rf/subscribe [:photo-urls])]
    (get urls filename)))

(defn- event
  [event]
  (case (:type event)
    :note
    [:li.timeline-event
     [:div.timeline-event--content
      [:p.timeline-event--date (format-date (:time event))]
      [:p.timeline-event--note (:note event)]]]
    :photo
    [:li.timeline-event
     [:div.timeline-event--content
      [:p.timeline-event--date (format-date (:time event))]
      [:img.timeline-event--image {:src (photo-src event)}]]]
    :step
    [:li.timeline-event
     [:div.timeline-event--content
      [:p.timeline-event--date (format-date (:time event))]
      [:h4.timeline-event--title (bake-states/translate (:step event))]]]))

(defn render
  [steps current-state]
  [:ul#timeline
   (into [(reagent/adapt-react-class flip-move) {:enterAnimation "fade"
                                                 :leaveAnimation "fade"}]
         (conj []
               (doall (for [s steps]
                        ^{:key (:time s)} (event s)))
               ^{:key "cursor"} [cursor/render current-state]))])
