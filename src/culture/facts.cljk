(ns culture.facts
  "Country-level regional-culture catalog for Vanuatu (VUT) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"VUT"
   [{:culture/id "vut.dish.laplap"
     :culture/name "Laplap"
     :culture/country "VUT"
     :culture/kind :dish
     :culture/summary "National dish of Vanuatu: a baked pudding of grated yam, banana, manioc or taro mixed with coconut milk and salt, wrapped in banana leaves and baked in an underground stone oven."
     :culture/url "https://en.wikipedia.org/wiki/Laplap"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "vut.dish.simboro"
     :culture/name "Simboro"
     :culture/country "VUT"
     :culture/kind :dish
     :culture/summary "Vanuatuan dish of a steamed roll of grated banana, manioc, yam, taro or flour wrapped in banana leaves and covered in coconut milk."
     :culture/url "https://en.wikipedia.org/wiki/Vanuatuan_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "vut.dish.bunya"
     :culture/name "Bunya"
     :culture/country "VUT"
     :culture/kind :dish
     :culture/summary "Vanuatuan feast dish (also called bunia/bounga) made by wrapping meat, vegetables and coconut milk in a bundle of banana leaves and steaming it with hot stones or an underground oven."
     :culture/url "https://en.wikipedia.org/wiki/Vanuatuan_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "vut.beverage.kava"
     :culture/name "Kava"
     :culture/country "VUT"
     :culture/kind :beverage
     :culture/summary "Ceremonial beverage that originated in northern Vanuatu, domesticated by farmers around 3,000 years ago; consumed at the nakamal, the traditional central meeting place, and subject to strict national export standards requiring noble cultivars."
     :culture/url "https://en.wikipedia.org/wiki/Kava"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "vut.beverage.tusker"
     :culture/name "Tusker"
     :culture/country "VUT"
     :culture/kind :beverage
     :culture/summary "Lager beer produced in Port Vila, Vanuatu, often considered the country's national beer and holding roughly 70% domestic market share."
     :culture/url "https://en.wikipedia.org/wiki/Tusker_(Vanuatu_beer)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "vut.festival.land-diving"
     :culture/name "Land diving"
     :culture/country "VUT"
     :culture/kind :festival
     :culture/summary "Ritual performed by men on Pentecost Island, jumping from wooden towers 20-30 meters high with two tree vines wrapped around the ankles, serving as a rite of passage believed to ensure a bountiful yam harvest."
     :culture/url "https://en.wikipedia.org/wiki/Land_diving"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "vut.heritage.chief-roi-matas-domain"
     :culture/name "Chief Roi Mata's Domain"
     :culture/country "VUT"
     :culture/kind :heritage
     :culture/summary "UNESCO World Heritage Site (inscribed 2008) comprising three sites on the islands of Efate, Lelepa and Eretoka associated with 17th-century paramount chief Roi Mata: his residence, the site of his death, and his mass burial site."
     :culture/url "https://en.wikipedia.org/wiki/Roi_Mata"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-vut culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "VUT"))
                 " VUT entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
