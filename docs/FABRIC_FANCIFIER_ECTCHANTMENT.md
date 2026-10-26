# Fabric Ectchantment Machine and Fancifier

## Ectchantment

The native three-slot machine accepts input, ectoplasm and output. Raw ectoplasm adds 1,000 charge; purified adds 4,000, up to 16,000. Each operation consumes 4,000 charge, or 40,000 energy when charge is insufficient. Energy capacity is 160,000 with transactional Fabric energy input. Output blockage cannot spend the input or operation cost. Inventories, charge and power persist; sided item storage permits extracting output only.

Reference transformations include Diamond to Bloodstone, Stone to Gravestone, logs to Spiritwood, saplings to Spiritwood Saplings, and vines/moss/lichen to Grave Moss. Other eligible items receive the native Demi-Tangible component, retaining their existing components. The screen displays charge, power, recent power input and status. Its recipe is now generated alongside the Fancifier recipe and the previously missing Spiritwood building/Spirit Torch/diamond-from-Bloodstone recipes.

## Fancifier

Any native BlockItem can receive an integer opacity from 1–100 through a draggable slider or numeric field with a live checkerboard preview. Apply processes the available stack up to output capacity; closing returns unclaimed inputs/output. At 100% the cosmetic component is removed. Native blocks retain their real identity, collision, inventory, redstone and other behavior.

The persistent item component and immutable, chunk-local Fabric attachment store opacity independently of the real block state. Native placement and drops, same-block state changes, multiblock placement, falling blocks and piston transitions carry the metadata. Loaded clients receive chunk attachments; immutable snapshots feed mesh queries. The renderer suppresses ordinary faded model rendering and draws an alpha pass, adapts block entity/item buffers, and retains the chest/redstone overlap sorting fix and low-opacity shader. The Fancifier blade uses the translucent layer.

Validation: native tests cover energy rollback, blocked output, NBT state, transformation, opacity bounds, component-preserving stack conversion/restoration, close returns, native chest identity/inventory and drops/removal cleanup. An integrated client at 30% volume verified actual machine and Spirit screens, draggable slider synchronization/application and faded chest/redstone/glass rendering. A second launch verified chunk opacity, chest inventory, active quest and machine power persistence. Temporary probes are removed before building the published jar.

Boundaries: vanilla model rendering is verified. Third-party Fabric Renderer API models, connected-texture packs, Sodium/Iris/shaders and unusual custom renderers require dedicated interoperability work; behavior preservation does not guarantee those renderers honor opacity. Legacy NeoForge wrapper-block saves are not migrated. There is no claim of NeoForge-world compatibility.

Migration: Fabric has no general cosmetic block-state property or alpha override covering native block entities, drops, piston and falling transitions. Focused mixins isolate these version-sensitive hooks; codecs/tags and native menus handle persistence and data. Keep their placement/drop/render integration tests when updating Minecraft.
