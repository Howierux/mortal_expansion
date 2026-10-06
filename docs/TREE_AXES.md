# Tree axes

Bloodstone and Spectralite axes fell connected tree logs after a normal log harvest. Sneak for single-log mining. Each log uses normal drops, enchantments, durability and protection checks; breaking the axe stops the operation. Leaves decay normally.

Discovery requires natural foliage (non-persistent leaves or wart blocks) near the logs. Vanilla wood-family tags include stripped variants; other woods such as Spiritwood use matching block identity. Different wood families are not merged. The bounded search accepts diagonal branches, up to 512 logs within 16 horizontal and 64 vertical blocks, in loaded chunks only. This is a foliage heuristic, not a guarantee that a player-built structure near natural leaves is excluded. Sneaking is the override. A denied branch cannot serve as a bridge to additional logs.

Right-click a sapling with the Spectralite axe to apply one native bonemeal use for **0.10 Haunt from the sapling's chunk**. Repeat as needed: ordinary stage, chance and space requirements still apply. Accepted chance failures spend Haunt like bonemeal normally consumes an item. Insufficient Haunt, unsupported targets and canceled bonemeal events spend nothing. Personal Haunt and axe durability are unchanged by growth. Spiritwood uses its existing sapling grower and gravemoss decoration.

Implementation uses NeoForge harvest/tick events and native player destruction and bonemeal hooks. No new saved data, mixins, recipes or registry IDs are introduced. Migration checks: preserve per-log harvesting/protections, early item-use hooks, native sapling growth events and wood-family tag membership.

Validation includes whole-tree harvest with diagonal branches and stumps, different wood families, sneaking, placed foliage, protected branches, tool breakage, fractional payment, native Oak/Spiritwood growth and bonemeal vetoes. Client visuals, unusual modded trees and balance still need playtesting.
