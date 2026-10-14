# Pharaohl Scepter charge

The scepter holds 12 charges, shown with a cyan inventory/item bar and a numeric tooltip. It starts empty, including existing saved scepters without the new component. Each successful activation summons up to two allied Mummies, spends one charge and retains the existing 15-second cooldown. Empty, cooldown-blocked and fully canceled summons spend nothing. Charges are still required in creative mode.

In an inventory, carry raw or purified Ectoplasm on the cursor and right-click the scepter slot. Each click consumes one material: raw adds four charges (three fill an empty scepter); purified fills all twelve. Partial raw refills cap at twelve; already-full scepters consume nothing. Invalid materials and left-click retain normal inventory behavior. This interaction reloads only; it does not activate the scepter.

Charge is persisted and synchronized through `mortal_expansion:scepter_charge`, an integer data component bounded 0–12. Uses and reloads share the same charge helper. Supported Item inventory-click/bar/tooltip APIs handle the UI; no new packets or mixins are needed. Preserve the component key and empty default on version migrations, and verify inventory cursor/slot synchronization, item save/load and summon cancellation semantics.

The Book of Ectcetera's scepter entry describes the capacity, cost, recharge materials and inventory interaction.
