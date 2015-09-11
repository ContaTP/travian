(ns travian.vilages
  (:gen-class))

(use 'travian.parse)

(defn own-name?
  [{name :name}]
  (= name "Collection:Village:own"))

(use
  'clojure.pprint
  '[clojure.string :only (split)])

(defn id-village [village] [(keyword (:villageId village)) village])

(defn map-id-village
  [vilages]
  (into {} (map id-village vilages)))

(defn own
  [data]
  (let [raw (filter own-name? data)]
    (let [[[& vilages]] (filter #(not (empty? %)) (map travian.parse/extract raw))]
      (map-id-village vilages))))