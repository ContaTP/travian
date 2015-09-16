(ns travian.core-test
  (:require [clojure.test :refer :all]
            [travian.core :refer :all]))

(defn counter
  []
  (let [c 100]
    (fn [] (dec c)))
  )

(deftest a-test
  (testing "FIXME, I fail."
    (let [f (counter)]
      (do
        (is (= 99 (f)))
        )
      )))
