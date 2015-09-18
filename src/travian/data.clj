(ns travian.data
  (:gen-class)
  )

(def villages (ref {}))
(def storages (ref {}))
(def market (ref {}))
(def cargos (ref {}))
(def session (ref ""))
