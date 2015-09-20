(ns travian.market
  (:gen-class)
  (:require
     [travian.parse]
     [travian.request]
   )
)

(use
 '[travian.data :as data]
 '[clojure.algo.generic.functor :only (fmap)]
)

(def fix-int-keywords [:max :inOffers :inTransport :carry])

(defn fix-int
  [data]
  (let [fix-values (map data fix-int-keywords)]
    (let [int-values (map #(Integer/parseInt %) fix-values)]
     (zipmap fix-int-keywords int-values)
)))

(defn extract-id-data
  [item]
  (let [{data :data} item]
    [(keyword (:villageId data)) (into data (fix-int data))])
)

(defn merchants?
  [{name :name}]
  (re-matches #"Merchants:[0-9]+" name)
)

(defn parse
  [data]
  (into {} (map extract-id-data (filter merchants? data)))
)

(defn cargo
  [market]
  (*
   (- (:max market) (apply + (map market [:inOffers :inTransport])))
   (:carry market)
  )
)

(defn cargo-map
  [market-map]
  (fmap cargo market-map)
)

(defn lack [storage to-send]
  into {} (filter (comp neg? val) (merge-with - storage to-send))
)

(defn has [storage to-send] (merge-with + to-send (lack storage to-send)))

(defn cargo-limit
  [send cargo]
  (let [sum (apply + (vals send))]
    (if (>= cargo sum)
      send
      (let [mult (/ sum (float cargo))] (fmap (fn [resource] (int (/ resource mult))) send)))
    ))

(defn send
  [session src dest to-send]
  (dosync
   (let [cargo (src @data/cargos) storage (src @data/storages) sended (cargo-limit (has storage to-send) cargo)]
     (travian.request/send-resources session src dest sended)
     sended
     )
   ))
