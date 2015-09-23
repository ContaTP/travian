(ns travian.buildings
  (:gen-class)
  (:require
   [travian.villages]
   [travian.request]
   [travian.data]
   ))

(defn building?
  [{name :name}]
  (re-matches #"Collection:Building:[0-9]+" name)
)

(defn queue?
  [{name :name}]
  (re-matches #"BuildingQueue:[0-9]+" name)
)

(defn fix-type
  [building]
  (assoc building
         :villageId (keyword (:villageId building))
         :buildingType (keyword (:buildingType building))
         :locationId (keyword (:locationId building))
         ))

(defn parse-buildings
  [data]
  (map fix-type
       (flatten (map travian.parse/extract (filter building? data)))
       ))

(defn parse-queue
  [data]
  (into {} (map (comp travian.villages/id-village travian.parse/extract-data) (filter queue? data)))
)

(defn building-types-in-village
  [villageId building-types]
  (fn
    [building]
    (and
     (= (:villageId building) villageId)
     (.contains building-types (:buildingType building))
     )
    )
)

(defn upgrade
  [village-id location-id building-type]
  (travian.request/upgrade-building (village-id @travian.data/sessions) village-id location-id building-type)
)

(defn resources-sum [resources] (apply + (vals resources)))

(defn upgrade-cost [building] (:upgradeCosts building))

(def upgrade-cost-sum (comp resources-sum upgrade-cost))

(defn has-resource
  [village-id]
  (let [storage (village-id @travian.data/storages)]
  (fn
    [building]
    (every? true? (apply map >= (map vals [storage (upgrade-cost building)])))
    )
))

(defn building-slot?
  [village-id]
  (let [queue (village-id @travian.data/queue)]
    (> ((comp :1 :freeSlots) queue) 0)
    ))

(defn get-buildings-in-village
  [village-id building-types]
  (filter (building-types-in-village village-id building-types) @travian.data/buildings))

(defn grow-sort
  [building-types]
  (fn [building]
    [(.indexOf building-types (:buildingType building)) (upgrade-cost-sum building)]
    )
)

(defn grow-choice
  [village-id building-types]
  (let [
        buildings (get-buildings-in-village village-id building-types)
        has (filter (has-resource village-id) buildings)
        build (first (sort-by (grow-sort building-types) has))]
    build
    ))

(defn grow
  [village-id building-types]
  (if (building-slot? village-id)
    (when-let [build (grow-choice village-id building-types)]
      (upgrade village-id (:locationId build) (:buildingType build))
      )))
