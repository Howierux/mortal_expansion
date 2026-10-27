# Bloodstone scaling, Spirit trade variety and visitation boons

## Bloodstone tools

Bloodstone swords, pickaxes, axes, shovels and hoes use their Diamond counterparts' base attack damage, attack speed and mining speed. Personal Haunt applies one multiplier to attack damage, attack speed and mining speed: Void 90%, Natural 100%, Supernatural 110%, Dead Rising 120%, Demonic 130%, Abominable 140%, Eldritch 150%. Attack speed means attacks per second, rather than shortening cooldown by that percentage. Existing extra durability, harvest capability, enchantability, area mining, tree felling, crop effects and demi-tangible permission are retained. Worn-pickaxe mining acceleration stacks with this multiplier, and repairs still depend on chunk Haunt.

The server maintains transient native attack modifiers while the appropriate tool is in the main hand, removing them on switching away or entering spectator mode. Native attack-speed synchronization provides the client's mining multiplier through BreakSpeed without another packet. Modifiers are multiplicative with other attack attributes; mining multiplies the existing event speed. Existing items with deliberately overridden attribute components retain those components. No new saved player fields or mixins are introduced. Tooltips' native base attributes remain the Natural baseline; dynamic combat follows the held-tool modifier.

## Spirit trade variety

Each expertise tier retains its two core offers and gains two distinct additional offers drawn from four candidates. Selection uses the Spirit UUID and tier, then persists as ordinary merchant offers. Different Spirits can share offers, but no longer have identical guaranteed catalogs. Opening, restocking and reloading do not reroll selections. A Master has twenty offers.

| Tier | Extra offer pool |
| --- | --- |
| Novice | Grave Moss, Spiritwood Saplings, buying Spiritwood Logs, buying Grave Moss |
| Apprentice | Gravestone, Spirit Torches, Spiritwood Logs, Graves |
| Journeyman | Purified Ectoplasm, Purified Gravestone, EMF Reader, Bloodstone |
| Expert | Ghost chestplate/leggings assembly, Dreamcatcher assembly, Precise EMF Reader |
| Master | Haint hood/boots assembly, Purified Dreamcatcher assembly, Wisp o' Lantern |

Assembly trades retain full thread costs plus emerald fees. XP, stock and ordinary workstation restocking remain expertise based. Catalog version 2 saves retain every existing offer, use count, demand and expertise; two extras per unlocked tier are appended on first load. Version 3 reloads retain that exact selection. Earlier unsupported legacy catalogs retain the previous regeneration policy.

## Helpful Visitation and Spirit Boon

Helpful Visitation is now an active Dead Rising-or-higher benefit that gives exactly one Spirit Boon, with particles and a thematic hint. It uses existing benefit cooldowns and dreamcatcher prevention. No permanent worker or visitor is spawned. Inventory space is used first; otherwise an owner-targeted item drops at the player.

A Spirit Boon is accepted by both altar offering tags and adds 1,200 burn ticks (60 seconds) per consumed item. Normal altar consumption timing, capacity and Purity's separate 50% offering blessing still apply. A blessed boon therefore receives the existing bonus. It has no ordinary crafting recipe: visitation is its survival source. The Creative tab also exposes it for testing.

The generated 32x32 RGBA sprite is `src/main/resources/assets/mortal_expansion/textures/item/spirit_boon.png`; nearest-neighbor export preserves the built-in imagegen master's transparency. Master: `C:/Users/scott/.codex/generated_images/01a10808-7c57-7922-a619-5f19e36de671/exec-165fd02a-64da-4217-ab0b-297c1319a0de.png`.

Final built-in imagegen prompt: Minecraft inventory item sprite, a Spirit Boon: a small pale ivory knotted cloth charm enclosing a soft mint-green spirit flame, tied with a simple golden thread. Flat front-facing original 16x16 logical pixel art enlarged nearest-neighbor, bold clean silhouette centered with clear margins, only 3 shades per material, readable small. The charm is a gift from a friendly ghost and is sacrificed as an altar offering. No realistic shading, no text, no bloom, no scene, no border. Genuine transparent background outside item.

Validation covers Diamond baselines and all seven personal tiers, mining and attribute cleanup, varied catalogs and old-stock migration, actual boon delivery/cooldown and exact fuel consumption in both altars. Client appearance, live attribute synchronization and long-term trade balance remain playtest checks.
