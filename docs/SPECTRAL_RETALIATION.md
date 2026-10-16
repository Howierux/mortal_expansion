# Spirit and Elemental retaliation

Spirits and Elementals remain peaceful unless damaged by a living attacker. Native HurtByTargetGoal assigns the responsible mob or player (including the owner of an incoming projectile); environmental damage does not create an attacker. Vanilla target eligibility applies, including creative/spectator exclusions. Existing intangibility still decides whether an incoming hit damages them.

Once provoked they approach and fire visible raw-ectoplasm bolts within 16 blocks, about once every 40 ticks while they can aim. Each bolt deals 4 points of native indirect magic damage, with the shooter credited. Magic is accepted by the existing demi-tangible/intangible combat rules, including against Ghost Garb. Existing spectral protective armor reductions still apply. Bolts use native projectile collision, hit tangible or intangible targets, stop at blocks or entities and expire after 80 ticks. This is retaliation, not proactive aggression or group-wide anger.

An attacked Spirit closes its active trade so combat can take priority. Merchant offers, expertise and grave conversion remain intact. Other spirit-family creatures do not acquire these new goals.

The projectile is a registered native ThrowableItemProjectile rendered with the existing raw ectoplasm item; no extra textures, custom packets, damage schemas or mixins are needed. Persisted projectile ownership uses the native entity system. AI registration is confined to SpiritEntity and ElementalEntity; shared projectile behavior is in SpectralBoltEntity.
