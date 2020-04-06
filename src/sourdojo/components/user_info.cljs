(ns sourdojo.components.user-info
  (:require [sourdojo.firebase.auth :as firebase-auth]))

(defn render
  [user]
  (if user
    [:p (str "User ID: " (:id user))]
    [:p "Not logged in "
     [:a {:href "#" :on-click #(firebase-auth/sign-in)} "Sign in"]]))
