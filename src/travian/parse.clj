(ns travian.parse
  (:gen-class))


(defn parse-int [s] (Integer/parseInt s))

(defn name [i] (:name i))

(defn to-int-if-string
  [s]
  (if (string? s) (parse-int s) s))

(defn extract-data [{data :data}] data)

(defn extract [{{cache :cache} :data}] (map extract-data cache))
