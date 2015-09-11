(ns travian.troops
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log])
  )

(use
  'travian.parse
  '[travian.request :as request]
  )

(defn moving?
  [{name :name}]
  (re-matches #"Collection:Troops:moving:[0-9]+" name))

(defn moves
  [data]
  (let [troops (flatten (map travian.parse/extract (filter moving? data)))]
    troops))

(defn raid
  [session src dest units]
  (request/send-troops session 4 src dest units))

(defn go-to-or-from
  [village-id]
  (fn
    [move]
    (let [{:keys [movement]} move]
      (or
        (= village-id (:villageIdTarget movement))
        (= village-id (:villageIdStart movement))
        ))))

(defn farm
  [store src dest units & [description]]
  (let [description (or description dest)]
    (let [active (filter (go-to-or-from dest) (:moves store))]
      (if
        (empty? active)
        (do
          (log/info "attack" description units)
          (raid (:session store) src dest units)
          )
        ))))

(defn units
  [n]
  (let [units {:1 2 :2 0 :3 0 :4 0 :5 0 :6 0 :7 0 :8 0 :9 0 :10 0 :11 0}]
    (conj units n)))
