package net.foxelfire.tutorialmod.entity.custom;


import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class CedarBoatEntity extends Entity {

    public CedarBoatEntity(EntityType<? extends CedarBoatEntity> entityType, World world) {
        super(entityType, world);
    }

    public Item asItem(){
        return ModItems.CEDAR_BOAT_ITEM;
    }
 
    protected int getMaxPassengers() {
        return 4;
    }

    @Override
    protected void initDataTracker() {
        
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) {
        
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound var1) {
        
    }

}
