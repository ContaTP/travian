(ns travian.core
  (:gen-class)
  (:require
   [clj-http.client :as client]
   [clojure.tools.logging :as log]
   [travian.request]
   [travian.data]
   [travian.troops]
   [travian.villages]
   [travian.market]
   ))

(use
  'clojure.pprint
  '[clojure.string :only (split)]
  )

(defn process
  []
  (let [raw @travian.data/raw]
    (alter travian.data/moves into (travian.troops/moves raw))
    (alter travian.data/villages into (travian.villages/own raw))
    (alter travian.data/storages into (travian.villages/resources @travian.data/villages))
    (alter travian.data/market into (travian.market/parse raw))
    (alter travian.data/cargos into (travian.market/cargo-map @travian.data/market))
    ))

(defn update-data
  [items]
  (ref-set travian.data/raw [])
  (alter travian.data/raw into items)
  (process)
)

(defn set-village-session
  [session villages]
  (alter travian.data/sessions into (map array-map villages (repeat (count villages) session)))
)

(defn parse-and-store
  [session]
  (dosync
   (let [data (travian.request/get-all session)]
     (update-data data)
     (set-village-session session (keys @travian.data/villages))
     )))

(defn tick
  [session path]
  (parse-and-store session)
  (let [strategy (load-file path)] (strategy)
))

(defn -main
  [session path]
  (while (= true)
    (tick session path)
    (Thread/sleep (* 60 1000))
    )
  )
