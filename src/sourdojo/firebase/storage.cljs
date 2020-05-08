(ns sourdojo.firebase.storage
  (:require ["firebase/storage"]
            ["firebase/app" :as firebase]
            [re-frame.core :as rf]))

(defn- storage-root
  []
  (.ref (.storage firebase)))

(defn save-image
  [filename file]
  (let [storage-ref (.child (storage-root) filename)]
    (.put storage-ref file)))

(defn load-image-url
  [filename callback-event]
  (let [storage-ref (.ref (.storage firebase) filename)]
    (-> (.getDownloadURL storage-ref)
        (.then (fn [url]
                 (rf/dispatch [callback-event filename url]))))))
