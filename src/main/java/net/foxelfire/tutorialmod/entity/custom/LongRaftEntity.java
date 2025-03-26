package net.foxelfire.tutorialmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class LongRaftEntity extends AbstractLongBoatEntity{

    public LongRaftEntity(EntityType<? extends AbstractLongBoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void setVariant(LongBoatVariant variant) {

    }

    @Override
    public LongBoatVariant getVariant() {
        return LongBoatVariant.BAMBOO;
    }
}
