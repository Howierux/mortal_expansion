# Altar of Purity

A furnace-style offering inventory accumulates burn time while cleansing the altar's chunk and its eight neighboring chunks.

## Offerings and reserve

| Offering | Added burn time |
| --- | --- |
| Grave Moss | 30 seconds |
| Spiritwood Log | 30 seconds |
| Holy Salt | 60 seconds |
| Purified Spiritwood Log | 60 seconds |
| Purified Ectoplasm | 120 seconds |

The first available offering is consumed immediately. Further offerings are consumed at most once every 250 active server ticks (12.5 seconds), including while already burning. Unspent stacks remain in the slot and can be withdrawn. The reserve holds at most 72,000 ticks (one hour at 20 TPS). If the next offering will not fully fit, it waits without being consumed. Tagged custom offerings default to 60 seconds; extend `mortal_expansion:purity_offerings` through a datapack. Individual default durations are isolated in PurityOfferings.

Ordinary Coal and other furnace fuels are not accepted unless added to that tag. Crafting-container remainders are returned to the second slot; incompatible/full remainder storage prevents consumption. Automation inserts offerings into the input slot and extracts only returned containers. The player menu allows ordinary and shift-click transfers.

## Cleansing

While burning, every 200 active ticks (10 seconds), each loaded chunk in the centered 3x3 square loses 0.10 Haunt, clamped at zero. Personal Haunt is unchanged. The altar does not grant Ward protection, prevent leakage/spread or block negative personal events. Existing biome recovery and other Haunt sources continue normally. Multiple active altars each perform their own cleansing pulses.

Only loaded chunks are affected. This altar does not load or generate neighboring chunks, and its burn/consumption timers pause when the altar stops ticking. No offline catch-up or chunkloading tickets are added. Saved burn time, inventory, consumption cooldown and partial cleansing interval resume on load. Breaking an altar drops unspent input/remainder items and the normal block loot; already accumulated burn time is lost, as spent fuel would be in a furnace.

## GUI and recipe

Right-click to open the inventory. The GUI has an offering slot, a returned-container slot and a bonfire image formed from native flame artwork and drawn logs. Flame fill follows the stored reserve with a smoothed visual transition; hover displays exact remaining time as minutes:seconds. The world block uses Purified Gravestone when idle and a glowing texture/light while burning.

Craft one Altar of Purity by placing a Beacon directly above a Haint Furnace. Its blueprint must first be revealed through Spectral Insight in Secrets of the Dead; ingredients alone do not unlock it. The earlier seven-Gravestone/Campfire/Salt recipe is replaced. See [Paired altars](ALTARS_AND_SECRETS.md) for both new recipes and migration details.

## Persistence and migration

New stable IDs: altar_of_purity block/item, block entity and menu; purity_offerings item tag. No existing saved fields, attachments, packets or mixins are changed. A new block entity stores BurnTicks, OfferingDelay, CleanseProgress and the native item-handler inventory. Vanilla menu data uses separate low/high shorts for the reserve, preserving values above 32,767 ticks. Native GUI registration is client-only. Models, states, recipe, advancement, loot and tags are Java-generated and committed together.

Migration checks: menu data slots, native container remainder API, item capability/slots, block ticker and removal callbacks, loaded chunk lookup and fractional Haunt updates. GUI flames use the vanilla furnace sprite through GuiGraphics; no external artwork or sound was added.

## Verification

Build and 130 required server tests pass. Altar tests cover all five fuel durations, invalid fuel, accumulated time, 12.5-second item consumption, reserve capacity/no wasted offers, exact nine-chunk footprint, unaffected outside chunks, idle/exhausted stopping, fractional clamping, save/load phases, long reserve synchronization, inventory transfers, menu validity and unspent break drops. Headless mock clients cannot negotiate the NeoForge advanced-menu opening payload, so inventory behavior is tested directly using the registered menu. Real-client opening, bonfire fill/hover and balance need playtesting.

## Positive Haunt offering events

- **Offering Gift** (`offering_gift`): Natural personal Haunt or higher, selection weight 3. A loaded altar within eight blocks receives one Holy Salt if its input can accept it. Empty/salt inputs qualify; full slots or different offering types do not get overwritten. Normal consumption later burns that gift.
- **Offering Blessing** (`offering_blessing`): Supernatural personal Haunt or higher, selection weight 3. A nearby altar with an eligible offering and enough reserve capacity has its next consumed offering's duration increased by 50%. It still consumes exactly one item and keeps the normal consumption interval. A pending blessing cannot stack; it persists until the next accepted offering and clears on use.

Both are positive events in the existing benefit pool, with the normal 30-second rolls, personal-tier chance and independent 60-second benefit cooldown. Full spectral garb, blessings and wards do not suppress them. Successful effects show chat hints and particles. Missing/ineligible altars cause no success or spent event cooldown. The altar's chunk shares the existing 60-second world-event cooldown with material conversion and furnace benefits, preventing multiplayer spam. If several eligible altars are nearby, the nearest qualifying one is selected.

Operator tests: `/mortalexpansion haunt effect @s offering_gift` and `/mortalexpansion haunt effect @s offering_blessing`. Named commands skip normal tier/player cooldown but retain altar requirements and shared chunk cooldown. These events do not charge personal or chunk Haunt. The offering itself still uses its ordinary ingredients and burn rules.

Persistence adds only the optional `OfferingBlessed` boolean to altar NBT, defaulting false in older saves. No new network payloads or resources. Existing GUI burn-time synchronization shows the increased reserve once the blessed offering is consumed.

Validation: build and 134 server tests pass, including full-garb positive eligibility, independent benefit/risk cooldowns, one-item bonus consumption, ordinary following fuel, actual gift insertion, multiple players/shared cooldown, full/incompatible slots, capacity, pending-blessing replay and save/load. Client hints and long-term balance remain playtest checks.
