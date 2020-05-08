(ns sourdojo.firebase.firestore
  (:require [firebase.firestore]
            [clojure.string]
            [re-frame.core :as rf]))

(defn set-doc
  ([path data]
   (set-doc path data :firestore-ok))
  ([path data callback-event]
   (.then (.set (.doc (.firestore js/firebase) path) (clj->js data))
          #(rf/dispatch [callback-event (-> (clojure.string/split path #"/") last)]))))

(defn set-doc-with-merge
  ([path data]
   (set-doc-with-merge path data :firestore-ok))
  ([path data callback-event]
   (.then (.set (.doc (.firestore js/firebase) path) (clj->js data) (clj->js {:merge true}))
          #(rf/dispatch [callback-event (-> (clojure.string/split path #"/") last)]))))

(defn add-doc
  ([path data]
   (add-doc path data :firestore-ok))
  ([path data callback-event]
   (.then (.add (.collection (.firestore js/firebase) path) (clj->js data))
          #(rf/dispatch [callback-event (.-id %)]))))

(defn get-doc
  [path callback-event]
  (.then (.get (.doc (.firestore js/firebase) path))
         #(rf/dispatch [callback-event (js->clj (.data %) :keywordize-keys true)])))
