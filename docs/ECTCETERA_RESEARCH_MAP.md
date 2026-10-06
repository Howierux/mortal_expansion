# Book of Ectcetera research-map interface

The book now uses a Thaumonomicon-inspired research-browser format with original spectral colors and native Minecraft item icons. There are four category tabs: Foundations, Spirits, Ectoplasm and Purification. Each contains connected chapter nodes arranged on a draggable, zoomable map. The entry viewer uses a parchment-colored panel with Previous, Next and Research Map buttons instead of the old flat contents pages.

## Controls

- Click a category tab to switch maps.
- Drag empty map space to pan; scroll to zoom around the pointer.
- Center Map restores pan/zoom when the window has room for that control.
- Click an unlocked entry icon to read its existing pages.
- Previous/Next navigate within the chapter; scroll long entry text.
- Research Map or Escape returns from an entry to the map. Close/Escape on the map closes the book.

Unlocked nodes use a pale green border. Locked nodes show the required book ingredient or Spectral Insight guidance and cannot display their body text. Unrevealed weaving entries use a generic secret label/icon instead of disclosing their title. Category tabs can be inspected before their stage is unlocked, but this does not grant access. Connecting lines show topic relationships for navigation, not new research prerequisites or unlock actions.

## Existing progression remains authoritative

Book + Rotten Flesh starts the book. Bone, Raw Ectoplasm and Purified Ectoplasm upgrade it in order. Existing ingredient handoff chapters remain as map nodes. Ectothread, Ghost Garb and Haint Garb still unlock gradually through the same Spectral Insight events and personal Haunt thresholds. The existing raw-book Haint secret exception remains: a revealed Haint chapter can be read in the Purification tab before the final book upgrade. Spectralite Haint guidance within that chapter stays locked until its separate secret is known.

The map checks `EctceteraGuide.chapters(tier,secrets)` for every entry. Clicking nodes never writes knowledge, upgrades the book or grants recipes. The viewer reads the same per-stack tier/secret components and dynamic book guidance as before, refreshes while the book is held, and closes when that hand no longer holds it. The server recipe locks, item/attachment codecs, upgrading recipes, networking and saved-world data are unchanged.

## Implementation and verification

`EctceteraResearchMap` holds presentation coordinates and topic links separately from registration/progression. The client screen uses standard Minecraft Screen, Button, GuiGraphics, item rendering, scissoring and font wrapping. No Thaumcraft asset copies, dependency or new mixins are introduced. Migration checks: screen input callbacks, GUI draw/scissor APIs, held-stack synchronization and item icons.

Build and all 124 existing server tests pass, including book upgrade validation, retained book components and Spectral Insight knowledge/recipe restrictions. The 30-chapter text catalog and legacy 128-page metadata pass guide validation; 428 generated-resource checks pass. Mouse interactions, readability at different GUI scales and client visual layout still require playtesting. No new tests were added solely to mirror rendering code.

Format reference: the original Thaumcraft author's release notes describe the category-based research browser: https://www.curseforge.com/minecraft/mc-mods/thaumcraft/files/2264542 .
