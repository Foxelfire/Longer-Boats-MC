package net.foxelfire.longer_boats.entity.custom;

import net.foxelfire.longer_boats.screen.LongBoatScreenHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class LongBoatEntity extends AbstractLongBoatEntity{

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(LongBoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public LongBoatEntity(EntityType<? extends AbstractLongBoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder){
        super.initDataTracker(builder);
        builder.add(VARIANT, 0);
    }

    @Override
    public void setVariant(LongBoatVariant variant) {
        int id = variant.ordinal();
        this.dataTracker.set(VARIANT, id);
    }

    public void setVariant(int id){
        this.dataTracker.set(VARIANT, id);
    }

    @Override
    public LongBoatVariant getVariant() {
        return LongBoatVariant.values()[this.dataTracker.get(VARIANT)];
    }

    @Override
    // We're always on the server here.
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        int ordinalOfVariant = nbt.getInt("Variant");
        this.setVariant(ordinalOfVariant);
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Variant", this.dataTracker.get(VARIANT));
        super.writeCustomDataToNbt(nbt);
    }
}
