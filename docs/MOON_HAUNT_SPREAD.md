# Moon phase and world Haunt spread

Loaded chunks receive three spread checks per full Minecraft day, at world-time interval boundaries 8,000 ticks apart (8,000, 16,000, 24,000, then repeating). Each nonzero chunk uses a base chance of 66 2/3%, weighted by the current moon phase. Zero Haunt never spreads. Every nonzero tier reaches a 100% spread roll at new moon.

| Phase | Multiplier | Chance per check |
| --- | --- | --- |
| Full moon | 0.50x | 33.33% |
| Waning gibbous | 0.75x | 50% |
| Last quarter | 1.00x | 66.67% |
| Waning crescent | 1.25x | 83.33% |
| New moon | 1.50x | 100% |
| Waxing crescent | 1.25x | 83.33% |
| First quarter | 1.00x | 66.67% |
| Waxing gibbous | 0.75x | 50% |

A successful roll chooses one cardinal neighbor and adds one point if it is loaded, unprotected and below the maximum. The 100% guarantee applies to the probability roll; unavailable or protected neighbors still prevent transfer and do not retry. Wards also suppress source spreading. Each pass reads a snapshot, so newly added Haunt cannot chain-spread within that pass, but can spread at a later check.

Decay/recovery, Ward cleansing, Graveyard Stone weathering and Bloodstone conversion remain once per day. Player leakage and personal Haunt effects retain their own schedules. Only spread receives the two additional checks.

The native level moon-phase API is sampled once per pass. Sleep or forward time commands crossing multiple intervals cause at most one spread pass, with one daily maintenance pass if a day boundary was crossed; no skipped/unloaded intervals are replayed. Backward clock changes reset the scheduling baseline without running a pass, and a frozen day cycle pauses scheduled spread. First observation after startup initializes the clock without an immediate extra pass. No saved fields, packets, chunk loading, custom calendar or mixins are added.

Tests cover all phases over two cycles and every Haunt value, three evenly spaced checks, frozen/backward clocks, sleep and large forward jumps. Migration should preserve vanilla phase zero as full and phase four as new. Long-term growth is a balance playtest concern: these odds deliberately increase world spread substantially.
