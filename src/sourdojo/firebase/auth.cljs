(ns sourdojo.firebase.auth
  (:require [firebase.auth]
            [re-frame.core :as rf]))

(defn- firebase-user->user [firebase-user]
  {:id (.-uid firebase-user)})

(defn- auth-handler [user]
  (if user
    (rf/dispatch [:signed-in (firebase-user->user user)])
    (rf/dispatch [:signed-out])))

(defn init! []
  (.onAuthStateChanged (.auth js/firebase) auth-handler))

(defn sign-in []
  (.signInAnonymously (.auth js/firebase)))

(defn sign-out []
  (.signOut (.auth js/firebase)))
