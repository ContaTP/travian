(ns travian.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]
            ))

(use
  'clojure.pprint
  '[clojure.string :only (split)]
  '[travian.troops :as troops]
  '[travian.data :as data]
  '[travian.request :as request]
  '[travian.villages]
  )

(defn parse-and-store
  [session]
  (dosync
   (let [data (request/get-all session)]
     (alter data/villages into (travian.villages/own data))
     (alter data/storages into (travian.villages/resources @data/villages))
     (alter data/market into (travian.market/parse (travian.request/get-market session (keys @data/villages))))
     (alter data/cargos into (travian.market/cargo-map @data/market))
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
