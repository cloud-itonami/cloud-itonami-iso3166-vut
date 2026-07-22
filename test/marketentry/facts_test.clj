(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest vut-has-spec-basis
  (let [sb (facts/spec-basis "VUT")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "VUT")))
    (is (some? (facts/corporate-registry-spec-basis "VUT")))
    (is (some? (facts/foreign-investment-spec-basis "VUT")))
    (is (some? (facts/procurement-threshold-spec-basis "VUT")))))

(deftest vut-rep-spec-basis-is-honestly-nil
  (testing "no local-representative/agent section number could be confirmed this session"
    (is (nil? (facts/rep-spec-basis "VUT")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "VUT")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "VUT" all)))
    (is (not (facts/required-evidence-satisfied? "VUT" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["VUT" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest procurement-threshold-spec-basis-criteria
  (let [pt (facts/procurement-threshold-spec-basis "VUT")]
    (is (= 10000000.0 (get-in pt [:procurement-threshold-criteria :low-value-procurement-threshold-vt])))))

(deftest corporate-registry-spec-basis-confirms-dual-role
  (testing "VFSC's own legislation page confirms ONE body covers both domestic and international companies"
    (let [cr (facts/corporate-registry-spec-basis "VUT")]
      (is (some? cr))
      (is (re-find #"Companies Act No\.25 of 2012" (:corporate-registry-legal-basis cr)))
      (is (re-find #"International Companies Act" (:corporate-registry-legal-basis cr))))))
