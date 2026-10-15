# Ecto Pipes and Ectanks: block/model stage

Three new decorative blocks/items: `mortal_expansion:ecto_pipe`, `mortal_expansion:ectank` and `mortal_expansion:purified_ectank`. They can be crafted, placed, collected with appropriate mining tools and found in the mod creative tab. There are no fluid handlers, storage, transfers, leaking, power requirements, GUIs, block entities or tickers yet. Capacity values will be implemented in the functional stage; Purified Ectank is the intended higher-capacity variant.

## Recipes

Ectank: eight Gravestone around one Glass. Purified Ectank: eight Purified Gravestone around one Glass. Each tank recipe yields one.

Ecto Pipe: `SSS / G G / SSS`, where S is Gravestone and G is Glass; the center is empty. Produces 16 pipes.

## Models

Original, simple tech-style native block geometry: tanks have stone top/bottom rims and four corner pillars around a glass body. Pipes have a narrow glass core and glass arms with stone end collars. Models reuse the actual Gravestone, Purified Gravestone and vanilla Glass textures, so their materials match their recipes; no third-party model assets or new bitmap textures are used. Cutout rendering preserves the clear glass openings. Item models use the same tank geometry and a straight pipe section.

Pipes visually connect in all six directions to either tank variant or another Ecto Pipe. Placement, neighbor updates, rotation and mirroring use supported block-state APIs. Shape combinations are cached by the native PipeBlock class. Connections are cosmetic only and do not form a fluid-processing network. Tanks use a full-block collision shape; pipes use native narrow connected shapes.

Registration, Java data generators and generated recipes/models/states/loot/mining tags are committed together. Preserve registry IDs and the six native directional properties when adding functionality later. No custom packets or mixins are added. Generated resources and a real native-placement/neighbor-update test validate this stage; in-game appearance remains a playtest check.
