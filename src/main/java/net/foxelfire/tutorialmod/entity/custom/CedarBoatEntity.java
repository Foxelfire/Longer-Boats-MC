package net.foxelfire.tutorialmod.entity.custom;

import org.joml.Vector3f;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class CedarBoatEntity extends BoatEntity{

    private Location location;
    @SuppressWarnings("unused")
    private double fallVelocity; // BoatEntity won't let me access its fall velocity field, so we're making our own!
    @SuppressWarnings("unused")
    private static final TrackedData<Integer> BOAT_TYPE = null;
    // patching out dependency on a stupid wood variant enum, more details in following comments

    public CedarBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Item asItem(){
        return ModItems.CEDAR_BOAT_ITEM;
    }

    @Override
    protected int getMaxPassengers() {
        return 4;
    }

    @Override
    public void setVariant(Type type) {
        // you get nothing!
    }

    @Override
    public Type getVariant() {
        // you STILL get nothing!
        TutorialMod.LOGGER.error("Error: Minecraft still wants its nonexistent wood variant. You have more to patch out :(");
        return null;
    }


    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // no wood variants or enums for you, silly stinky BoatEntity!
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // you hear me? NOTHING!
    }

    // STINKY CODE WARNING:
    // the following overrides are *only* to remove references in BoatEntity to getVariant(). We can't inherit its stupid enum-based
    // wood variant system, because we need to be able to define our own variant. I know in my heart that mixins and the weird 
    // synthetic Object getVariant() thing that fabric makes visible would do part of this for me somehow, but I'm nowhere near knowledgeable
    // enough to use those yet. TODO: once you can access/assign getVariant() properly, refactor that instead and remove this bullshit.

    @Override // tweak later for larger model size
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        float f = this.getPassengerHorizontalOffset();
        if (this.getPassengerList().size() > 1) {
            int i = this.getPassengerList().indexOf(passenger);
            f = i == 0 ? 0.2f : -0.6f;
            if (passenger instanceof AnimalEntity) {
                f += 0.2f;
            }
        }
        return new Vector3f(0.0f,  dimensions.height / 3.0f, f);
    }



    @SuppressWarnings("resource") // worlds are AutoCloseable, but obviously it's mc's job to close()!
    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        this.fallVelocity = this.getVelocity().y;
        if (this.hasVehicle()) {
            return;
        }
        if (onGround) {
            if (this.fallDistance > 3.0f) {
                if (this.location != Location.ON_LAND) {
                    this.onLanding();
                    return;
                }
                this.handleFallDamage(this.fallDistance, 1.0f, this.getDamageSources().fall());
                if (!this.getWorld().isClient && !this.isRemoved()) {
                    this.kill();
                    if (this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        int i;
                        for (i = 0; i < 3; ++i) {
                            this.dropItem(ModBlocks.CEDAR_PLANKS);
                        }
                        for (i = 0; i < 2; ++i) {
                            this.dropItem(Items.STICK);
                        }
                    }
                }
            }
            this.onLanding();
        } else if (!this.getWorld().getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < 0.0) {
            this.fallDistance -= (float)heightDifference;
        }
    }

}
