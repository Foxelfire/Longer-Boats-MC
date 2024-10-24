package net.foxelfire.tutorialmod.item.custom;

import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CedarBoatItem extends Item{

    private EntityType<? extends CedarBoatEntity> type;
    public CedarBoatItem(EntityType<? extends CedarBoatEntity> type, Settings settings) {
        super(settings);
        this.type = type;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = BoatItem.raycast(world, user, RaycastContext.FluidHandling.ANY);
        if(((HitResult)hitResult).getType() == HitResult.Type.BLOCK){
            if(!world.isClient()){
                BlockPos pos = hitResult.getBlockPos();
                type.spawnFromItemStack((ServerWorld)world, itemStack, user, pos.up(1), 
                SpawnReason.SPAWN_EGG, false, false).setYaw(user.getYaw() + 180.0f);
                world.emitGameEvent((Entity)user, GameEvent.ENTITY_PLACE, pos);
                if (!user.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.pass(itemStack);
    }
   
    
}
