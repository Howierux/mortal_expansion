# Tree axes

Bloodstone and Spectralite axes fell connected tree logs after a normal log harvest. Sneak for single-log mining. Each log uses normal drops, enchantments, durability and protection checks; breaking the axe stops the operation. Leaves decay normally.

Discovery requires natural foliage (non-persistent leaves or wart blocks) near the logs. Vanilla wood-family tags include stripped variants; other woods such as Spiritwood use matching block identity. Different wood families are not merged. The bounded search accepts diagonal branches, up to 512 logs within 16 horizontal and 64 vertical blocks, in loaded chunks only. This is a foliage heuristic, not a guarantee that a player-built structure near natural leaves is excluded. Sneaking is the override. A denied branch cannot serve as a bridge to additional logs.

Right-click a sapling with the Spectralite axe to grow a tree using repeated native bonemeal applications for **0.10 Haunt from the sapling's chunk, charged once at the start**. The first accepted application pays the full cost; further pulses in the same click are free and stop when the sapling becomes a tree. Normal tree selection, sapling stages, native growth hooks and space requirements remain. Spiritwood uses its existing sapling grower and gravemoss decoration.

Insufficient Haunt, unsupported targets and an initial canceled bonemeal event spend nothing. A later veto stops further pulses without another charge. Blocked growth is limited to 15 attempts per click to protect the server thread; a remaining sapling displays a growing-conditions hint. An accepted but blocked attempt still pays the one starting cost. Personal Haunt and axe durability are unchanged by growth. No persistent retry jobs are created.

Implementation uses NeoForge harvest/tick events and native player destruction and bonemeal hooks. No new saved data, mixins, recipes or registry IDs are introduced. Migration checks: preserve per-log harvesting/protections, early item-use hooks, native sapling growth events and wood-family tag membership.

Validation includes whole-tree harvest with diagonal branches and stumps, different wood families, sneaking, placed foliage, protected branches, tool breakage, fractional payment, native Oak/Spiritwood growth and bonemeal vetoes. Client visuals, unusual modded trees and balance still need playtesting.
