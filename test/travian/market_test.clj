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

(deftest cargo-test
  (testing "fix-ints"
    (is (=
         {:max 11, :inOffers 23, :inTransport 13, :carry 4}
         (fix-int {:max "11" :inOffers "23" :inTransport "13" :carry "4"})))
    )
  (testing "cargo"
    (is (= 11000 (cargo {:max 11 :inOffers 0 :carry 1000 :inTransport 0})))
    (is (= 10000 (cargo {:max 11 :inOffers 0 :carry 1000 :inTransport 1})))
    (is (= 5000 (cargo {:max 11 :inOffers 5 :carry 1000 :inTransport 1})))
   )
   (testing "full"
    (is (= {:1 500 :2 500 :3 500 :4 500}
           (has {:1 500 :2 500 :3 500 :4 500} {:1 500 :2 500 :3 500 :4 500}))))
  (testing "partial"
    (is (= {:1 0 :2 0 :3 0 :4 500} (has {:1 0 :2 0 :3 0 :4 500} {:1 500 :2 500 :3 500 :4 500})))
    (is (= {:1 500 :2 3 :3 0 :4 500} (has {:1 506 :2 3 :3 0 :4 500} {:1 500 :2 500 :3 500 :4 500})))
    )
  (testing "bigger"
    (is (= {:1 500 :2 500 :3 500 :4 500} (has {:1 1000 :2 1000 :3 1000 :4 500} {:1 500 :2 500 :3 500 :4 500})))
    )
)

(deftest cargo-limit-test
  (testing "full"
    (is (= {:1 500 :2 500 :3 500 :4 500} (cargo-limit {:1 500 :2 500 :3 500 :4 500} 2000)))
    )
  (testing "half"
    (is (= {:1 250 :2 250 :3 250 :4 250} (cargo-limit {:1 500 :2 500 :3 500 :4 500} 1000)))
    )
  (testing "bigger"
    (is (= {:1 250 :2 250 :3 250 :4 250} (cargo-limit {:1 250 :2 250 :3 250 :4 250} 5000)))
    )
  )
