(ns sourdojo.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :user
 (fn [db _]
   (:user db)))

(reg-sub
 :current-bake
 (fn [db _]
   (:current-bake db)))

(reg-sub
 :current-state
 :<- [:current-bake]
 (fn [current-bake _]
   (:state current-bake)))

(reg-sub
 :steps
 :<- [:current-bake]
 (fn [current-bake _]
   (:steps current-bake)))

(reg-sub
 :photo-urls
 (fn [db _]
   (:photo-urls db)))

(reg-sub
 :past-bakes
 (fn [db _]
   (:past-bakes db)))
