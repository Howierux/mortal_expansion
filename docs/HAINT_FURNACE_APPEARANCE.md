# Haint Furnace Haunt-powered front

The Haint Furnace displays a ghast-eyed front only while it is actively advancing cooking in its existing free-burn mode (Demonic chunk Haunt or higher). Fuel-powered cooking uses closed ghast eyelids with a lit furnace mouth. Idle cooking and Haunt interference pauses use the same closed eyelids with a dark mouth. Fuel costs, starting fuel requirements, preservation rules and cooking speed are unchanged.

A server-updated `haunt_powered` block-state boolean drives the generated model, independently of facing and the existing `lit` flag. All four directions have idle, fuel-lit and Haunt-lit mappings. Existing worlds load the added property as false and the next cooking tick updates it; inventory and block entity save data need no migration. No new mixins, custom renderers or synchronization packets are required.

The new 16×16 opaque front texture at `src/main/resources/assets/mortal_expansion/textures/block/haint_furnace_haunt_front_temporary.png` keeps furnace stone/fire and adds ghast-like red eyes. Existing icy-blue Haint block tint is preserved. Built-in imagegen edited the vanilla furnace reference using ghast eyes as reference, then nearest-neighbor sampling exported it. Original master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-9aa31573-34ad-4a24-a7b0-5c2a0ee24532.png`.

Prompt: Use case: precise-object-edit. Asset type: Minecraft furnace FRONT block texture. First reference is the furnace front-on texture edit target. Second reference is a ghast texture atlas, use ONLY its paired square red angry ghast eyes as design reference. Keep exact furnace square gray stone front, border, top vent and lower burning opening. Add a pair of distinctive square ghast eyes on the upper front stone panel where the vent is, dark recesses with red pale highlights like the ghast eyes. Lower furnace opening retains fire. Neutral GRAYSCALE stone (game applies icy-blue tint), red eyes and orange fire remain colored. Strict flat orthographic 16x16-style crisp pixel-art texture enlarged to a square, full canvas opaque, no perspective, no background, no text, no additional faces or atlas panels. Do not change the overall furnace layout.

## Closed-eye fronts

Idle: `src/main/resources/assets/mortal_expansion/textures/block/haint_furnace_closed_front_temporary.png`.
Fuel cooking / below free-burn Haunt: `src/main/resources/assets/mortal_expansion/textures/block/haint_furnace_closed_front_on_temporary.png`.
Both are opaque 16×16 exports sampled from built-in imagegen masters. Gray stone preserves the existing icy-blue game tint. Model data generation selects these front textures; Haunt-powered cooking keeps its existing open red eyes. Inventory/block item uses the idle model automatically. No gameplay behavior changes.

Idle master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-92dc0a8d-eac3-451a-8724-285d6186034e.png`.
Prompt: Edit the existing Haint Furnace front; preserve square stone furnace layout and border; change red ghast eyes to horizontal dark closed eyelid slits with downward corners; idle lower mouth completely dark without fire; neutral grayscale stone because game applies blue tint; flat opaque crisp pixel-art front, no perspective or text.

Fuel-cooking master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-38ddf2cb-435e-4522-8b04-1afb0c5349a7.png`.
Prompt: Edit the closed-eye idle front to fuel-cooking; preserve both closed eyelids and gray stone; change only the lower mouth to orange/yellow furnace fire; no red eye glow; exact same flat opaque pixel-art composition.
