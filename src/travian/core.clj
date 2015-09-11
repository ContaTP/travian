(ns travian.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]
            ))

(use
  'clojure.pprint
  '[clojure.string :only (split)]
  '[travian.troops :as troops]
  '[travian.request :as request]
  '[travian.vilages]
  )

(defn parse-and-store
  [session]
  (let [data (request/get-all session) store {}]
    (into store
      {
        :session session
        :moves (troops/moves data)
        }
      )))

(defn -main
  [session path]
  (let [store (parse-and-store session)]
    (store (load-file path))
    ))
