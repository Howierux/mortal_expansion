# Fabric Haunt events and protection

The Fabric event dispatcher now replaces the earlier Insight/altar-only natural pool. Personal Haunt drives two independent benefit/risk rolls every 600 ticks. Chance per pool is 2%, 5%, 8%, 12%, 16% and 20% from Natural through Eldritch; Void disables natural rolls. Each pool has its own 1,200-tick cooldown and active deadline. Contextually unavailable effects are skipped without consuming a successful event. Research biases Insight in the benefit pool; a completed research revelation also starts that pool's cooldown.

## Available effects

Twenty-six selections have functional behavior: Offering Gift/Blessing; tapping, shadow, slow, shove, shuffle; good/bad potion; Spirit/Ghost; furnace completion; plank/cobblestone conversion; Helpful Whispers; Second Sight tracks; Spectral Insight; Helpful Visitation; Empowerment; Unwanted Company; False Traces; Restless Visitation; Interference; Marked Presence; Pursuer; Veil Breach. Successful effects send their translated hint; Insight keeps its specific book whisper.

- Tapping uses the native generic wood-hit sound. No Warframe audio is included.
- Shadows are client-only, 75%-opaque player-shaped apparitions with white eyes, rather than actual spawned Ghosts. Looking directly at one, approaching it or its deadline clears it.
- Shuffle only moves occupied main-inventory stacks, preserving their components and the hotbar. Conversions use native stacks and shared chunk cooldowns.
- Furnace completion advances one native operation, preserving drops/remainders and XP handling. Interference pauses Haunt/Haint furnaces for 60 ticks, including their fuel timer. Third-party furnace recipe types/machines need compatibility testing/adapters.
- Helpful Visitation grants a Spirit Boon. Both altars already accept it for 1,200 ticks (one minute) of fuel.
- Encounter spawns use bounded, loaded-only safe-position searches and category caps. Temporary encounters and marked targets have persisted ownership/deadlines and are cleaned up when their owner becomes unavailable, protected or out of range.
- Ghost selections without a usable loaded well within 100 blocks remain in a per-dimension persistent queue. Delivery, rather than selection, starts their cooldown. At most one queued encounter is delivered per dimension per second.

## Garb and owned-effect cleanup

A risk is rolled/selected before full garb prevents it and sends the prevention hint. Prevented risks never run or enqueue Ghosts, and do not delay benefits. Ward/blessing protection still vetoes negative effects. Equipping full garb clears haunting-owned negative potions on the next equipment tick; ordinary external potions remain. A later external application relinquishes haunting ownership. The existing successful apparition/Spirit/Ghost hooks can repair equipped garb by one durability at Dead Rising personal Haunt or above.

`haunt_effect_state` is a persistent player attachment copied on death. Managed entity/machine state uses `haunt_managed_state`; queued Ghosts/shared world cooldowns use per-dimension SavedData. All event deadlines use Overworld game time. One focused add-effect hook distinguishes external potion ownership; one furnace accessor advances native cooking progress without duplicating the smelting algorithm.

## Commands

Operator-only commands bypass natural tier/cooldown waiting while retaining player validity, protection and context checks:

```text
/mortalexpansion haunt @s hit
/mortalexpansion haunt @s shadow
/mortalexpansion haunt @s second_sight
/mortalexpansion haunt @s helpful_visitation
/mortalexpansion haunt @s random
/mortalexpansion haunt @s positive
/mortalexpansion haunt @s negative
```

Specific names use the enum's lowercase spelling. Completion suggests all named selections. The result reports triggered/prevented targets; an unavailable selection returns zero rather than pretending to execute.

## Explicit remaining dependencies

`SPIRIT_COMMISSION` now issues persistent unfinished-business quests with immediate completion rewards. `SPIRIT_RECOGNITION` and `VEIL_REVELATION` still need expertise services and hidden-site/powerful-Spirit designs respectively; the latter two are also unfinished in the reference. They remain commented and excluded from natural/random pools. Unwanted Company currently supplies footsteps rather than a visible follower; False Traces reuses the apparition rather than placing decoy track objects. Second Sight supplies real footprints but does not reveal hidden inscriptions. Dreamcatcher ownership/interception, including deferred Ghost deliveries, is implemented; only future machine interference adapters remain pending.

Native tests exercise real command dispatch, independent cooldowns, owned versus external effects, immediate garb cleanup, Boon fuel, cooking/interference, inventory conversion and durable queued encounters. See the current test total in [Fabric port status](FABRIC_PORT.md).
