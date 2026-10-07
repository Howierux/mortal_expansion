# Haunt effect testing commands

Operators (permission level 2) can target one online player by name or a single-player selector:

```mcfunction
/mortalexpansion haunt effect @s random
/mortalexpansion haunt effect PlayerName spectral_insight
/mortalexpansion haunt effect @s second_sight
/mortalexpansion haunt effect @s good_potion
```

The effect argument offers autocomplete for `random` and all effect IDs. Random uses the target's current personal tier, normal selection weights and protection rules; it tries other eligible effects if the selected operation cannot run. Void has no eligible random events.

A specific effect bypasses minimum tier and automatic cooldown for development testing. Underlying requirements remain: protective gear/wards, available nearby blocks or safe spawn positions, and Spectral Insight's aware book, personal-tier secret progression and unrevealed knowledge. Targets must be alive and outside spectator mode; creative targets are allowed. Commands do not change personal/chunk Haunt or automatic cooldowns as a prerequisite, although the requested effect retains its normal gameplay consequences.

Successful events show their normal player hint and an operator confirmation. Unknown, unimplemented or blocked effects report failure. Ghost commands require an immediately usable spawn location instead of putting a ghost into the dimension-wide deferred queue for a different player.

No persistent data or network changes. This uses Brigadier/vanilla entity arguments and the existing server effect operations. Validation covers native command parsing, single-target resolution, permission checks, explicit tier/cooldown bypass, unchanged Haunt/cooldown, random eligibility, missing functions and spectator rejection. Manual feedback and effect-specific world scenarios remain playtest checks.

`spirit_commission` now creates an optional unfinished-business kill quest offer. Accept it from chat; completion pays without returning to an NPC. The target must have no active/unexpired quest and the offer cooldown must be ready.

Random effect commands honor Mortality Research bonuses and spend them on a successful eligible selection. Explicit named commands neither redirect nor spend that bonus.
