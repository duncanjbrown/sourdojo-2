(ns sourdojo.env
  #?(:cljs (:require-macros [sourdojo.env :as env])))

;; This file comes from https://gist.github.com/mhuebert/a87795a74bf3f3452e6b032f1c7ee25d

(def config
  "Map of environment variables, to be read at runtime."
  #?(:cljs (env/get-config-map)
     :clj  {}))

#?(:clj
    (defmacro ^:private get-config-map
       "Returns config map at compile time"
       []
       config))
