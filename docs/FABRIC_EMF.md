# Fabric EMF readers

Both readers now sample the server's exact chunk and personal Haunt values while held in either hand. Holding use switches to personal Haunt; releasing returns to chunk Haunt. Right-clicking a block with a reader selects personal mode before the block interaction. Sampling only accesses the player's already-loaded current chunk.

Native model predicates are normalized to 0–1, matching Fabric/vanilla clamped predicates. This also corrects higher-tier book model selection.

The basic reader uses the existing six-bulb model and has no HUD. A small client-only hand-pose mixin replaces vanilla's generic item transform so the bulbs face the camera in either hand and the use pose moves the reader inward. Fabric has no equivalent first-person item-transform callback.

The precise reader uses Fabric's built-in item renderer and additional baked models. It draws the live number to two decimal places directly on both device faces, and retains the reference's precise-reader HUD. It renders `--.--` until an authoritative sample arrives. Bulb colors and future overflow models are retained, although personal Haunt above 69 awaits the Darkhold slice.

Samples are transient item components, synchronized through the normal item stack network codec. They are not written to item NBT. Owner UUID, dimension and chunk coordinates invalidate readings after transfer or movement, and unequipped readers clear their samples. An unchanged sample does not cause a component rewrite.

Native generated crafting matches NeoForge: six iron ingots across the top/bottom rows, redstone either side of Holy Salt in the center row. The precise upgrade is a shapeless basic reader plus raw ectoplasm.

Validation includes a native server test for offhand sampling, exact hundredths, held personal mode, stale chunk rejection, unsaved sample components, unequipped cleanup and display bands. Integrated-client checks verified chunk 23.45 and personal 45.67 through native held-use networking. Screenshots confirmed the basic bulb count without a HUD, readable precise numbers and the inward use pose. Temporary probes were removed. Other equipment powers and full Haunt-event parity remain separate work.
