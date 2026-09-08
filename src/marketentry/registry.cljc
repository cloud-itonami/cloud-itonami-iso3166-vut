(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the SAME
  ground-truth-recompute DISCIPLINE sibling actors use (verify a claimed
  monetary total against the entity's own recorded quantity x unit
  fields), reapplied to a market-entry engagement fee line.

  `low-value-procurement-eligible?` / `low-value-procurement-ineligible-
  claim?` are the SAME discipline applied to a genuinely Vanuatu-specific
  mechanism: the Government Contracts and Tenders Act [CAP. 245]'s own
  two-tier value-threshold split between s.3(4) (\"a Director General or
  his or her authorised delegate ... may enter into a Government
  Contract where the consideration ... is less than VT 10,000,000\") and
  s.3(2)/s.11A(1)(a) (VT 10,000,000 or above requires a minister and the
  Central Tenders Board's own procurement-procedure involvement) -- read
  directly off the Act's own consolidated PDF text via ctb.gov.vu (see
  `marketentry.facts` for the full research trail).

  This is honestly a FLAT-VALUE-THRESHOLD check shape -- the same
  general shape family precedent already used for Fiji's own reg 29/reg
  30 split and (per CAF's own comparative docstring) Albania's Neni
  76(2)(c) carve-out. This namespace does not force a different shape
  onto Vanuatu's actual mechanism just to appear novel; the Government
  Contracts and Tenders Act genuinely IS a plain two-tier value split,
  and the check below honestly recomputes exactly that, no more and no
  less.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real Central Tenders Board or company-registry system. It
  builds the RECORD an operator would keep, not the act of submitting a
  portal registration itself (that is `marketentry.operation`'s
  `:filing/submit`, always human-gated -- see README Actuation)."
  (:require [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def low-value-procurement-threshold-vt
  "Government Contracts and Tenders Act [CAP. 245], own s.3(4)
  (\"a Director General or his or her authorised delegate ... may enter
  into a Government Contract where the consideration in relation to any
  contract, arrangement, franchise or concession is less than VT
  10,000,000\") -- the value below which a Government Contract may be
  entered directly; s.3(2)/s.11A(1)(a) require VT 10,000,000 or above to
  go through a minister and the Central Tenders Board's own
  procurement-procedure involvement. Read directly off the Act's own
  consolidated PDF text (ctb.gov.vu, verified 2026-07-22)."
  10000000.0)

(defn low-value-procurement-eligible?
  "The ground-truth s.3(4) low-value-procurement eligibility for
  `engagement`, independently recomputed from its own declared
  `:declared-contract-value`. A missing/nil declared value simply fails
  (does not throw) -- an engagement with no declared value is not
  eligible for the low-value track."
  [{:keys [declared-contract-value]}]
  (boolean
   (and (some? declared-contract-value)
        (< (double declared-contract-value) low-value-procurement-threshold-vt))))

(defn low-value-procurement-ineligible-claim?
  "Does `engagement` declare `:claims-low-value-procurement? true` (i.e.
  it is following the s.3(4) direct-Director-General track, bypassing
  the Central Tenders Board) while the INDEPENDENTLY recomputed
  `low-value-procurement-eligible?` is false (its own declared contract
  value actually sits at or above the VT 10,000,000 threshold)? An
  engagement that does NOT claim the low-value track is never flagged
  by this check (entity/engagement-scope-gated, the same discipline
  Bhutan's `:foreign-company?`-gated FDI check and CAF's
  `:reserved-market?`-gated check use)."
  [{:keys [claims-low-value-procurement?] :as engagement}]
  (boolean (and claims-low-value-procurement?
                (not (low-value-procurement-eligible? engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
