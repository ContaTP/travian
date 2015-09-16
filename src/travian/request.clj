(ns travian.request
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]
            ))

(def headers
  {:accept-language "en-USen;q=0.8ru;q=0.6"
   :Content-Type "application/json;charset=UTF-8"
   :Connection "keep-alive"
   :accept "application/json text/plain */*"
   :Accept-Encoding "gzip deflate"})

(defn parse-data
  [{body :body}]
  (let [data (:cache body)]
    data))

(defn api
  [session action controller params]
  (log/debug session action controller params)
  (let [url (str "http://ks4-ru.travian.com/api/?c=" controller "&a=" action)]
    (let [body {:session session :action action :controller controller :params params}]
      (let [request (client/post url {:headers headers :content-type :json :form-params body :as :json})]
        (log/debug request)
        (parse-data request)
      ))))

(defn cache
  [session names]
  (api session "get" "cache" {:names names})
)

(defn get-all
  [session]
  (parse-data (api session "getAll" "player" {})))

(defn send-troops-params
  [movement-type src dest units]
  {:destVillageId dest
   :villageId src
   :movementType movement-type
   :redeployHero false
   :units units
   :spyMission "resources"})

(defn send-troops
  [session movement-type src dest units]
  (api session "send" "troops" (send-troops-params movement-type src dest units)))
