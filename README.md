# cloud-itonami-iso3166-vut

**VUT**: Republic of Vanuatu.

- Public procurement -- Central Tenders Board (CTB), Ministry of
  Finance and Economic Management (MFEM), Government Contracts and
  Tenders Act [CAP. 245]; a VT 10,000,000 own-text value threshold
  splits a direct Director-General track (s.3(4)) from a
  minister-plus-Board tender-process track (s.3(2)/s.11A(1)(a))
- Vanuatu Financial Services Commission (VFSC) -- confirmed genuinely
  DUAL role, on ONE registry platform: domestic company/business
  registration (Companies Act No. 25 of 2012) **and**
  international/offshore company registration (International Companies
  Act [CAP. 222])
- Vanuatu Investment Promotion Authority (VIPA) -- Foreign Investment
  Approval Certificate (FIAC), Foreign Investment Act, 2019 (Act No. 25
  of 2019)
- Department of Customs and Inland Revenue -- Value Added Tax (15%
  since 2018), Business Licence Act [CAP 249] 2006, Tax Administration
  Act No. 37 of 2018 (s.51 tax clearance certificate)

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as every other `cloud-itonami-iso3166-*` sibling:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Central
  Tenders Board's own Government Contracts and Tenders Act [CAP. 245]
  (own s.3(2)/(4)/s.11A(1)(a) VT 10,000,000 procurement-method value
  threshold, own s.14(2)(e) tax-clearance cross-reference); VFSC's own
  confirmed dual role as Vanuatu's domestic AND international/offshore
  company registrar (Companies Act No. 25 of 2012 + International
  Companies Act [CAP. 222], one registry); VIPA's Foreign Investment
  Act, 2019 / Foreign Investment Approval Certificate (FIAC, citation
  only -- no sectoral gate could be confirmed this session); and the
  Department of Customs and Inland Revenue's VAT (15%), Business
  Licence Act [CAP 249] 2006, and Tax Administration Act No. 37 of
  2018 (s.51 tax clearance). `governor.cljc`'s flagship check
  independently recomputes whether an engagement's own declared
  contract value actually clears the Act's own VT 10,000,000 s.3(4)
  low-value-procurement threshold it claims -- a FLAT-VALUE-THRESHOLD
  check shape, the same family already used for Fiji's own reg 29/reg
  30 split, grounded in the Act's own primary text (fetched and read
  directly, not a delegated/unread number) -- see the namespace
  docstrings for the full research trail and honestly-narrowed scope,
  including facts this iteration could NOT verify (e.g. VIPA's own
  Reserved/Restricted-List sectoral gate, and a local-representative/
  agent section number).
- `src/statute/facts.cljc` -- general-law catalog: the Companies Act
  No. 25 of 2012 (company law) and the Employment Act [CAP 160] (Act 1
  of 1983) (general labour law -- a genuinely GENERAL statute, unlike
  the Tonga sibling catalog's own honestly-narrower Immigration Act
  cross-reference, since Tonga's own catalog could not confirm a
  general Employment/Labour Act this session at all).

Every citation is curl/WebFetch-verified against an official source
(`gov.vu`, `vfsc.vu`, `ctb.gov.vu`, `tradeportal.gov.vu`,
`customsinlandrevenue.gov.vu`); where the LIVE official site could not
be reached this session (`paclii.org` served a Cloudflare bot-detection
challenge for the Employment Act and for confirming no matching
Foreign Investment Act entry in PacLII's 2006 consolidated edition; no
standalone VIPA website could be found under any domain guess tried),
the SAME official document was instead read from a `web.archive.org`
snapshot where one was available -- see `marketentry.facts` /
`statute.facts`'s docstrings for exactly which facts are
live-verified vs. archived-snapshot-verified vs. an honestly-flagged
gap.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Vanuatu:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
