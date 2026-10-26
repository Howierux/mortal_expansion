# Fabric altar and world-ritual gameplay slice

Both altars now accept offerings, run their original local Haunt effects, expose native inventory automation and have functional screens. Ritual Stars perform repeatable Overworld-wide changes. Original art and block IDs are preserved.

## Using the altars

Right-click for the offering slot, returned-item slot and filling bonfire. Hover the fire for exact remaining time. A small flame stays visible at low reserves. Both blocks emit light level 10 while burning.

Each ordinary offering adds burn time, capped at 72,000 ticks (one hour). At most one offering is consumed every 250 ticks (12.5 seconds). Full reserves or a blocked crafting remainder leave the offering untouched. While burning, a pulse every 200 ticks changes Haunt by 0.10 in the altar's chunk and its eight neighbors: Purity lowers it, Impurity raises it. Only loaded chunks participate; altars do not force-load terrain. Effects pause when the altar is unloaded.

| Offering | Added burn time |
| --- | ---: |
| Grave Moss, Spiritwood Log | 600 ticks / 30 seconds |
| Raw Ectoplasm, Holy Salt, Purified Spiritwood Log, Spirit Boon | 1,200 ticks / 60 seconds |
| Purified Ectoplasm | 2,400 ticks / 120 seconds |

The item tags `mortal_expansion:purity_offerings` and `mortal_expansion:impurity_offerings` control acceptance. Impurity initially includes the Purity tag, matching the reference. Other tagged items default to 1,200 ticks; normal native crafting remainders go into Returned. Standard offerings currently have no remainder, so that slot is generally empty. Fabric item automation can insert valid offerings and remove returned items, but cannot extract unburned offerings.

Inventory, reserve, offering delay, fractional-pulse progress and a pending blessed-offering flag persist in native block-entity saves. Breaking drops inventory and discards stored burn time, as in the reference. The menu uses paired 16-bit fields to retain reserves above 65,535 ticks without signed truncation.

## Ritual Stars

A generated native crafting recipe surrounds one Nether Star with eight Bloodstone. Sacrifice it in an Overworld altar:

- **Purity:** lower every Overworld chunk by 10 Haunt points.
- **Impurity:** raise every Overworld chunk by 10 Haunt points.

One Star is consumed per activation, with the normal offering cooldown, and it adds no ordinary fuel. Rituals are repeatable. The Nether and End reject the Star without consuming it. Regular offerings still work there.

Loaded Overworld chunks change immediately. Other saved chunks and newly generated chunks reconcile on load or authoritative Haunt access, without being force-loaded. Each chunk persists its last applied revision. The ordered journal retains clamping: raising a chunk from 69 to the capped 69, then cleansing, produces 59 rather than a net-zero 69. Fractional hundredths are retained except at the normal bounds. Natural biome/dimension recovery baselines are not raised or lowered.

Persistence boundaries:

- `mortal_expansion_world_haunt_rituals` native SavedData stores the ordered `Shifts` array of +10/-10 entries.
- `mortal_expansion:haunt_ritual_revision` is a persistent Fabric chunk attachment.
- Reconciliation runs after baseline initialization at `HauntManager.initialize`; changes notify existing furnace tier caches.
- Tracking visits only loaded Overworld chunks and is cleared when the server stops. No new mixin is required.

Use fresh Fabric worlds: these keys are deliberate Fabric persistence, not a NeoForge save migration guarantee. Pending personal-Haunt overflow/Darkhold behavior is separate from this Overworld ritual.

## Deferred integration

Altar crafting recipes remain Secrets of the Dead and are now enabled with the [knowledge slice](FABRIC_KNOWLEDGE.md): beacon above Haunt Furnace for Impurity, beacon above Haint Furnace for Purity. Both require their learned secret. The Ritual Star recipe is public.

The Purity altar exposes `blessNextOffering` (50% extra time on one offering) and `addOfferingGift` (Holy Salt). Their behavior and persistence are implemented and tested. The [knowledge slice](FABRIC_KNOWLEDGE.md) now dispatches both hooks as nearby positive Haunt events.

## Validation

The altar slice passed seventeen native server GameTests; the current knowledge update passes 21. Four new integration checks cover offering cadence and 3×3 fractional pulses, cap/return-slot/automation behavior, real block-entity saves and blessed-offering hooks, native journal/chunk-NBT revision round trips, ordered clamping, generated Star crafting, actual altar consumption, other-dimension rejection and future-chunk baseline preservation. The test-only Milk Bucket offering exercises returned vessels and is excluded from the production jar.

A real client verified both screen variants and a reserve exceeding 65,535 ticks. A second launch verified ritual persistence, exactly-once reads, a newly generated chunk receiving the prior shift, and a Purity ritual reversing it. Layout screenshots were inspected, including a follow-up check for low-reserve fire visibility. All launched clients used 30% volume. Temporary probes, worlds and screenshots are excluded from published artifacts.
