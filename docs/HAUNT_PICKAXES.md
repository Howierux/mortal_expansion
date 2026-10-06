# Wear-driven Bloodstone and Spectralite pickaxes

Implemented 2026-10-05. Both pickaxes mine pickaxe-compatible blocks faster as durability is lost. The multiplier is linear: full durability 1x, half durability about 1.5x, nearly broken almost 2x. Native tier speed, harvest requirements, enchantments and player mining modifiers remain in effect. Other material types do not receive this wear boost.

## Self repair

Damaged pickaxes repair one durability point per activation while carried in ticking inventories or as loaded dropped items. Each activation draws exactly 0.01 Haunt from the tool's current chunk; personal Haunt is unaffected.

| Current chunk tier | Interval per durability point |
| --- | --- |
| Void | Disabled |
| Natural | 60 seconds |
| Supernatural | 30 seconds |
| Dead Rising | 15 seconds |
| Demonic | 10 seconds |
| Abominable | 5 seconds |
| Eldritch | 2 seconds |

Times are game ticks at 20 TPS. First exposure starts the clock, and a fully repaired item costs nothing. A persistent per-stack clock prevents repeated repair from moving/dropping the same tool. Void exposure resets the clock; entering a haunted chunk starts again. Dropped item updates retain normal physics and synchronize the repaired stack. Ordinary chests do not tick their contents, so stored pickaxes are not continuously repaired there. Delayed/unloaded tools receive at most one activation on resuming, rather than a large offline catch-up.

A repair at exactly 10.00 chunk Haunt leaves 9.99, crossing into Void and disabling subsequent repairs. Repair restores ordinary durability and therefore gradually reduces the wear-speed benefit. Tooltips show the current multiplier and Haunt cost.

## Fractional chunk Haunt

Chunk Haunt now retains hundredths alongside the existing named tier/progress fields. Existing chunks default to zero fraction. Fixed-point arithmetic preserves fractions through normal whole-point gains/losses and world simulation. Explicit value/tier assignments reset fractions deliberately. Values stay within 0.00-69.00; named tiers and existing whole-point APIs use the integer floor.

The Precise EMF Reader now displays actual chunk fractions, for example 16.99. Basic bulbs still show the named tier. Its transient sample stream now carries chunk hundredths, so server and clients need the same build. No persistent EMF sample migration is needed.

Stable new state consists of the codec-backed chunk haunt_fraction attachment and the pickaxe_repair_time long item component. Preserve their IDs when migrating; the timestamp uses overworld game time so dimension changes do not select a different clock. Tools imported into an earlier-time world reinitialize their clock. No new mixins, item IDs, recipes, models or custom packets are introduced.

## Validation

Build and 94 server GameTests pass. New tests cover both tool speed curves and wrong materials, all tier intervals, exact 0.01 costs, duplicate-tick prevention, Void/full durability guards, inventory/dropped hooks, unchanged personal Haunt, whole-point fraction preservation, exact caps, real chunk attachment serialization, pickaxe saved/network components and precise reader network values. Guide/data checks pass. Live mining feel, repair cadence and client presentation remain for playtesting.
