# EMF Reader

Craft one reader with six iron ingots, two redstone and one Holy Salt:

```text
Iron      Iron       Iron
Redstone  Holy Salt  Redstone
Iron      Iron       Iron
```

Hold it in either hand to read the current chunk. Hold Use (right-click) to turn it inward and read personal Haunt; release to resume chunk readings. A labeled HUD displays the exact whole Haunt value and tier alongside six colored bulbs. The item itself lights the same bulbs.

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
