(ns travian.villages-test
  (:require [clojure.test :refer :all]
            [travian.villages :refer :all]))

(def stub-data {:535281680 {:tributeTreasures 0, :treasures "24", :acceptanceProduction "0.68332", :culturePointProduction "139", :treasureResourceBonus "24", :belongsToKing "-1", :storageCapacity {:1 "18000", :2 "18000", :3 "18000", :4 "5000"}, :coordinates {:x "16", :y "-49"}, :supplyBuildings "233", :villageId "535281680", :supplyTroops "570", :availableControlPoints "0", :celebrationType "0", :tributes {:1 0, :2 0, :3 0, :4 0}, :name "Descendant", :tributeTime "0", :treasury {:1 "0", :2 "0", :3 "0", :4 0}, :type "0", :acceptance 100, :playerId "1634", :production {:1 "338", :2 "375", :3 "263", :4 "66"}, :celebrationEnd "0", :culturePoints 1686.1743750015, :isMainVillage false, :tributesRequiredToFetch 0, :treasuresUsable "100", :tributeProduction 0, :population "233", :tribeId "2", :storage {:1 3072.2172222227, :2 6935.0341666657, :3 7108.5088888901, :4 666.19555555544}, :isTown false, :tributesCapacity "18000", :usedControlPoints "0"}, :535379974 {:tributeTreasures 0, :treasures "1292", :acceptanceProduction "8.01658", :culturePointProduction "1003", :treasureResourceBonus "1292", :belongsToKing "-1", :storageCapacity {:1 "55000", :2 "55000", :3 "55000", :4 "14400"}, :coordinates {:x "6", :y "-46"}, :supplyBuildings "1205", :villageId "535379974", :supplyTroops "6099", :availableControlPoints "3", :celebrationType "0", :tributes {:1 0, :2 0, :3 0, :4 0}, :name "Core", :tributeTime "0", :treasury {:1 "0", :2 "0", :3 "0", :4 0}, :type "3", :acceptance 200, :playerId "1634", :production {:1 "1838", :2 "1799", :3 "1400", :4 "121"}, :celebrationEnd "0", :culturePoints 13367.242615754, :isMainVillage true, :tributesRequiredToFetch 0, :treasuresUsable "1500", :tributeProduction 0, :population "1205", :tribeId "2", :storage {:1 8725.9229651655, :2 11633.79399036, :3 8381.4142192906, :4 7059.6427777765}, :isTown true, :tributesCapacity "55000", :usedControlPoints "3"}})

(deftest resources-test
  (testing "get resources" 
    (is
     (= {
         :535281680 {:1 3072.2172222227 :2 6935.0341666657 :3 7108.5088888901 :4 666.19555555544} 
         :535379974 {:1 8725.9229651655 :2 11633.79399036 :3 8381.4142192906 :4 7059.6427777765}
         }
         (resources stub-data)
))))
