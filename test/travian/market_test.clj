(ns travian.market-test
  (:require [clojure.test :refer :all]
            [travian.market :refer :all]))

(def stub-data 
  [
   {:name "Merchants:535379974", :data {:villageId "535379974", :max "11", :inOffers "0", :inTransport "0", :carry "1000", :speed "12"}}
   {:name "Merchants:535281680", :data {:villageId "535281680", :max "5", :inOffers "0", :inTransport "0", :carry "1000", :speed "12"}}
   ]
)

(deftest parse-test
  (testing "parse stub data"
    (is 
     (= {
         :535379974 {:villageId "535379974", :max 11, :inOffers 0, :inTransport 0, :carry 1000, :speed "12"}
         :535281680 {:villageId "535281680", :max 5, :inOffers 0, :inTransport 0, :carry 1000, :speed "12"}
         }
        (parse stub-data)
        ))
    )
  )

(deftest can-send-test
  (testing "fix-ints"
    (is (= 
         {:max 11, :inOffers 23, :inTransport 13, :carry 4} 
         (fix-int {:max "11" :inOffers "23" :inTransport "13" :carry "4"})))
    )
  (testing "can-send"
    (is (= 11000 (can-send {:max 11 :inOffers 0 :carry 1000 :inTransport 0})))
    (is (= 10000 (can-send {:max 11 :inOffers 0 :carry 1000 :inTransport 1})))
    (is (= 5000 (can-send {:max 11 :inOffers 5 :carry 1000 :inTransport 1})))
   )
  )
