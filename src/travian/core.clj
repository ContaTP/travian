(ns travian.core
  (:gen-class)
  (:require
   [clj-http.client :as client]
   [clojure.tools.logging :as log]
   [travian.request]
   [travian.buildings]
   [travian.data]
   [travian.troops]
   [travian.villages]
   [travian.market]
   [travian.parse]
   ))

(use
  'clojure.pprint
  '[clojure.string :only (split)]
  )

(defn process
  []
  (let [raw @travian.data/raw]
    (ref-set travian.data/moves (travian.troops/moves raw))
    (ref-set travian.data/buildings (travian.buildings/parse-buildings raw))
    (alter travian.data/queue into (travian.buildings/parse-queue raw))
    (alter travian.data/villages into (travian.villages/own raw))
    (alter travian.data/storages into (travian.villages/resources @travian.data/villages))
    (alter travian.data/market into (travian.market/parse raw))
    (alter travian.data/cargos into (travian.market/cargo-map @travian.data/market))
    ))

(defn filter-raw
  [old new]
  (let [new-names (map travian.parse/name new)]
    (filter #(not (contains? (set new-names) (travian.parse/name %))) old)
  ))

(defn update-raw
  [items]
  (let [raw @travian.data/raw]
    (ref-set travian.data/raw (concat (filter-raw raw items) items))
    ))

(defn update-data
  [items]
  (update-raw items)
  (process)
  (log/info "raw" (count @travian.data/raw) "moves" (count @travian.data/moves))
)

(defn set-village-session
  [session villages]
  (alter travian.data/sessions into (map array-map villages (repeat (count villages) session)))
)

(defn parse-and-store
  [session]
  (dosync
   (let [data (travian.request/get-all session) villages (keys (travian.villages/own data))]
     (update-data data)
     (set-village-session session villages)
     (update-data (travian.request/get-market session villages))
     )))

(defn tick
  [path]
  (let [strategy (load-file path)] (strategy)
))

(defn -main
  [path]
  (while (= true)
    (tick path)
    (Thread/sleep (* 60 1000))
    )
  )
