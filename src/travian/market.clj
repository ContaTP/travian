(ns travian.market
  (:gen-class)
  (:require
     [travian.parse]
     [travian.troops]
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
    (let [int-values (map travian.parse/parse-int fix-values)]
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

(defn lack
  [& res]
  (fmap - (into {} (filter (comp neg? val) (apply merge-with - res)))))

(defn has [storage to-send] (merge-with - to-send (lack storage to-send)))

(defn cargo-limit
  [send cargo]
  (let [sum (apply + (vals send))]
    (if (>= cargo sum)
      send
      (let [mult (/ sum (float cargo))] (fmap (fn [resource] (int (/ resource mult))) send)))
    ))

(defn resource
  [n]
  (let [resource {:1 0 :2 0 :3 0 :4 0}]
    (conj resource n)))

(defn storage [village-id] (village-id @travian.data/storages))

(defn send
  [src dest to-send limit]
  (let [
        to-send (resource to-send)
        session (src @data/sessions)
        cargo (src @data/cargos)
        storage (src @data/storages)
        sended (cargo-limit (has to-send storage) cargo)]
    (if (>= (apply + (vals sended)) limit)
      (travian.request/send-resources session src dest (resource sended))
      )))


(defn keep
  [src dest to-send limit]
  (let [has (merge-with + (storage dest) (travian.troops/go-to-resource dest))
        need (lack has to-send)]
    (send src dest need limit)
))
