# Intangible vanilla Phantoms

Minecraft's `minecraft:phantom` is now in `mortal_expansion:intangible`. Existing saved and newly spawned vanilla Phantoms use the same damage immunities and exceptions as other tagged intangible mobs. Ordinary damage is rejected; magic/magical damage, demi-tangible attacks, intangible attackers, sanctification and the other existing exceptions still apply. This concerns the vanilla flying Phantom; Mortal Expansion's separate humanoid Phantom remains governed by its existing rules.

The existing client/server collision hook routes tagged vanilla Phantoms through the shared spectral phasing solver: ordinary walls and ceilings can be crossed, while downward collision retains floors. Spiritwood, Gravestone and other `intangible_solid` blocks stop phasing. Holy Salt columns and linked Spirit Torch barriers stop swept horizontal/upward movement at every height, and removing their sources releases the boundary. Horizontal path checks do not enter unloaded chunks. The tag can be adjusted by datapacks.

Consecrated enclosures continue to apply Sanctified through the existing generic mob handler. Vanilla Phantoms are already in Minecraft's Smite-sensitive tag, and sanctification damage bypasses intangible defenses, so an enclosed Phantom remains vulnerable to the area's periodic damage.

No new mob, saved fields, packets, goals, renderer or mixins are added. The vanilla appearance, insomnia spawning, drops, movement AI and daylight logic remain native; damage from those systems is subject to the shared intangible rules. Migration checks should preserve the Phantom tag, the existing collision interception and native Smite classification.

Tests exercise actual damage rejection/exceptions, horizontal and upward phasing, floor collision, spectral-solid collision, salt and linked-torch barriers, and barrier removal. Existing enclosure tests cover the common Sanctified refresh mechanism. Client movement presentation and encounter balance remain playtest checks.
