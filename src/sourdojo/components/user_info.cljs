(ns sourdojo.components.user-info
  (:require [sourdojo.firebase.auth :as firebase-auth]))

(defn render
  [user]
  (println user)
  [:div.current-user
   (cond
     (map? user)
     [:p (str "Signed in. ID: " (:id user) " ")
      [:a {:href "#" :on-click #(firebase-auth/sign-out)} "Sign out"]
      [:a {:href "#" :on-click #(firebase-auth/link-to-google)} "Link to Google"]]
     (nil? user)
     [:p "Not signed in. "
      [:a {:href "#" :on-click #(firebase-auth/sign-in)} "Create an account"]
      [:a {:href "#" :on-click #(firebase-auth/sign-in-with-google)} "Sign in with Google"]]
     (nil? user)
     nil)])
