(ns travian.troops
  (:gen-class)
  (:require
   [clj-http.client :as client]
   [clojure.tools.logging :as log]
   [travian.parse]
   [travian.request]
   [travian.data]
   ))


(defn moving?
  [{name :name}]
  (re-matches #"Collection:Troops:moving:[0-9]+" name))

(defn moves
  [data]
  (let [troops (flatten (map travian.parse/extract (filter moving? data)))]
    troops))

(defn extract-resourses [{{resources :resources} :movement}] resources)

(defn move-resourses
  [moves]
  (apply merge-with + (map extract-resourses moves))
)

(defn raid
  [session src dest units]
  (travian.request/send-troops session 4 src dest units))

(defn go-to-or-from
  [village-id]
  (fn
    [move]
    (let [{:keys [movement]} move]
      (or
        (= village-id (:villageIdTarget movement))
        (= village-id (:villageIdStart movement))
        ))))

(defn go-to
  [village-id]
  (fn
    [move]
    (let [{:keys [movement]} move]
      (= village-id (:villageIdTarget movement))
      )))

(defn go-to-moves
  [village-id]
  (filter (travian.troops/go-to (name village-id)) @travian.data/moves))

(def go-to-resource (comp travian.troops/move-resourses travian.troops/go-to-moves))


(def filter-map {:to-from go-to-or-from :to go-to})

(defn farm
  [farm-type src dest units & [description movement-type]]
  (let [description (or description dest)
        session (src @travian.data/sessions)
        movement-type (or movement-type 4)
        ]
    (let [active (filter ((farm-type filter-map) dest) @travian.data/moves)]
      (if
        (empty? active)
        (do
          (log/info description src dest movement-type units)
          (travian.request/send-troops session movement-type src dest units)
          )
        ))))

(defn units
  [n]
  (let [units {:1 0 :2 0 :3 0 :4 0 :5 0 :6 0 :7 0 :8 0 :9 0 :10 0 :11 0}]
    (conj units n)))
