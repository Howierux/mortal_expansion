# Holy Water bottles and Consecration

Put **one Holy Water Bucket in the upper ingredient slot** of a Brewing Stand and **one empty Glass Bottle in each of its three lower slots**. Supply Blaze Powder in the fuel slot. A normal 400-tick (20-second) brew fills all three bottles and returns one empty Bucket to the upper slot. Remove the returned bucket, then add **one Gunpowder** above the Holy Water bottles to brew **three Splash Potions of Consecration** in another normal cycle.

Normal brewing-stand partial batches remain possible: one or two empty bottles also work, but consume the entire Holy Water bucket. Each occupied bottle slot must contain a single bottle. Oversized direct/automated stacks are rejected by the recipe rather than having surplus bottles lost. The bucket remains in the ingredient slot for collection; vanilla sided hopper extraction from that slot accepts Glass Bottles, so remove the bucket manually when using that vanilla automation path.

Holy Water Bottles are drinkable. Drinking consumes the filled bottle and returns an empty Glass Bottle through native potion handling. Splash Potions use vanilla throwing, shattering, particles, hit targeting and splash range (within four blocks, with the ordinary two-block vertical envelope). Every eligible player hit receives the same Holy Water blessing:

- Remove **1 personal Haunt**, no more than once per **1,200 ticks (60 seconds)**.
- Suppress negative Haunt events for **1,200 ticks** after application and clear existing haunting-owned risks through the existing protection helper.
- Drinking, splashing and touching placed Holy Water all share the same cleansing cooldown. Multiple bottles cannot bypass it. Reapplication can refresh the blessing subject to the existing contact debounce.

Consecration affects living, nonspectating players; it adds no new mob damage or terrain cleansing. It does not lower chunk Haunt. Native splash potency determines which targets are reached; every target reached receives the full existing blessing rather than a distance-scaled fractional cleansing value.

Both forms appear beside the Holy Water Bucket in the mod's creative tab, have pale blue-white native potion liquid, and show their benefit/cooldown in tooltips. **Bottled Consecration** is a three-page branch of **Cauldrons** on the Book of Ectcetera's Ectoplasm tab (tier 2 onward). It requires no Secrets of the Dead revelation.

## Implementation and portability

`ModPotions` registers `mortal_expansion:holy_water`, containing the instant `mortal_expansion:consecration` effect. These are native `minecraft:potion` and `minecraft:splash_potion` item stacks with `minecraft:potion_contents`, rather than custom bottle item classes. Native item rendering, tint, bottle returns, save codecs, registry synchronization and potion projectiles remain in use. No generated JSON, custom packet, custom entity, item-model duplication, brewing stand replacement or mixin is needed.

`HolyWaterBrewing` registers a standard NeoForge `BrewingRecipe` on `RegisterBrewingRecipesEvent`, narrowing only its input count check. The existing bucket item's crafting remainder supplies the returned bucket. Vanilla's Gunpowder container conversion preserves the Holy Water potion contents. JEI's NeoForge brewing adapter recognizes `BrewingRecipe` subclasses and reads this route automatically; its native potion/container discovery also includes the splash conversion. No JEI runtime dependency is added.

Vanilla downstream potion behavior is retained: Dragon's Breath can convert the splash form into a Lingering Potion of Consecration, and native tipped-arrow crafting uses those same contents. These forms share the same blessing/cooldown and have localized names; no separate stronger or longer potion tier is introduced.

The effect delegates to `HauntProtection.bless`, so existing cleansing ownership, personal-Haunt clamping, messages and protection logic remain authoritative. Existing saves need no migration. Recheck the supported brewing event and native potion APIs when porting Minecraft versions.

For development:

```text
/give @s minecraft:potion[potion_contents={potion:"mortal_expansion:holy_water"}]
/give @s minecraft:splash_potion[potion_contents={potion:"mortal_expansion:holy_water"}]
```

## Validation

Gameplay checks exercise an actual Brewing Stand and menu, all three filled outputs, returned bucket, normal fuel and Gunpowder consumption, rejecting incorrect/oversized inputs, native potion-content packet round-trips, survival drinking with returned glass, a real native splash effect application to nearby players, an out-of-range player, unchanged mob health, and shared placed-fluid/drink/splash cleansing cooldowns. Broader modpack recipe overrides and multiplayer balancing remain playtest work.

The production build, book text checks and generated-resource checks pass. All three new brewing/drinking/splash GameTests pass. The full 238-test suite currently reports one pre-existing encounter-spawn test failure (`loaded_trace_and_bounded_encounter_respect_conditions`); a retry reproduced that failure. Earlier runs also showed the known intermittent Ghost targeting check. These existing encounter failures are not reported as a passing full suite.
