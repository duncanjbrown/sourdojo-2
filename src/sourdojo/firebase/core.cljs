(ns sourdojo.firebase.core
  (:require [firebase.app]))

(defn init! [config]
  (let [firebase-config (clj->js config)]
    (.initializeApp js/firebase firebase-config)))
