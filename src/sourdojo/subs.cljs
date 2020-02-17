(ns sourdojo.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 :current-bake
 (fn [db _]
   (:current-bake db)))

(reg-sub
 :current-state
 :<- [:current-bake]
 (fn [current-bake _]
   (:state current-bake)))