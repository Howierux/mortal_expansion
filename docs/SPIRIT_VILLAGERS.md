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

## Initial trade catalog

| Payment | Result | Stock per day |
|---|---|---|
| 8 Raw Ectoplasm | 1 Emerald | 16 |
| 2 Emeralds | 8 Ectothread | 12 |
| 4 Emeralds | 8 Purified Ectothread | 8 |
| 2 Emeralds | 4 Holy Salt | 12 |

Stock replenishes at most once per new Minecraft day while not trading.
Grave-created workers must be within eight blocks of their loaded grave to
restock; natural Spirits replenish without a grave. Prices and catalog are
initial balancing choices isolated in `SpiritTrades`. No career-tier progression
is shown yet. Broader discovery/spawn and trade balance remain future work.

## Compatibility and validation

Mojang's merchant classes/codecs own trade menus and offer/inventory saves.
No new mixins or global vanilla POI registrations are needed: POIs classify
block states and cannot distinguish individual grave block-entity inscriptions.
The small workstation service reads the grave's authoritative inscription and
claim instead. Existing unnamed graves and old Spirit saves default to no claim.
The movement helper is shared with other spirits to avoid diverging rules.

Server GameTests cover real engraving and eligibility, one worker per grave,
actual merchant-menu payment/output/stock, offer persistence, provenance/claim
persistence, and immediate reversion after breaking a reloaded workstation.
Client appearance and player interaction still need playtesting.
