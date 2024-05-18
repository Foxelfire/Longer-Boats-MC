package net.foxelfire.tutorialmod.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public abstract class OreDetectorItem extends Item{
    
    public OreDetectorItem(Settings settings) {
        super(settings);
    }
    public ActionResult useOnBlock(ItemUsageContext context){
        if(!context.getWorld().isClient()){ // checking as the server, not the client/player
            BlockPos positionClicked = context.getBlockPos();
            PlayerEntity player = context.getPlayer();
            boolean foundBlock = false;
            for(int i = 0; i <= positionClicked.getY() + 64; i++){ // i is the Y offset of the block we want to know from where we clicked. 
                BlockState state = context.getWorld().getBlockState(positionClicked.down(i));
                if(isValuableBlock(state)){
                    outputValuableCoordinates(positionClicked.down(i), player, state.getBlock());
                    foundBlock = true;
                    break;
                }
            }
            if(!foundBlock){
                player.sendMessage(Text.literal("No valuables found..."));
            }
        }
        context.getStack().damage(1, context.getPlayer(), playerEntity -> playerEntity.sendToolBreakStatus(playerEntity.getActiveHand()));

        return ActionResult.SUCCESS; // this is what plays the swinging animation when you right-click on a block with this item
    }

    private void outputValuableCoordinates(BlockPos blockPos, PlayerEntity player, Block block) {
        player.sendMessage(Text.literal("Found " + block.asItem().getName().getString() + " at " + "(" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + ")"), false);
    }

    protected abstract boolean isValuableBlock(BlockState state);

}
