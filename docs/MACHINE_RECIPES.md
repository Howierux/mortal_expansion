# Machine crafting recipes

Each shaped 3x3 recipe produces one machine. The border follows the leggings pattern with the two main items filling its center gaps. The Enchantment Generator replaces the top-center Iron Ingot with Redstone Dust.

## Enchantment Generator

```text
Iron    Redstone       Iron
Iron    Grindstone     Iron
Iron    Haunt Furnace  Iron
```

Total: 6 Iron Ingots, 1 Grindstone, 1 Redstone Dust, 1 Haunt Furnace.

## Ectoplasmic Generator

```text
Gold    Gold           Gold
Gold    Raw Ectoplasm  Gold
Gold    Haunt Furnace  Gold
```

Total: 7 Gold Ingots, 1 Raw Ectoplasm, 1 Haunt Furnace.

## Ectchantment Machine

```text
Purified Gravestone  Purified Gravestone  Purified Gravestone
Purified Gravestone  Enchanting Table     Purified Gravestone
Purified Gravestone  Purified Ectoplasm   Purified Gravestone
```

Total: 7 Purified Gravestone blocks, 1 Enchanting Table, 1 Purified Ectoplasm (`mortal_expansion:pure_ectoplasm`).

The generators unlock in the recipe book on obtaining a Haunt Furnace. The Ectchantment Machine unlocks on obtaining Purified Ectoplasm. Their ingredients remain normal crafting requirements; no additional Spectral Insight knowledge gate was added.

Recipes and unlock advancements are produced through the Java recipe data provider and committed with generated output. The machines are now obtainable in survival without commands. Existing machine IDs, functions and saved data are unchanged.

Validation: data generation and build pass. Generated layouts, ingredient counts, output IDs and one-item counts were checked. Guide validation and all 427 generated-resource checks pass. The existing 124 gameplay-test baseline is unchanged; no new tests were added for these recipe-only changes.
