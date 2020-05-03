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
