# Upward intangible mob phasing

Implemented 2026-10-05. Intangible mobs can rise through ordinary solid blocks. Downward collision stays vanilla so grounded spirits do not fall through floors. Ordinary tangible mobs and players retain their existing movement; garb player movement and wall breathing are unchanged.

The shared Entity collision hook now applies a upward exception for intangible mobs and full spectral-garb players. This includes registry-tagged intangible mobs such as Ghasts and dynamic intangible phases such as Pharaohl. A dynamic mob's current phase takes precedence over its type tag. Existing custom horizontal phasing continues independently.

Each rise checks the swept body volume in increments no larger than a quarter block. Spectral-solid materials, Holy Salt columns and linked Spirit Torch columns stop passage, including fast rises whose destination lies beyond a barrier. Unloaded columns are rejected without generating terrain. Normal horizontal collision remains for entities whose movement class does not already support horizontal phasing.

Spectral melee pursuit now accepts higher targets within the mob's follow-range attribute, still subject to the existing four-occupied-cell tracking limit and spectral barriers. The movement controller supplies controlled upward velocity toward genuinely higher destinations. It does not manufacture downward phasing or automatic upward motion without a higher movement destination.

No saved-data, recipe, registry or packet changes are needed. The existing collision mixin is reused; version-sensitive collision calls are isolated in UpwardSpectralPhasing. Migration checks should verify Entity.collide ordering, the static collision helper and mob movement-control semantics.

Build and 71 required server GameTests pass. New movement tests cover Ghosts, Spirit Villagers, tagged vanilla Ghasts, ordinary floors, swept spectral-solid ceilings, Holy Salt columns, tangible mobs, both Pharaohl phases and active pursuit of a higher target. Resource/guide checks pass. Client motion and prolonged live-world pursuit still require playtesting.

## Player gear extension (2026-10-06)

Full Ghost, Haint and Spectralite Haint sets use the same loaded swept-volume check for positive vertical motion. Ordinary ceilings can be crossed upward; negative motion still uses vanilla floor collision. Horizontal garb phasing remains. This enables existing jumping, swimming and other upward movement without adding flight or automatic ascent. Removing a piece restores ordinary ceiling collision. The existing head-in-block breathing, Respiration and normal drowning rules remain intact.

The existing player collision mixin remains the only collision interception: no supported event replaces Entity's collision solver. Its upward branch shares the mob helper; client and server use identical rules. No new mixins, saved fields or packets. Migration checks include upward collision, player movement validation and floor/barrier behavior.
