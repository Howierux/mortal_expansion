# Fabric equipment slice

Implemented on `fabric-dev`, Minecraft 1.21.1 / Java 21. Root NeoForge sources remain the reference. This slice adds gameplay to the existing native equipment IDs, stats, armor layers and item art; it does not claim full mod parity or NeoForge save compatibility.

## Garb and armor

- Four equipped pieces in `mortal_expansion:spectral_garb` enable horizontal and upward phasing. Ghost, Haint and Spectralite Haint pieces can mix. Ordinary floors, spectral-solid materials, loaded-chunk boundaries, Holy Salt and Spirit Torch walls retain collision.
- Four pieces in `mortal_expansion:haints_garb` add 0.30 native jump strength. Haint and Spectralite Haint can mix; a Ghost piece keeps phasing but removes the full Haint jump. Transient bonuses are updated before predicted movement and disappear when a set breaks.
- Collision at the eyes, including glass, enters Minecraft's existing underwater-air branch. Respiration, Water Breathing, creative immunity, air refill and the native bubble meter remain in control. Ordinary drowning and exhausted wall air damage garb wearers; magic, intangible attackers and void damage retain the shared combat exceptions.
- The player's rendered body uses 20% opacity. Armor keeps its woven/hybrid art. Only the opaque block overlay and normal wall-ejection behavior are suppressed; fire/water overlays and unrelated movement validation remain.
- Bloodstone, Spectralite and Spectralite Haint armor reduce damage from intangible attackers by 5% per equipped piece, up to 20%, before normal armor handling. Existing native trim support remains.
- Garb tooltips summarize the implemented equipment rules. `EquipmentRuntime.preventHaunting` is the post-selection negative-event gate and sends the gear-prevented notice. **Missing function:** the full negative Haunt-event dispatcher and its owned-effect cleanup/garb-repair triggers are not yet ported. Natural prevention notices cannot occur until that dispatcher calls the gate. Ordinary negative potions are not removed by wearing garb.

## Tools and Scepter

| Equipment | Behavior |
| --- | --- |
| Bloodstone held tools/sword | Damage, attack speed and mining speed scale from 0.9x at Void through 1.0x at Natural to 1.5x at Eldritch. Above-Eldritch exposure retains the Eldritch multiplier. |
| Both pickaxes | Mining speed rises linearly with wear, up to 2x. Damaged inventory or dropped pickaxes repair one durability for 0.01 chunk Haunt. Tier intervals: Void disabled; Natural 1,200; Supernatural 600; Dead Rising 300; Demonic 200; Abominable 100; Eldritch 40 ticks. No offline catch-up burst. |
| Both shovels | Mine a 3x3x1 plane determined by the actual clicked block face. Crouching bypasses the area ability. |
| Spectralite shovel | Hold right-click for two seconds and release onto a shovel-mineable block to harvest at most 64 connected blocks. |
| Both axes | Fell connected same-family logs when a natural canopy is present. Search bounds: 512 logs, 16 horizontal and 64 vertical blocks. Crouch to mine one block. |
| Spectralite axe | Right-click a sapling for at most 15 native bonemeal attempts. Costs 0.10 chunk Haunt once after an accepted first pulse, even if the tree remains blocked. |
| Both hoes | Successful native tilling produces Haunted Farmland, preserving moisture. Soil random ticks apply free bonemeal with denominators 16/8/4/3/2/1 from Natural to Eldritch; Void disables the bonus. Above Supernatural it remains moist. Vanilla crop/stem/pitcher survival and crop hydration calculations recognize the soil. |
| Spectralite hoe | Right-click a crop for up to 16 native bonemeal pulses toward maturity, spending one chunk Haunt per successful pulse. |
| Pharaohl Scepter | Twelve charges and a visible ammo bar. Right-click cursor-held raw/purified Ectoplasm onto the Scepter in inventory to add four/twelve charges; full bars consume nothing. A successful use summons up to two owner-allied Mummies, spends one charge and starts a fifteen-second cooldown. Blocked summons spend nothing. |

Area/tree harvests run through `ServerPlayerGameMode.destroyBlock`, including Fabric break cancellation, native drops and durability. Jobs confirm the initial break, retain the original tool/player/dimension, stop on tool loss and never discover unloaded terrain. Connected harvests cannot use a protected block as a bridge. Jobs process at most 32 candidates per tick per job. Growth checks the loaded neighborhood before invoking the native bonemeal helper. Modded crop/tree behavior and third-party claim mods still require interoperability playtesting.

Twenty native generated crafting/smithing recipes cover Bloodstone equipment, Spectralite upgrades and the two Gravestone swords. Existing secret-gated garb recipes stay under book research. Providers and generated output are committed together.

## Persistence and migration boundaries

New item components: `mortal_expansion:scepter_charge` (persistent/networked integer 0..12, absent means empty) and `mortal_expansion:pickaxe_repair_time` (persistent server-side long). Existing item/block IDs and armor materials are unchanged. Tool jobs/face samples and equipment modifiers are transient. A rewound server clock resets a repair timer rather than applying catch-up repairs.

Focused mixins are needed because Fabric 1.21.1 has no matching supported hooks for the living air predicate, player collision rejection/ejection, body alpha, dropped-item ticks, final player break result or vanilla's hard-coded farmland checks. Keep those hooks isolated during version migration. Native classes still own air/oxygen, attributes, inventory clicks, bonemeal, armor rendering, harvesting and recipes.

## Verification

- Native data generation and the Fabric build pass; 37 required GameTests pass. Seven equipment tests exercise actual phasing, air exhaustion/drowning, Respiration/Water Breathing, mixed sets, damage rules, attribute removal, exact repair costs, protected face-based/connected harvesting, tree felling, native soil/crop growth and Scepter charging/summons.
- An isolated integrated client at 30% volume crossed an ordinary wall using real movement packets. The server accepted its position; the client air meter reached 264 and native Haint jump strength was 0.72. Wall-overlay suppression, woven/hybrid armor and Scepter bar were visually checked.
- Temporary client probes were removed before the production build. Dedicated multiplayer, third-party equipment/claim/crop mods, shaders and NeoForge world migration remain separate validation work.
