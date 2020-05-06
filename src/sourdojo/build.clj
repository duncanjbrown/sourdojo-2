(ns sourdojo.build
  (:require [aero.core :as aero]
            [sourdojo.env :as env]
            [shadow.cljs.devtools.config :as shadow-config]
            [shadow.cljs.devtools.api :as shadow]))

;; This file comes from https://gist.github.com/mhuebert/a87795a74bf3f3452e6b032f1c7ee25d
;; I have changed the build name :browser to :app and :dist as that's the convention in this project

;;;;;;;;;;;;;;;;;;;
;; Build commands
;;
;; these are to be run from the command line, with a release-flag parameter:
;; $ shadow-cljs clj-run app.build/release staging

(defn release
  ([] (release "local"))
  ([release-flag]
   (shadow/release* (-> (shadow-config/get-build! :dist)
                        ;; note, we add ::release-flag to our build-config, we need this later.
                        (assoc ::release-flag release-flag)) {})))

(defn watch
  "Watch the :app build, reloading on changes."
  {:shadow/requires-server true}
  ([] (watch "local"))
  ([release-flag]
   (shadow/watch (-> (shadow-config/get-build! :app)
                     (assoc ::release-flag release-flag)))))

;;;;;;;;;;;;;;;;;;;
;; Reading environment variables
;;
;; We use `juxt/aero` to read a config map, with our `release-flag`
;; passed in as :profile

(defn read-env [release-flag]
  (-> (aero/read-config "config.edn" {:profile release-flag})
      (assoc :release-flag release-flag)))

(defn load-env
  {:shadow.build/stages #{:compile-prepare}}
  [{:as build-state
    :keys [shadow.build/config]}]
  (let [app-env (read-env (-> config ::release-flag keyword))]

    (alter-var-root #'env/config (constantly app-env))

    (-> build-state
        (assoc-in [:compiler-options :external-config ::env] app-env))))
