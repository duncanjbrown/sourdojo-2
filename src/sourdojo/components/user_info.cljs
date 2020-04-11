(ns sourdojo.components.user-info
  (:require [sourdojo.firebase.auth :as firebase-auth]))

(defn render
  [user]
  [:div.current-user
    (cond
      (map? user)
      [:p (str "Signed in. ID: " (:id user) " ")
        [:a {:href "#" :on-click #(firebase-auth/sign-out)} "Sign out"]]
      (false? user)
      [:p "Not signed in. "
        [:a {:href "#" :on-click #(firebase-auth/sign-in)} "Sign in"]]
      (nil? user)
      nil)])
