package net.foxelfire.longer_boats.entity;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.foxelfire.longer_boats.entity.custom.LongBoatEntity;
import net.foxelfire.longer_boats.entity.custom.LongRaftEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<LongBoatEntity> LONG_BOAT = Registry.register(Registries.ENTITY_TYPE, Identifier.of(LongerBoatsMod.MOD_ID, "long_boat"),
    EntityType.Builder.create(LongBoatEntity::new, SpawnGroup.MISC)
    .dimensions(2.5f, 0.5f).build());
    public static final EntityType<LongRaftEntity> LONG_RAFT = Registry.register(Registries.ENTITY_TYPE, Identifier.of(LongerBoatsMod.MOD_ID, "long_raft"),
    EntityType.Builder.create(LongRaftEntity::new, SpawnGroup.MISC)
    .dimensions(2.5f, 0.5f).build());
}
