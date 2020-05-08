(ns sourdojo.firebase.storage
  (:require [firebase.storage]
            [re-frame.core :as rf]))

(defn- storage-root
  []
  (.ref (.storage js/firebase)))

(defn save-image
  [filename file]
  (let [storage-ref (.child (storage-root) filename)]
    (.put storage-ref file)))

(defn load-image-url
  [filename callback-event]
  (let [storage-ref (.ref (.storage js/firebase) filename)]
    (-> (.getDownloadURL storage-ref)
        (.then (fn [url]
                 (rf/dispatch [callback-event filename url]))))))
