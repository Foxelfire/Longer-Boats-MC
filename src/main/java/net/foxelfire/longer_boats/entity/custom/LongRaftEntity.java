package net.foxelfire.longer_boats.entity.custom;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
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
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        float zPosition = 0.0f;
        if(!this.getFirstAvailableSeat(passenger).isEmpty()){
            zPosition = this.seatIndexesToPositions.get(getFirstAvailableSeat(passenger).get());
        } else if(zPosition == 0.0f){
            passenger.stopRiding();
        }
        return new Vec3d(0.0f, 0.7f, zPosition);
    }

    @Override
    // We're always on the server here.
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
