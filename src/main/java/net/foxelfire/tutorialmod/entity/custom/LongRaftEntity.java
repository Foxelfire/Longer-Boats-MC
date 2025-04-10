package net.foxelfire.tutorialmod.entity.custom;

import org.joml.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
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

    @Override
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        float zPosition = 0.0f;
        if(!this.getFirstAvailableSeat(passenger).isEmpty()){
            zPosition = this.seatIndexesToPositions.get(getFirstAvailableSeat(passenger).get());
        } else if(zPosition == 0.0f){
            passenger.stopRiding();
        }
        return new Vector3f(0.0f, 0.7f, zPosition);
    }
}
