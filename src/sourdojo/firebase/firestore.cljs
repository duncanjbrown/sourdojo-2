(ns sourdojo.firebase.firestore
  (:require [firebase.firestore]
            [re-frame.core :as rf]))

(defn set-doc
  ([path data]
   (set-doc path data nil))
  ([path data hook]
   (.then (.set (.doc (.firestore js/firebase) path) (clj->js data))
          #(rf/dispatch [:firestore-ok :set path hook]))))

(defn add-doc
  ([path data]
   (add-doc path data nil))
  ([path data hook]
   (.then (.add (.collection (.firestore js/firebase) path) (clj->js data))
          #(rf/dispatch [:firestore-ok :add (.-id %) hook]))))
