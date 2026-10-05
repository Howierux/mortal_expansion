# Independent personal Haunt benefits and risks

Implemented 2026-10-05. Benefits and risks are separate event pools, with independent random rolls, active windows and 60-second cooldowns. Every 30 seconds each ready pool checks the player's personal tier. Per-pool chances are Natural 2%, Supernatural 5%, Dead Rising 8%, Demonic 12%, Abominable 16%, Eldritch 20%; Void never rolls. When both pools are ready at Eldritch, the chance of at least one pool passing its roll is 36%, before situational eligibility. Weights decide which eligible effect occurs; failed operations fall back within that same pool. Lower-tier effects remain eligible at higher tiers.

A successful effect sends a localized, atmospheric chat hint. No hint or cooldown is issued for a failed operation or missing function. A Spiritwell Ghost held in the existing durable queue announces itself only on actual delivery. Chat preserves hints if both pools succeed together.

## Live new benefits

| Effect | Minimum personal tier | Behavior |
| --- | --- | --- |
| Helpful whispers | Natural | Directional sound and particles at the closest loaded Grave Moss or Gravestone within a bounded 12-block horizontal/4-block vertical search. No trace means no trigger. |
| Spectral empowerment | Demonic | Holding a demi-tangible weapon grants 25% additional direct spectral melee damage for 20 seconds. Switching to an ordinary weapon does not retain the bonus. Supports the existing tag/component/enchantment permission helper. |

Existing beneficial potions, Spirit visits, furnace completion and material conversions remain in the benefit pool.

## Live new risks

| Effect | Minimum tier | Behavior |
| --- | --- | --- |
| Unwanted company | Natural | Six seconds of following footsteps. A visible harmless follower is a missing function. |
| False traces | Supernatural | Uses the existing fleeting apparition renderer; approaches/direct observation reveal its ephemeral nature. New track objects remain future work. |
| Restless visitation | Dead Rising | A safely placed hostile Ghost nearby, expiring after 30 seconds or when escaped/protected. |
| Spectral interference | Demonic | An active nearby Haunt/Haint Furnace pauses for three seconds, retaining inputs, outputs, fuel and cooking progress. Other machine integrations are missing functions. |
| Marked presence | Demonic | For 20 seconds, untargeted hostile intangible mobs within 32 blocks, beyond eight blocks, can notice the player. Existing targets belonging to other fights are preserved. Owned attraction targets are released on expiry/protection. |
| Persistent pursuer | Abominable | One Ghost continually targets its owner for up to two minutes; escape beyond 48 blocks, protection or defeat ends it. |
| Veil breach | Eldritch | Five-second particle/chat warning, then up to three safely placed Ghosts lasting at most 30 seconds. Protection, stale/off-dimension warnings, unsafe terrain, caps or an existing nearby group can cancel the crossing; cancellation has an honest hint. |

Encounters use loaded-only placement checks, world border/collision checks, existing mob-cap reservations and protected-location checks. Peaceful suppresses hostile encounter effects. No terrain is loaded to find a target. Temporary encounters store owner/deadline tags and end on owner death, logout, dimension change, protection or distance; expired entities are removed when loaded again. Existing normal/natural mobs are not removed.

Full Ghost/Haint-family garb, Holy Water blessings and Purification Wards suppress negative triggers while leaving benefits available. Owned timed risks and pending breaches are cleared by cleansing; temporary encounter cleanup runs once per second. Personal Haunt does not automatically change chunk Haunt.

## Missing functions: reserved, excluded from selection

The following enum entries retain their intended minimum tiers but return false from `implemented()` and cannot spend a roll, cooldown, or send a fake success hint. Each dependency is marked with a literal `missing function` source comment:

- Second sight (Supernatural): hidden inscription/track definitions and reveal rendering.
- Spectral insight (Supernatural): optional discovery records and dynamic Book entries.
- Spirit commissions (Dead Rising): quest acceptance, objectives, completion and rewards.
- Helpful visitation (Dead Rising): spirit worker jobs, ownership and inventory permissions.
- Spirit recognition (Abominable): expertise-aware service menus and service definitions.
- Veil revelation (Eldritch): hidden-site registry and willing powerful Spirit encounters.

## Persistence, migration and verification

Existing player ambient-state attachments retain risk `CooldownUntil`/`ActiveUntil`; new `BenefitCooldownUntil`/`BenefitActiveUntil` fields default to zero on older saves. Additional effect deadlines and encounter tags use overworld game time. Furnace interruption uses the framework's block-entity persistent data. No custom packets, registry migrations, client dependencies in common code, or new mixins are introduced. Hint text and Book pages are localized; existing resource generation remains unchanged.

Automated tests cover pool separation and save/load, tier inheritance and inactive entries, actual spectral damage and expiry, furnace conservation/resumption, garb protection/owned-risk clearing, loaded trace conditions and bounded duplicate-resistant encounters. Client presentation and prolonged live-world AI/lifecycle behavior still need playtesting.
