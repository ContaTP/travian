(ns travian.market
  (:gen-class)
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

(defn parse
  [data]
  (into {} (map extract-id-data data))
)

(defn can-send
  [market]
  (*
   (- 
    (:max market)
    (apply + (map market [:inOffers :inTransport]))
    )
   (:carry market)
  )
)
