(ns sourdojo.firebase.auth
  (:require [firebase.auth]))

(defn init! []
  (letfn [(handler [user]
            (if user
                (println (.-uid user))
                (println "Logged out")))]
    (.onAuthStateChanged (.auth js/firebase) handler)))

(defn sign-in []
  (.signInAnonymously (.auth js/firebase)))
