(ns travian.villages
  (:gen-class))

(use 'travian.parse
     '[clojure.algo.generic.functor :only (fmap)]
)

(defn own-name?
  [{name :name}]
  (= name "Collection:Village:own"))

(use
  'clojure.pprint
  '[clojure.string :only (split)])

(defn id-village [village] [(keyword (:villageId village)) village])

(defn map-id-village
  [villages]
  (into {} (map id-village villages)))

(defn own
  [data]
  (let [raw (filter own-name? data)]
    (let [[[& villages]] (filter #(not (empty? %)) (map travian.parse/extract raw))]
      (map-id-village villages))))

(defn resources 
  [own]
  (fmap (fn [item] (:storage item)) own)
)
