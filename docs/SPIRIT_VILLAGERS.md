# Spirit Villagers

Spirits retain the stable `mortal_expansion:spirit` entity ID and now use the
vanilla villager model/base texture at 20% opacity. They are passive
`AbstractVillager` merchants with the standard trading menu, merchant inventory,
offer serialization, trade stock, and experience rewards. Existing horizontal
phasing, water avoidance, intangible defenses, and daylight movement rules remain.
They have 18 health and movement speed 0.24. No hostile attack goals are added.

## Workstation

Engrave a placed Grave with **Villager** (case insensitive). Each such grave
claims one adult jobless vanilla villager within four blocks, checked once per
second. Babies, nitwits, villagers currently trading, and employed villagers do
not take this workstation. Other inscriptions have no conversion effect.

Breaking that worker's grave immediately returns it to a jobless villager, even
after it has traded. Its custom name, original biome type/gossip, age, current
health, equipment, and current inventory are retained; its ordinary profession,
career level, XP, and job memory reset. Conversion uses the vanilla entity
conversion API, which assigns a new entity UUID. The grave stores its worker's
UUID and the Spirit stores its workstation position/dimension and original
villager data. One grave cannot repeatedly produce workers. Worker death releases
the claim. An unloaded worker validates its grave when both are loaded again;
unloaded graves are unknown, never treated as destroyed or force-loaded.
Naturally spawned, summoned, and existing saved Spirits have no conversion
provenance and cannot revert through this mechanic.

## Expertise and balanced trades

Spirits start as **Novices**. Completed trades award merchant XP using the
vanilla thresholds: Apprentice at 10, Journeyman at 70, Expert at 150, and
Master at 250 XP. The ordinary trading screen displays expertise and its XP bar.
Previously unlocked offers remain available. Expertise, XP, stock uses, demand,
and catalog version persist across saves; opening the screen does not award XP.

| Expertise | Payment | Result | Daily stock | Merchant XP |
|---|---|---|---|---|
| Novice | 8 Raw Ectoplasm | 1 Emerald | 16 | 2 |
| Novice | 2 Emeralds | 1 Raw Ectoplasm | 12 | 1 |
| Apprentice | 2 Emeralds + 1 Raw Ectoplasm | 8 Ectothread | 12 | 5 |
| Apprentice | 2 Emeralds | 1 Holy Salt | 8 | 5 |
| Journeyman | 4 Emeralds + 1 Pure Ectoplasm | 8 Purified Ectothread | 8 | 10 |
| Journeyman | 4 Pure Ectoplasm | 1 Emerald | 8 | 10 |
| Expert | 3 Emeralds + 5 Ectothread | Ghost Garb Hood | 4 | 15 |
| Expert | 3 Emeralds + 4 Ectothread | Ghost Garb Boots | 4 | 15 |
| Master | 6 Emeralds + 8 Purified Ectothread | Haint Garb Robe | 2 | 30 |
| Master | 5 Emeralds + 7 Purified Ectothread | Haint Garb Leggings | 2 | 30 |

Thread trades preserve the recipe's ectoplasm core and eight-thread yield.
Emeralds replace the eight String plus the merchant's service; crafting with
collected String remains cheaper. Purified thread still requires an actual Pure
Ectoplasm input, so it cannot bypass purification using only Emeralds. Garb
assembly retains every thread required by its normal recipe and charges a fee;
it adds no free enchantments or stats. Pure Ectoplasm earns twice as much per
item as raw ectoplasm. Buying raw ectoplasm then selling it back loses Emeralds.
Holy Salt supply costs two Emeralds per unit, rather than the old four-for-two
bulk offer, and has limited Apprentice stock.

Stock replenishes at most once per new Minecraft day while not trading.
Grave-created workers must be within eight blocks of their loaded grave to
restock; natural Spirits replenish without a grave. Restocking uses vanilla
demand adjustments, so heavily used offers can rise above these base prices.
No automatic discounts reduce the required material count. Catalog balancing is
isolated in `SpiritTrades`.

Old flat-catalog saves migrate once to a fresh balanced Novice catalog with zero
merchant XP; their grave ownership, original villager data, and inventory remain.
They had no saved expertise previously. Current catalog saves preserve all XP,
unlocked trades, and stock. Reverting via a broken grave still produces a
jobless level-one villager with zero XP.

## Compatibility and validation

Mojang's merchant classes/codecs own trade menus and offer/inventory saves.
No new mixins or global vanilla POI registrations are needed: POIs classify
block states and cannot distinguish individual grave block-entity inscriptions.
The small workstation service reads the grave's authoritative inscription and
claim instead. Existing unnamed graves and old Spirit saves default to no claim.
The movement helper is shared with other spirits to avoid diverging rules.

Server GameTests cover real engraving and eligibility, one worker per grave,
actual merchant-menu two-input payment/output/stock, five expertise tiers and XP persistence, recipe ingredient-cost comparisons, legacy catalog migration, provenance/claim
persistence, and immediate reversion after breaking a reloaded workstation.
Client appearance and player interaction still need playtesting.
