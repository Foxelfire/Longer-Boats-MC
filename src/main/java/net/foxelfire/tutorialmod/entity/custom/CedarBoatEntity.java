package net.foxelfire.tutorialmod.entity.custom;


import net.foxelfire.tutorialmod.TutorialMod;
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

    // STINKY CODE WARNING:
    // the following overrides are *only* to remove references in BoatEntity to getVariant(). We can't inherit its stupid enum-based
    // wood variant system, because we need to be able to define our own variant. I know in my heart that mixins and the weird 
    // synthetic Object getVariant() thing that fabric makes visible would do part of this for me somehow, but I'm nowhere near knowledgeable
    // enough to use those yet. TODO: once you can access/assign getVariant() properly, refactor that instead and remove this bullshit.

}
