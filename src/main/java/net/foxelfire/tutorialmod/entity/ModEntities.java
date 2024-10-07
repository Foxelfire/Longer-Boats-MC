package net.foxelfire.tutorialmod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.foxelfire.tutorialmod.entity.custom.PorcupineEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<PorcupineEntity> PORCUPINE = Registry.register(Registries.ENTITY_TYPE, new Identifier(TutorialMod.MOD_ID, "porcupine"),
    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PorcupineEntity::new)
    .dimensions(EntityDimensions.fixed(1f, 1f)).build());

    public static final EntityType<CedarBoatEntity> CEDAR_BOAT = Registry.register(Registries.ENTITY_TYPE, new Identifier(TutorialMod.MOD_ID, "cedar_boat"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, CedarBoatEntity::new)
    .dimensions(EntityDimensions.fixed(1f, 1f)).build()); // dimensions are to be decided

    public static void registerModEntities() {
        TutorialMod.LOGGER.info("Registering Entities for " + TutorialMod.MOD_ID);
        FabricDefaultAttributeRegistry.register(ModEntities.PORCUPINE, PorcupineEntity.createPorcupineAttributes());
    }
}
