# Spirit unfinished-business quests

Right-click an adult Spirit that is not trading to open buttons for Trade, Unfinished Business and Leave. Trade opens the ordinary merchant screen. Unfinished Business shows a kill quest offer with Accept Quest, Decline Quest and Back buttons, or the existing active quest and its progress. No sneaking is required. Temporary Haunt offers still use clickable chat Accept/Decline controls. `/spiritquest` or `/spiritquest status` shows the current objective or unexpired offer; `/spiritquest abandon` clears an active quest. Accept/decline commands contain the offer's unique token so old chat messages cannot act on newer offers. These are ordinary player commands, not operator commands.

There is one active quest or offer per player. Offers expire after five minutes of server game time, and new offers have a 60-second cooldown to discourage immediate rerolling. Acceptance is optional; kills before acceptance do not count. Active quests do not expire and persist across logout, death and dimension travel, without storing an entity reference or requiring the giver to remain alive.

Target selection prefers the hostile pool 75% of the time and the non-hostile pool 25%. Within a pool, types are equally likely. Empty pools fall back to the other pool, and Peaceful excludes hostile candidates. Three generated entity-type tags define the catalog and allow datapacks/mods to extend it:

- `mortal_expansion:quest_targets/easy`: Cow, Pig, Sheep, Chicken; 1–3 kills.
- `mortal_expansion:quest_targets/standard`: Zombie, Skeleton, Spider, Creeper; 3–5 kills.
- `mortal_expansion:quest_targets/hard`: Witch, Enderman, Pillager; 1–3 kills.

If a type appears in multiple difficulty tags, Hard takes precedence, followed by Standard. Hostility is classified using the registered entity type's MONSTER category. The default catalog avoids bosses and compulsory dimension travel. Difficulty is a starting balance catalog; it is not computed from nearby mob equipment or biome availability.

| Difficulty | Reward |
| --- | --- |
| Easy | 2 emeralds, 1 raw ectoplasm, 20 XP |
| Standard | 4 emeralds, 2 raw ectoplasm, 40 XP |
| Dangerous | 6 emeralds, 1 purified ectoplasm, 60 XP |

Only matching mob deaths attributed to the quest holder count, including player-owned projectile sources. Other players' kills, environmental kills, canceled deaths, and temporary haunting-owned encounters do not count. Pet-only kills are not currently credited. Existing personal Haunt costs from mob killing remain normal gameplay.

On the final kill the quest is cleared first, then items and XP are delivered immediately. No return to a Spirit is required. Items enter the inventory; overflow drops at the player with pickup ownership assigned to them. Duplicate completion attempts cannot pay again. This follows ordinary save behavior; it does not add a transactional crash-recovery economy.

## Haunt integration

`SPIRIT_COMMISSION`, previously marked missing, now offers the same optional unfinished-business quest directly from a restless spirit. It uses its existing Dead Rising-or-higher personal Haunt eligibility and independent benefit cooldown. No permanent Spirit is spawned. The offer cooldown and one-active-quest limit apply. Operators can test with `/mortalexpansion haunt effect @s spirit_commission`, then accept the chat offer.

## Persistence and migration

The new `mortal_expansion:spirit_quests` player attachment stores immutable codec records for the pending offer and accepted objective, using registry resource locations and a difficulty snapshot. It copies on death and defaults to empty in old saves. No existing attachment changes or new mixins are needed. The Spirit options screen adds native-codec open/choice payloads; menu sessions are transient and server-authorized. Catalog tags come from the Java entity-tag provider and are committed with generated output. Migration checks: attachment codecs/copy-on-death, native death attribution/cancellation, ordinary commands, clickable chat actions and inventory overflow semantics.

## Verification

Build and all 118 required server tests pass. Quest tests cover Spirit interaction and optional acceptance, stale tokens, command access without operator permission, persistence after giver departure, wrong-target/other-player/canceled kills, native completion, single payout, all reward tiers, full inventory overflow, hostile bias, Haunt commissioning, decline/cooldown and expired offers. Guide and generated-resource checks pass. Client chat interaction, actual respawn cloning and extended balance playtesting remain manual checks.
