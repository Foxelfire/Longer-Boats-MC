package net.foxelfire.tutorialmod.item.custom;

import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

public class CedarBoatItem extends Item{

    private EntityType<? extends CedarBoatEntity> type;
    public CedarBoatItem(EntityType<? extends CedarBoatEntity> type, Settings settings) {
        super(settings);
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context){
        if(!context.getWorld().isClient()){
            if(context.getWorld().getBlockState(context.getBlockPos().up(1)).isAir()){
                BlockPos pos = context.getBlockPos();
                type.spawnFromItemStack((ServerWorld)context.getWorld(), context.getStack(), context.getPlayer(), pos.up(1), 
                SpawnReason.SPAWN_EGG, false, false);
                context.getWorld().emitGameEvent((Entity)context.getPlayer(), GameEvent.ENTITY_PLACE, context.getBlockPos());
                if (!context.getPlayer().getAbilities().creativeMode) {
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }
    
}
