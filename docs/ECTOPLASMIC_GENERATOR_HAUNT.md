# Ectoplasmic Generator Haunt output

Each fully completed fuel burn adds Haunt to the generator's current chunk:

| Fuel | Burn duration | Base power | Chunk Haunt at completion |
| --- | --- | --- | --- |
| Raw Ectoplasm | 1,000 generation ticks | 20 FE/t | +1 |
| Purified Ectoplasm | 2,000 generation ticks | 40 FE/t | +3 |

The existing Haunt multiplier still increases FE output; it does not multiply the Haunt payment. Haunt is added only after the last actual generation tick, not when fuel is inserted or consumed. Full buffers pause burning and emit no Haunt. Breaking the generator before a burn finishes gives no partial Haunt output. Exact existing fractions are retained unless the chunk reaches the normal 69-point maximum. Only the generator's chunk is affected, and ordinary Haunt spread remains separate.

Both inventory-fed and programmatically started burns snapshot their fuel's Haunt output. The pending amount is saved as `BurnHaunt` alongside existing burn progress and base FE. Completion clears it before emission; idle ticks and loading a finished burn cannot repeat the output. Older saves without this field infer an active burn's output from the existing persisted raw/purified base-generation rate. No new registry IDs, attachments, packets, generated resources or mixins are needed. Migration checks: fuel snapshot, productive-tick gating, native energy acceptance and persisted burn completion.

Build and 124 server tests pass, covering both complete fuel durations, no early payment, retained fractional Haunt, single consumption/output, full-buffer pause/resume, saved active/finished burns, legacy active-burn migration and normal Haunt clamping. Guide and generated-resource checks pass. Long-term feedback between generator Haunt and energy production remains a balance playtest check.
