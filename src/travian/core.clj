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
  '[travian.villages]
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

(defn tick
  [session path]
  (let [store (parse-and-store session)] 
    (let [strategy (load-file path)] (strategy store)
)))

(defn -main
  [session path]
  (while (= true)                       ;
    (tick session path)
    (Thread/sleep (* 60 1000))
    )
  )
