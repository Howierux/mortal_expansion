# EMF Reader

Craft one reader with six iron ingots, two redstone and one Holy Salt:

```text
Iron      Iron       Iron
Redstone  Holy Salt  Redstone
Iron      Iron       Iron
```

Hold it in either hand to read the current chunk. Hold Use (right-click) to turn it inward and read personal Haunt; release to resume chunk readings. The basic reader shows its six tier bulbs on the held device with no HUD overlay. Its face stays toward the player in either hand and during personal scanning. The upgraded Precise EMF Reader retains its decimal device display and labeled HUD.

| Haunt | Tier | Lit bulbs |
| --- | --- | --- |
| 0–9 | Void | 0 |
| 10–19 | Natural | 1, green |
| 20–29 | Supernatural | 2, yellow-green |
| 30–39 | Dead Rising | 3, yellow |
| 40–49 | Demonic | 4, orange |
| 50–59 | Abominable | 5, red |
| 60–69 | Eldritch | 6, purple |

The sensor consumes no fuel or durability and does not raise or lower Haunt. The inward pose gives no shield protection. Holding Use also works when targeting a block.

## Implementation and migration

The server samples only the holder’s already-loaded chunk and personal Haunt through existing managers. A network-only item data component carries both values using framework stream codecs and normal inventory synchronization. Its owner, dimension and chunk scope prevent displaying a stale reading after transfers or movement. Saved items discard the transient reading and resample when held. No new saved-world schema or custom packets are introduced.

Client rendering uses supported item properties, client item extensions and GUI layer registration. All recipe and model resources come from Java data generators; the cuboid model uses vanilla textures. Bulb tier rules are isolated from rendering in EmfScale. Client poses and model orientation still require in-game visual playtesting.

## Precise EMF Reader upgrade

Combine one EMF Reader with one Raw Ectoplasm in any arrangement, including the inventory crafting grid. Both are consumed into one Precise EMF Reader. The upgrade retains chunk/personal controls and six tier bulbs and adds a live decimal number on both faces of the in-hand reader. The HUD also shows the decimal value. Personal readings expose actual hundredths (for example 24.37); chunk state now also stores hundredths and displays its actual fractional value, including small pickaxe repair costs.

A supported client item renderer combines registered baked bulb models with font rendering, avoiding a mixin or hundreds of pre-generated digit models. Renderer model coordinates and first/third-person transforms need manual visual playtesting. Unknown or out-of-scope samples display --.--.

The existing network-only sample now transmits personal hundredths as an integer, retaining the existing whole-point accessor for basic-reader behavior. Server and clients must run the same build; no persistent item or world format changes. Integer arithmetic and explicit formatting avoid floating-point drift or locale-dependent decimal punctuation.

2026-10-05 playtest correction: basic reader first-person model and hand poses now face the camera in both modes; the basic HUD is removed. Verify the revised appearance in-game.

Chunk hundredths were added for [pickaxe repair](HAUNT_PICKAXES.md). Whole/tier readings remain compatible with existing rules; the Precise reader exposes the new fractions.
