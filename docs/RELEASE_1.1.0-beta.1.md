# Mortal Expansion 1.1.0-beta.1 — NeoForge

This update expands haunted progression, customization and recipe-viewer support for Minecraft 1.21.1.

- Added the Darkhold upgrade, seven Secrets of the Damned and their unlockable powers, seven Demon sin variants and Demonic Ash.
- Added Ritual Stars for repeatable Overworld Haunt shifts through either altar.
- Added the Fancifier: set block opacity from 1–100% with a draggable slider, numeric entry and preview, including functional blocks.
- Added Holy Water bottling, drinkable cleansing bottles and brewed Splash Potions of Consecration.
- Integrated optional JEI recipe and machine/altar displays directly into Mortal Expansion. A separate Mortal Integrations addon is no longer needed.
- Made Haunt spread respond to moon phase, with three daily checks, and added symbolic moon-phase charms to Dreamcatchers.
- Improved mob appearances, Demon perception/daylight behavior, spawn balance, Spirit trade variety and Helpful Visitation rewards.
- Improved Bloodstone formation/scaling and diamond ingredient compatibility, Demi-Tangible weapon/projectile support, and vanilla Phantom intangibility.
- Improved Mortality Research contributions, Enchantment Generator screens, failed Spirit Journey respawns and translucent-block rendering.

Requires NeoForge 21.1.219 or newer for Minecraft 1.21.1, Java 21 and TerraBlender. JEI is optional. Use matching mod versions and dependencies on clients and servers. Back up existing worlds before updating. Features, artwork and balance remain in active development.

## Release preparation and validation

Prepared 2026-10-27 for promotion from dev to main. Version 1.1.0-beta.1, Beta, NeoForge only, Minecraft 1.21.1, Java 21, Client & Server. Required CurseForge relation: TerraBlender; optional: Just Enough Items. The standalone Mortal Integrations addon is obsolete.

The versioned build and all 238 required server GameTests pass. Initial runs exposed fixture interference: autonomous Ghost tests shared nearby mock players/targets, and an encounter test shared the global hostile cap with concurrent spawn tests. These tests now run in separate named batches; their assertions and gameplay rules are unchanged. Datagen verifies 674 resources and book validation verifies 30 chapters/128 pages. All seven independent CI rule checks pass; the legacy Haunt-policy assertion now correctly excludes effects explicitly marked unimplemented. Three generated Fancifier model files were refreshed to native datagen line endings, with no semantic changes.

Before public upload, review and merge the draft main promotion and finish a final playtest of the exact candidate. Back up existing NeoForge worlds. Multiplayer/modpack and renderer combinations remain playtest items; existing design proposals are not advertised as complete. No CurseForge upload or release tag is created by this preparation.
