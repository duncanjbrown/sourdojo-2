(ns sourdojo.firebase.auth
  (:require ["firebase/auth"]
            ["firebase/app" :as firebase]
            [re-frame.core :as rf]))

(defn- firebase-user->user [firebase-user]
  {:id (.-uid firebase-user)})

(defn- auth-handler [user]
  (if user
    (rf/dispatch [:signed-in (firebase-user->user user)])
    (rf/dispatch [:signed-out])))

(defn init! []
  (.onAuthStateChanged (.auth firebase) auth-handler))

(defn sign-in []
  (.signInAnonymously (.auth firebase)))

(defn sign-out []
  (.signOut (.auth firebase)))

(defn sign-in-with-google []
  (let [provider (-> (new (.-GoogleAuthProvider (.-auth firebase)))
                     (.addScope "email"))]
    (-> (.auth firebase)
        (.signInWithPopup provider))))

(defn link-to-google
  []
  (let [provider (-> (new (.-GoogleAuthProvider (.-auth firebase)))
                     (.addScope "email"))]
      (-> (.auth firebase)
          (.-currentUser)
          (.linkWithPopup provider)
          (.then #(rf/dispatch [:user-linked-to-google])))))

