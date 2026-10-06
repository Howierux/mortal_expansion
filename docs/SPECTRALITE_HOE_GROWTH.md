# Spectralite Hoe active crop growth

Implemented 2026-10-05. Right-click an immature bonemealable crop with a Spectralite Hoe to apply repeated native bonemeal pulses toward maturity. Each accepted pulse costs one whole Haunt point from the clicked crop's chunk. The player's own chunk and personal Haunt are not charged. No inventory bonemeal or additional hoe durability is consumed by growth; normal tilling still costs normal durability.

Common crops mature in one click if the chunk has enough Haunt. A low balance produces partial growth and stops at zero. Mature crops, mature melon/pumpkin stems, non-crops, unsupported plants and vetoed applications do not drain Haunt. An action-bar hint explains a depleted chunk; the hoe tooltip displays the current pulse cost.

The action is limited to sixteen accepted applications per click, preventing an unbounded loop on unusual modded crops. Eligibility is checked again after each pulse. Crop-tagged plants use the native BonemealableBlock/BoneMealItem API, so normal growth amounts, success probabilities and framework bonemeal hooks remain in effect. An accepted but probabilistically unsuccessful native application still costs a pulse, just as native bonemeal consumes an item. Mature stems are explicitly skipped because their native bonemeal behavior can otherwise continue producing fruit.

Growth works on ordinary farmland as well as Haunted Farmland; the active ability does not require the passive soil. Bloodstone hoes retain only the special tilling behavior. The supported early item-use hook respects the player's ordinary right-click protection checks and passes the player to the bonemeal event. Nearby chunks must already be loaded; no growth search or forced chunk generation occurs.

HauntManager handles each withdrawal, including clamping, persistence and named-tier/furnace-cache transitions. No new saved fields, registries, mixins, recipes or custom packets are needed. Cost and iteration limit are centralized in SpectralCropGrowth; the tooltip reads the cost constant.

Build and 82 server GameTests pass. New tests use the real ServerPlayerGameMode right-click pipeline to verify full maturation, partial growth/exhaustion, mature/stem/non-crop guards, unchanged personal Haunt/tool wear, Bloodstone exclusion, exact target-chunk charging, and both bonemeal and right-click event vetoes. Guide and generated-resource checks pass. Visual particles, hand feedback and long-run cost balance remain for client playtesting.
