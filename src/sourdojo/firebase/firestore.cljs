(ns sourdojo.firebase.firestore
  (:require [firebase.firestore]
            [re-frame.core :as rf]))

(defn set-doc
  ([path data]
   (set-doc path data nil))
  ([path data hook]
   (.then (.set (.doc (.firestore js/firebase) path) (clj->js data))
          #(rf/dispatch [:firestore-ok :set path hook]))))

(defn set-doc-with-merge
  ([path data]
   (set-doc-with-merge path data nil))
  ([path data hook]
   (.then (.set (.doc (.firestore js/firebase) path) (clj->js data) (clj->js {:merge true}))
          #(rf/dispatch [:firestore-ok :set path hook]))))

(defn add-doc
  ([path data]
   (add-doc path data nil))
  ([path data hook]
   (.then (.add (.collection (.firestore js/firebase) path) (clj->js data))
          #(rf/dispatch [:firestore-ok :add (.-id %) hook]))))

(defn get-doc
  [path hook]
  (.then (.get (.doc (.firestore js/firebase) path))
         #(rf/dispatch [:firestore-get-ok (js->clj (.data %) :keywordize-keys true) hook])))
