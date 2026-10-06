# Haunt-reactive farmland

Implemented 2026-10-05. Tilling dirt/grass/path with a Bloodstone or Spectralite Hoe creates Haunted Farmland, using normal farmland textures and moisture states. Vanilla tilling predicates, coarse/rooted dirt steps, sounds and one durability point remain intact. Ordinary hoes produce ordinary farmland.

Each server random tick reads the soil's current chunk Haunt, not its creator's personal Haunt or the original tier at tilling. If a growth roll succeeds, a bonemeal-compatible crop directly above receives the native bonemeal action and particles. Plants must belong to the vanilla crops tag and support the bonemeal API; mature or non-bonemealable crops are skipped. No actual inventory bonemeal is consumed. Framework bonemeal hooks are respected.

| Chunk tier | Bonemeal chance per farmland random tick | Automatic moisture |
| --- | --- | --- |
| Void | None | No |
| Natural | 1/16 | No |
| Supernatural | 1/8 | No |
| Dead Rising | 1/4 | Full, moisture 7 |
| Demonic | 1/3 | Full, moisture 7 |
| Abominable | 1/2 | Full, moisture 7 |
| Eldritch | Every random tick | Full, moisture 7 |

These are initial balance values. Random ticks are not game ticks: actual timing depends on randomTickSpeed and loaded/ticking sections. Normal crop growth continues independently. At Supernatural and below, ordinary water/rain/drying rules still apply. Above Supernatural, the farmland starts moist when tilled and restores full moisture on its random ticks without water. A later drop in chunk Haunt resumes ordinary moisture rules.

## Lifetime and compatibility

The block ID stores the soil's special provenance in normal chunk block states. Dry and damp states both retain the bonus while the block remains Haunted Farmland. Trampling, a solid covering, breaking or dry empty-soil reversion to dirt ends it. Re-tilling that dirt with an ordinary hoe does not recover the bonus. Breaking drops Dirt; the block is a tilling result without a dedicated inventory item or recipe.

The implementation subclasses the supported HoeItem and FarmBlock APIs, preserving native crop placement/fertility, shape, hydration, trampling and scheduled survival behavior. Dry/moist models, state variants, loot and shovel tags are generated in Java. No new mixin, ticking block entity, saved position index or packet is needed. The stable block ID must be preserved when migrating worlds; moisture remains the vanilla 0-7 property. Ordinary previously tilled farmland is not retroactively converted.

## Validation

Build and 77 required server GameTests pass. Tests cover actual tilling by both hoes and ordinary hoes, normal wear, chunk-versus-personal tier behavior, exact moisture threshold, tier growth ordering, native bonemeal growth, mature/non-crop guards, saved block-state roundtrips, drying/replacement and scheduled solid-cover reversion. Data-generation checks cover 418 resources; the guide stays within 128 pages. In-game texture/particle appearance and long-run crop balance still require playtesting.
