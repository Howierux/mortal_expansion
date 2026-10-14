package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.entity.mob.ElementalEntity;
import com.howierux.mortalexpansion.entity.mob.GhostEntity;
import com.howierux.mortalexpansion.entity.mob.HaintEntity;
import com.howierux.mortalexpansion.entity.mob.HauntEntity;
import com.howierux.mortalexpansion.entity.mob.MummyEntity;
import com.howierux.mortalexpansion.entity.mob.PharaohlEntity;
import com.howierux.mortalexpansion.entity.mob.EmpoweredPhantomEntity;
import com.howierux.mortalexpansion.entity.mob.SpiritEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MortalExpansion.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<com.howierux.mortalexpansion.entity.PlayerCorpseEntity>> PLAYER_CORPSE =
            ENTITY_TYPES.register("player_corpse", () -> EntityType.Builder
                    .of(com.howierux.mortalexpansion.entity.PlayerCorpseEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.3F).clientTrackingRange(10).fireImmune().build("player_corpse"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.howierux.mortalexpansion.entity.mob.MortalGolemEntity>> MORTAL_GOLEM =
            ENTITY_TYPES.register("mortal_golem",()->EntityType.Builder
                    .of(com.howierux.mortalexpansion.entity.mob.MortalGolemEntity::new,MobCategory.CREATURE)
                    .sized(1.4F,2.7F).clientTrackingRange(10).build("mortal_golem"));
    public static final DeferredHolder<EntityType<?>, EntityType<GhostEntity>> GHOST =
            ENTITY_TYPES.register("ghost", () -> EntityType.Builder
                    .of(GhostEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("ghost"));

    public static final DeferredHolder<EntityType<?>, EntityType<EmpoweredPhantomEntity>> PHANTOM =
            ENTITY_TYPES.register("phantom", () -> EntityType.Builder
                    .of(EmpoweredPhantomEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("phantom"));

    public static final DeferredHolder<EntityType<?>, EntityType<SpiritEntity>> SPIRIT =
            ENTITY_TYPES.register("spirit", () -> EntityType.Builder
                    .of(SpiritEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .build("spirit"));

    public static final DeferredHolder<EntityType<?>, EntityType<ElementalEntity>> ELEMENTAL =
            ENTITY_TYPES.register("elemental", () -> EntityType.Builder
                    .of(ElementalEntity::new, MobCategory.CREATURE)
                    .sized(0.8F, 2.1F)
                    .clientTrackingRange(8)
                    .build("elemental"));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntEntity>> HAUNT =
            ENTITY_TYPES.register("haunt", () -> EntityType.Builder
                    .of(HauntEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.1F)
                    .clientTrackingRange(10)
                    .build("haunt"));

    public static final DeferredHolder<EntityType<?>, EntityType<HaintEntity>> HAINT =
            ENTITY_TYPES.register("haint", () -> EntityType.Builder
                    .of(HaintEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 2.4F)
                    .clientTrackingRange(10)
                    .build("haint"));

    public static final DeferredHolder<EntityType<?>, EntityType<MummyEntity>> MUMMY =
            ENTITY_TYPES.register("mummy", () -> EntityType.Builder
                    .of(MummyEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("mummy"));

    public static final DeferredHolder<EntityType<?>, EntityType<PharaohlEntity>> PHARAOHL =
            ENTITY_TYPES.register("pharaohl", () -> EntityType.Builder
                    .of(PharaohlEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.5F)
                    .clientTrackingRange(12)
                    .build("pharaohl"));

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(ModEntities::registerAttributes);
        modBus.addListener(ModEntities::registerSpawnPlacements);
    }

    private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                SPIRIT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                GHOST.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                PHANTOM.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                HAUNT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PLAYER_CORPSE.get(), net.minecraft.world.entity.LivingEntity.createLivingAttributes().build());
        event.put(MORTAL_GOLEM.get(), com.howierux.mortalexpansion.entity.mob.MortalGolemEntity.createAttributes().build());
        event.put(GHOST.get(), GhostEntity.createAttributes().build());
        event.put(PHANTOM.get(), EmpoweredPhantomEntity.createAttributes().build());
        event.put(SPIRIT.get(), SpiritEntity.createAttributes().build());
        event.put(ELEMENTAL.get(), ElementalEntity.createAttributes().build());
        event.put(HAUNT.get(), HauntEntity.createAttributes().build());
        event.put(HAINT.get(), HaintEntity.createAttributes().build());
        event.put(MUMMY.get(), MummyEntity.createAttributes().build());
        event.put(PHARAOHL.get(), PharaohlEntity.createAttributes().build());
    }
}
