package net.foxelfire.longer_boats.entity;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.foxelfire.longer_boats.entity.custom.LongBoatEntity;
import net.foxelfire.longer_boats.entity.custom.LongRaftEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class ModEntities {
    public static final EntityType<LongBoatEntity> LONG_BOAT = Registry.register(Registries.ENTITY_TYPE, Identifier.of(LongerBoatsMod.MOD_ID, "long_boat"),
    EntityType.Builder.create(LongBoatEntity::new, SpawnGroup.MISC)
    .dimensions(2.5f, 0.5f)
    .passengerAttachments(
        new Vec3d[] {
                new Vec3d(0, 0.25, 1.2),
                new Vec3d(0, 0.25, 0.2),
                new Vec3d(0, 0.25, -0.8),
                new Vec3d(0, 0.25, -1.8)
        }
    )
            .build());
    public static final EntityType<LongRaftEntity> LONG_RAFT = Registry.register(Registries.ENTITY_TYPE, Identifier.of(LongerBoatsMod.MOD_ID, "long_raft"),
    EntityType.Builder.create(LongRaftEntity::new, SpawnGroup.MISC)
    .dimensions(2.5f, 0.5f)
    .passengerAttachments(
        new Vec3d[] {
                new Vec3d(0, 0.8, 1.2),
                new Vec3d(0, 0.8, 0.2),
                new Vec3d(0, 0.8, -0.8),
                new Vec3d(0, 0.8, -1.8)
        }
    ).build());
}
