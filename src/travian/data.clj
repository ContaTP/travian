(ns travian.data
  (:gen-class)
  )

(def raw (ref []))
(def moves (ref []))
(def buildings (ref []))

(def villages (ref {}))
(def queue (ref {}))
(def storages (ref {}))
(def market (ref {}))
(def cargos (ref {}))
(def sessions (ref {}))
