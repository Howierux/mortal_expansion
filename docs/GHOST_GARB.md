# Ghost's Garb

Ectothread is crafted from eight String around one Raw Ectoplasm, producing eight
Ectothread. It uses a temporary green ectoplasm-infused thread sprite. Craft the four
Ghost's Garb pieces with standard helmet, chestplate, leggings, and boots patterns
using Ectothread. Default lime-dyed leather item/armor textures are placeholders.
The items appear in the Mortal Expansion creative tab and support normal armor
enchantments, dyeing, and repair with Ectothread.

Protection is leather strength: hood 1, robe 3, leggings 2, boots 1 (7 total).
Durability is iron strength: 165, 240, 225, and 195 respectively. There is no
armor toughness or knockback resistance.

A full four-piece set grants horizontal wall and upward ceiling phasing and intangible combat rules.
Downward floor collision remains solid; Spiritwood/other intangible-solid
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
Purified Ectothread uses a temporary icy-blue ectoplasm-infused thread sprite. The four Haint's
Garb pieces use standard armor crafting patterns with Purified Ectothread, and
light blue dyed leather item and worn armor placeholders. Haint's Garb has the
same defense, durability, enchantability, intangibility, and haunt protection
as Ghost's Garb; repair uses Purified Ectothread. Both sets belong to the shared
`mortal_expansion:spectral_garb` tag, allowing mixed Ghost/Haint pieces to retain
full-set benefits. Separate `ghosts_garb` and `haints_garb` tags retain their identities.

A full four-piece Haint-family set adds 0.3 to native jump strength (0.42 -> 0.72),
equivalent to Jump Boost III, and stacks with native jump effects. Haint and
Spectralite Haint pieces can be mixed for this bonus. Any Ghost piece removes the
jump bonus while retaining mixed-garb phasing and haunt protection. A transient,
stably keyed attribute modifier is maintained before player ticks on both client
and server, supporting predicted movement, equipment changes, respawn, and reload
without new saved fields or potion-effect replacement.

## Spectralite Haint and smithing upgrades

Leave the smithing template slot empty for both upgrades:

- Ghost Garb part + one Pure Ectoplasm -> corresponding Haint Garb part.
- Haint Garb part + corresponding Spectralite armor part -> Spectralite Haint part.

The base piece supplies enchantments, custom name, damage, and other custom
components through vanilla smithing transformation. Addition-piece enchantments
are not merged. Each recipe consumes one base and one addition; mismatched armor
parts and an occupied template slot do not match. Upgrading default Ghost Garb
changes its placeholder to light blue; deliberately dyed colors remain custom
components and are preserved.

Combined armor shares the Spectralite material: defense 3/8/6/3, toughness 3 and
knockback resistance 0.1 per piece, durability 715/1040/975/845, enchantability 15,
Netherite ingot repair, fire-resistant item properties, and current Spectralite
Netherite placeholders. Each piece also reduces incoming damage from intangible
attackers by 5%, up to 20% with four qualifying pieces. The generated
`spectral_protective_armor` tag covers Bloodstone, Spectralite and combined armor;
the generated Haint and shared garb tags supply full-set abilities. Normal armor
mitigation applies after this spectral reduction. All garb items describe their
benefits and full-set conditions in tooltips.

TemplateFreeSmithingRecipe uses the supported smithing recipe/serializer APIs and
Mojang ingredient/item-stack codecs. It inherits vanilla matching and assembly;
only recipe completeness omits the intentionally empty template. Vanilla's
SmithingTransformRecipe marks Ingredient.EMPTY as incomplete in this version.
No smithing UI mixin is needed. During migration verify empty-template matching,
menu result pickup/input consumption, component preservation and serializer
network roundtrips. Recipes, tags and models remain generated by Java providers.
Server tests cover all eight upgrades in the actual SmithingMenu, wrong-part and
occupied-template rejection, enchant/name/wear preservation, client recipe
network roundtrips, actual jump velocity and removal, combined armor stats,
spectral reduction, and wall-air consumption. Manual client movement/appearance
remains for playtesting.

Spirit Torch boundaries update on client block transitions as well as server
placement/removal. Breaking either standing or wall torch endpoints releases
the boundary immediately, and denied client predictions restore the barrier.
`SpiritTorchClientUpdatesMixin` observes `Level.onBlockStateChange` because
client chunk writes skip block lifecycle callbacks and NeoForge has no general
client block-change event. Verify this hook and inherited ClientLevel behavior
during version migration; keep updates tied to actual states rather than
pre-break events or timed polling.

## Holding breath inside walls

With a full Ghost/Haint (or mixed) set, the normal air meter drains whenever
the player's head intersects a block collision shape. Feet-only overlap and
standing on floors do not consume air; transparent solid walls such as glass
also count. The existing underwater HUD shows the depleted air meter.

NeoForge's `LivingBreatheEvent` retains Minecraft's native consumption amount,
including the `OXYGEN_BONUS` attribute supplied by Respiration. Normal capacity
is 300 ticks (15 seconds) with one air consumed each tick; Respiration slows
consumption with the same chance as underwater. Air refills four per tick after
the head emerges. Water Breathing/Conduit Power and creative invulnerability
prevent depletion without refilling air while inside walls. Spectators and
partial garb sets do not use this rule.

After air reaches -20, the native drowning tick resets it to zero and inflicts
two damage (one heart), repeating on the standard cadence. Drowning bypasses
player garb intangibility both underwater and inside walls, including full Ghost,
Haint and Spectralite Haint sets. Respiration, Water Breathing and other vanilla
air rules still apply; other intangible damage rules remain unchanged.
During migration, verify the breathing event's consumption/refill contract and
the native Respiration oxygen attribute. Server tests cover real base ticks,
head/feet/glass geometry, depletion/damage/refill, Haint Respiration III, potion
and creative exemptions. Manual HUD and client movement still need playtesting.

2026-10-05: corrected normal underwater drowning immunity. Build and all 72 required server tests pass, including actual native submerged air exhaustion for all three garb families.
