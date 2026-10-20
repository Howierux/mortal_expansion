# Moon phase and world Haunt spread

The daily loaded-chunk Haunt spread roll is multiplied by the current moon phase. Full moon suppresses spread; new moon encourages it. Waxing and waning phases of equal illumination use the same weight.

| Phase | Spread multiplier |
| --- | --- |
| Full moon | 0.50x |
| Waning gibbous | 0.75x |
| Last quarter | 1.00x |
| Waning crescent | 1.25x |
| New moon | 1.50x |
| Waxing crescent | 1.25x |
| First quarter | 1.00x |
| Waxing gibbous | 0.75x |

Before weighting, daily spread odds are 7.5% for residual Void Haunt, 15% Natural, 22.5% Supernatural, then 7.5 percentage points more per tier through 52.5% Eldritch. Zero Haunt never spreads. For example, Supernatural spreads at 11.25% under a full moon and 33.75% under a new moon; Eldritch ranges from 26.25% to 78.75%.

A successful roll still chooses one cardinal neighbor and adds one point only if that chunk is loaded, unprotected and below the maximum. Failed target checks do not retry. The existing snapshot prevents same-day chain reactions. The moon changes only the spread roll, including spread into previously zero-Haunt chunks; decay/recovery, personal leakage, Ward protection, conversions and personal Haunt events retain their existing rules.

The phase is sampled once per daily simulation pass using the native level moon-phase API. Existing day-transition timing applies, including sleep; there is no extra nighttime roll or unloaded catch-up. No saved fields, packets, chunk loading, custom calendar or mixins are added. Migration checks should preserve vanilla phase zero as full and phase four as new.

Validation covers all eight native phases over two cycles and every Haunt value, including zero and maximum. Build and server gameplay tests validate integration.
