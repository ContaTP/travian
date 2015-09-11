(ns travian.parse
  (:gen-class))

(defn extract-data [{data :data}] data)

(defn extract [{{cache :cache} :data}] (map extract-data cache))
