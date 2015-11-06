(ns travian.core-test
  (:require [clojure.test :refer :all]
            [travian.core :refer :all]
            [travian.data]))

(defn counter
  []
  (let [c 100]
    (fn [] (dec c)))
  )

(deftest filter-raw-test
  (testing "filter"
    (is (=
         (filter-raw [{:name 1} {:name 2}] [{:name 2} {:name 3}])
         [{:name 1}]))
    ))

(deftest update-raw-test
  (dosync
  (testing "update raw"
    (ref-set travian.data/raw [])
    (update-raw [{:name "some:value"} {:name "some:other"}])
    (update-raw [{:name "some:value"} {:name "some:other"}])
    (is (= @travian.data/raw [{:name "some:value"} {:name "some:other"}]))
    (update-raw [{:name "some:new"}])
    (is (= @travian.data/raw [{:name "some:value"} {:name "some:other"} {:name "some:new"}]))
    )))
