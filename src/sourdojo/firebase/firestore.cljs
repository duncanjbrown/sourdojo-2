(ns sourdojo.firebase.firestore
  (:require ["firebase/firestore"]
            ["firebase/app" :as firebase]
            [clojure.string]
            [re-frame.core :as rf]))

(defn set-doc
  ([path data]
   (set-doc path data :firestore-ok))
  ([path data callback-event]
   (.then (.set (.doc (.firestore firebase) path) (clj->js data))
          #(rf/dispatch [callback-event (-> (clojure.string/split path #"/") last)]))))

(defn set-doc-with-merge
  ([path data]
   (set-doc-with-merge path data :firestore-ok))
  ([path data callback-event]
   (.then (.set (.doc (.firestore firebase) path) (clj->js data) (clj->js {:merge true}))
          #(rf/dispatch [callback-event (-> (clojure.string/split path #"/") last)]))))

(defn add-doc
  ([path data]
   (add-doc path data :firestore-ok))
  ([path data callback-event]
   (.then (.add (.collection (.firestore firebase) path) (clj->js data))
          #(rf/dispatch [callback-event (.-id %)]))))

(defn get-doc
  [path callback-event]
  (.then (.get (.doc (.firestore firebase) path))
         #(rf/dispatch [callback-event (js->clj (.data %) :keywordize-keys true)])))

(defn query
  [collection where-clauses callback-event]
  (let [fs-collection (-> (.firestore firebase)
                          (.collection collection))]
    (-> ;; we must use JS .apply here: https://groups.google.com/forum/#!topic/clojurescript/mcPhYFovBqo
     (.apply (.-where fs-collection) fs-collection (apply array where-clauses))
     (.get)
     (.then (fn [snapshot]
              (let [docs (->> (.-docs snapshot)
                              (map #(.data %))
                              (map #(js->clj % :keywordize-keys true)))]
                (rf/dispatch [callback-event docs])))))))
