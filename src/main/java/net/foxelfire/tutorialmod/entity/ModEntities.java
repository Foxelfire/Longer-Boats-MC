package net.foxelfire.tutorialmod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.LongBoatEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<LongBoatEntity> LONG_BOAT = Registry.register(Registries.ENTITY_TYPE, new Identifier(TutorialMod.MOD_ID, "long_boat"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, LongBoatEntity::new)
    .dimensions(EntityDimensions.fixed(2.5f, 0.5f)).build());
}
