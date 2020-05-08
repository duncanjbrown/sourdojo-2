(ns sourdojo.firebase.core
  (:require ["firebase/app" :as firebase]))

(defn init! [config]
  (let [firebase-config (clj->js config)]
    (.initializeApp firebase firebase-config)))

