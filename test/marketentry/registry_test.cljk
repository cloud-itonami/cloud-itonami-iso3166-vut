(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "VUT" 0)
        s (registry/register-submit "eng-1" "VUT" 0)]
    (is (= "VUT-DFT-000000" (get d "draft_number")))
    (is (= "VUT-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "VUT" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest low-value-procurement-eligible-below-threshold
  (testing "a declared contract value below VT 10,000,000 (Government Contracts and Tenders Act s.3(4)) is low-value-eligible"
    (is (true? (registry/low-value-procurement-eligible? {:declared-contract-value 9999999.0})))
    (is (true? (registry/low-value-procurement-eligible? {:declared-contract-value 1.0})))))

(deftest low-value-procurement-ineligible-at-or-above-threshold
  (testing "s.3(2)/s.11A(1)(a): VT 10,000,000 or above requires the Central Tenders Board, not the direct s.3(4) track"
    (is (false? (registry/low-value-procurement-eligible? {:declared-contract-value 10000000.0})))
    (is (false? (registry/low-value-procurement-eligible? {:declared-contract-value 15000000.0})))))

(deftest low-value-procurement-eligible-missing-value-fails-honestly
  (testing "a missing declared value never silently passes"
    (is (false? (registry/low-value-procurement-eligible? {:declared-contract-value nil})))
    (is (false? (registry/low-value-procurement-eligible? {})))))

(deftest low-value-procurement-ineligible-claim-is-entity-scope-gated
  (testing "an engagement NOT claiming the low-value track is never flagged, even if its value would fail"
    (is (false? (registry/low-value-procurement-ineligible-claim?
                 {:claims-low-value-procurement? false :declared-contract-value 15000000.0}))))
  (testing "an engagement claiming the low-value track while its own declared value sits at/above the threshold IS flagged"
    (is (true? (registry/low-value-procurement-ineligible-claim?
                {:claims-low-value-procurement? true :declared-contract-value 15000000.0}))))
  (testing "an engagement claiming the low-value track with a genuinely below-threshold value is NOT flagged"
    (is (false? (registry/low-value-procurement-ineligible-claim?
                 {:claims-low-value-procurement? true :declared-contract-value 4000000.0})))))

;; ---------------------------------------------------------------------------
;; Money is compared at money precision, not at double precision
;; ---------------------------------------------------------------------------

(deftest whole-unit-fees-were-already-correct-and-stay-correct
  (testing "the seeded shape: base + rate x months in whole currency units"
    (is (registry/engagement-fee-matches-claim?
         {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
           :claimed-fee 860000.0}))))

(deftest cent-denominated-fees-are-no-longer-rejected-while-correct
  (testing "`(== (double claimed) (+ (double base) (* (double rate) (double months))))`
            rejected CORRECT totals once an amount carried cents -- 40,989 of
            327,060 combinations (12.5%), against 0 of 327,060 in whole units"
    (let [bad (for [m (range 1 37)
                    bc (range 10000 90000 2100)
                    rc (range 500 6000 210)
                    :let [truth (/ (+ bc (* rc m)) 100.0)]
                    :when (not (registry/engagement-fee-matches-claim?
                                {:base-fee (/ bc 100.0) :monthly-rate (/ rc 100.0)
                                  :monitoring-months m :claimed-fee truth}))]
                [m (/ bc 100.0) (/ rc 100.0) truth])]
      (is (empty? bad) (str "false rejections: " (count bad) " e.g. " (first bad))))))

(deftest a-genuinely-wrong-fee-is-still-caught
  (testing "rounding to money precision must not blunt the check"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.01})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 859999.99})))))

(deftest an-unverifiable-fee-never-matches
  (testing "un-verifiable is not the same as correct, and not a crash"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee "500000" :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.0})))
    (is (nil? (registry/compute-engagement-fee {:base-fee 500000 :monthly-rate 30000})))))
