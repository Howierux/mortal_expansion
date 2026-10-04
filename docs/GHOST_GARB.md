# Ghost's Garb

Ectothread is crafted from eight String around one Raw Ectoplasm, producing eight
Ectothread. It uses the vanilla lime wool model as a placeholder. Craft the four
Ghost's Garb pieces with standard helmet, chestplate, leggings, and boots patterns
using Ectothread. Default lime-dyed leather item/armor textures are placeholders.
The items appear in the Mortal Expansion creative tab and support normal armor
enchantments, dyeing, and repair with Ectothread.

Protection is leather strength: hood 1, robe 3, leggings 2, boots 1 (7 total).
Durability is iron strength: 165, 240, 225, and 195 respectively. There is no
armor toughness or knockback resistance.

A full four-piece set grants horizontal wall phasing and intangible combat rules.
Floors and vertical collision remain solid; Spiritwood/other intangible-solid
blocks, Holy Salt columns, and Spirit Torch barriers block phasing. Movement is
checked along the swept path on client and server. There is no spectator mode,
flight, or downward floor phasing. The body renders at 20% opacity; armor retains
its visible lime leather placeholder appearance. Removing a piece immediately
removes full-set abilities. Ordinary in-wall overlays are suppressed while phasing.

Ordinary physical/environmental damage is blocked through the shared intangible
damage handler. Intangible attackers, explosions, spectral arrows, Ghast fireballs,
demi-tangible weapons/enchantments, and sanctification remain able to hurt the
wearer. Magical damage also bypasses intangibility: NeoForge's `is_magic` tag
and damage tags with the final path component `magic`, `magical`, `is_magic`, or
`is_magical` in any namespace. This includes vanilla sources categorized by
NeoForge as magic (such as poison, Wither damage, Thorns, and dragon breath).
Matching tags permit damage through intangibility; ordinary armor, other damage
immunities, and potion-effect susceptibility still follow their existing rules.
Administrative/void sources that bypass invulnerability remain effective.
Intangible attackers now qualify in the shared damage API for other intangible
targets as well. Applicable damage still applies normal armor and durability rules.

The full set prevents negative ambient haunt events, keeps beneficial events, and
clears only currently haunt-owned negative potion effects when equipped. External
potion effects are preserved. Existing visitation repair at Dead Rising or above
remains one durability per worn tagged piece.

Version migration: registrations, models, recipes, and item tags are code-based;
regenerate resources after changes. Collision, server wall validation, client wall
ejection, and body rendering require narrowly scoped mixins because this version
has no supported event replacing those operations. GhostGarbPhasing contains the
shared gameplay logic. No transient noPhysics/game-mode changes or new persisted
player state are introduced; equipment determines the ability.

Demi-tangible player and mob melee hits use generated damage types tagged
`neoforge:is_magic`. This includes tagged weapons, the Demi-Tangible component,
and legacy enchanted test items. Armor, difficulty scaling, death messages, and
attacker/weapon credit retain normal melee behavior. `DemiTangibleDamageSourcesMixin`
converts the two melee factories because NeoForge incoming-damage events cannot
replace their damage source. During migration, verify those factory signatures
and registry tag loading before enabling combat.

## Haint's Garb

Eight String surrounding one Pure Ectoplasm produce eight Purified Ectothread.
Purified Ectothread uses light blue wool as its placeholder. The four Haint's
Garb pieces use standard armor crafting patterns with Purified Ectothread, and
light blue dyed leather item and worn armor placeholders. Haint's Garb has the
same defense, durability, enchantability, intangibility, and haunt protection
as Ghost's Garb; repair uses Purified Ectothread. Both sets belong to the shared
`mortal_expansion:spectral_garb` tag, allowing mixed Ghost/Haint pieces to retain
full-set benefits. Separate `ghosts_garb` and `haints_garb` tags retain their identities.

A later Spectralite combination will add both sets of benefits; no combination
recipe or additional Spectralite attributes are included yet.
