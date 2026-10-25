# Fabric generators, pipes and tanks

The two generators and both tiers of fluid tanks/pipes now operate on Fabric 1.21.1. They retain their original item/block IDs and artwork. Obtain them from the creative catalog or `/give` while book knowledge and crafting progression remain unported; this update does not bypass the future Secrets of the Dead recipe gates.

## Fluid storage and movement

| Block | Capacity | Pipe transfer budget |
| --- | ---: | ---: |
| Ectank | 16,000 mB | No tank-side rate restriction |
| Purified Ectank | 64,000 mB | No tank-side rate restriction |
| Ecto Pipe | 2,000 mB | 500 mB/tick |
| Purified Ecto Pipe | 8,000 mB | 2,000 mB/tick |

All accept any fluid supported by Fabric Transfer API, with component-aware fluid identity; incompatible fluids do not mix. Fabric's native units are converted at 81 units per mB. Rates are shared across each pipe's faces and routes, so branching cannot multiply throughput. A normal pipe in a purified network limits routes passing through it to 500 mB/tick.

Right-click a tank with a supported bucket/container to fill or withdraw fluid; a rejected interaction does not spill fluid above the block. Empty-hand interaction displays fluid amount/capacity. Breaking and replacing a tank or pipe preserves its fluid and side modes in native block-entity item data. Tank fill height and color synchronize to clients and use Fabric's fluid rendering support.

Right-click a pipe face/extended arm to cycle its mode:

1. **Normal:** connects, permits flow and outputs to adjacent storage.
2. **Input:** actively pulls from adjacent storage; prevents outward flow through that face.
3. **Output only:** permits outward flow and blocks insertion through that face.
4. **Disabled:** no connection or transfer.

Set the pipe face touching the source tank to Input for automatic pumping. Extended arms select their connection direction, including when their top or side surface is clicked. Disabled connections can be re-enabled by clicking the corresponding core face.

Networks cache loaded topology and directed paths. Block changes, side changes and chunk unloads invalidate it, with a periodic refresh for external storage providers. Routing and balancing use transactions; aborted operations restore both contents and rate budgets. No neighbor chunk is force-loaded. Buffers belong to individual pipes, so splitting a network does not discard pooled fluid. Client fluid updates are batched every five ticks.

## Ectoplasmic Generator

Right-click to open the input and live status screen. Hoppers/Fabric item automation may insert raw or purified ectoplasm but cannot extract its fuel.

| Fuel | Burn time | Base generation | Chunk Haunt on completed burn |
| --- | ---: | ---: | ---: |
| Raw Ectoplasm | 1,000 ticks | 20 energy/tick | +1 |
| Purified Ectoplasm | 2,000 ticks | 40 energy/tick | +3 |

Generation multiplier is `1 + 0.25 × chunk Haunt tier ID`, rounded down. Natural is tier 1, so raw fuel generates 25 per tick there. The buffer holds 200,000 energy, with up to 2,000 per tick pushed across adjacent energy consumers. Newly generated energy remains buffered until the next tick. A full buffer pauses burn time; Haunt is credited once, at actual completion. Active fuel, pending Haunt and energy persist through saving/loading.

## Enchantment Generator

Right-click for input, output, buffered energy, remaining conversion and rate. It accepts enchanted equipment/books and converts all their enchantments, including curses, using the existing formula:

- Total energy: sum of `max(1, enchantment minimum cost at level 1) × level² × 1,000`.
- Generation per tick: sum of `minimum cost × level × 2`, clamped to 20–4,000.

It buffers 400,000 energy and pushes up to 4,000 per tick across adjacent consumers. Conversion pauses when the buffer is full. Its stripped result is inaccessible until the conversion completes. Enchanted books return a Book; other equipment retains its other components and damage. The input, hidden result, progress and energy are saved. Automation may insert valid inputs and extract only completed results. Breaking a generator drops its inventory; stored energy is not retained, matching the original behavior.

## Native integration and scope

Fluid/item access uses Fabric Transfer API sided lookups. Energy uses bundled [Team Reborn Energy 4.1.0](https://github.com/TechReborn/Energy), the upstream-supported series for Minecraft 1.21. Energy values are retained numerically from the original implementation. Generators expose output-only energy storage and push to adjacent providers; Ecto Pipes remain fluid pipes. Other Mortal Expansion energy consumers are separate pending ports.

Native menu factories, slots, data packets, codecs and block entities replace NeoForge capabilities/network menu helpers. Status integers use paired 16-bit data fields, preserving six-digit buffers and large remaining-energy values without signed-short truncation. No new mixins are needed. `machine/` contains the loader-facing implementation; client screens/renderers remain client-only.

This is a fresh-Fabric save format. Matching IDs and preserved item data are not a NeoForge save migration guarantee. Native tags/loot continue to come from the Fabric data providers, and catalog asset regeneration remains deterministic.

## Verification

Nine native Fabric GameTests pass, including four machine integration checks covering capacities, transactional rollback, side/view permissions, arm selection, mixed-tier bottlenecks, conservation, graph removal, native NBT, drop-and-place fluid restoration, rejected bucket handling, exact withdrawals, automation, Haunt credit, full-buffer pauses, energy output, enchantment result handling and menu integer precision.

A real integrated client/server session opened both generator menus with live values and verified tank fluid synchronization and renderer registration at 30% master volume. Screenshots were visually checked for layout. Temporary probe code is excluded from production. Third-party modpack interoperability remains a playtest task; standard API lookups were exercised with native test providers.
