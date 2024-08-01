package net.foxelfire.tutorialmod.block.custom;

import net.foxelfire.tutorialmod.block.entity.ElementExtractorBlockEntity;
import net.foxelfire.tutorialmod.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ElementExtractorBlock extends BlockWithEntity{

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 14, 16);

    public ElementExtractorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new ElementExtractorBlockEntity(arg0, arg1);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return SHAPE;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved){
        if(state.getBlock() != newState.getBlock()){ // this is pretty much the same for all block entities
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof ElementExtractorBlockEntity){
                ItemScatterer.spawn(world, pos, (ElementExtractorBlockEntity)blockEntity); // drop all items if destroyed, ItemScatterer makes them drop in random directions
                world.updateComparators(pos, this);
            }
            if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) { // copy-pasted from super() - it was deprecated so i figured this would be better than calling it
                world.removeBlockEntity(pos);
            }
        }
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      /* this returns a ticker - which is exampleBlock.tick().
      * that's what the crazy lambda thing is for - validateTicker(returnedType, expectedType, expectedType'sTickerMethod) 
      * is basically a null-safe way for mc to easily redirect to whatever ticker a given block/block entity has 
      * to know when to stop running it and do other stuff with it. I think.
      */  
      return validateTicker(type, ModBlockEntities.ELEMENT_EXTRACTOR_BLOCK_ENTITY,
      (worldAgain, pos, stateAgain, blockEntity) -> blockEntity.tick(worldAgain, stateAgain, pos));
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if(!world.isClient){ // server opening the gui screen for player
            NamedScreenHandlerFactory screenHandlerFactory = ((ElementExtractorBlockEntity)world.getBlockEntity(pos));
            if(screenHandlerFactory != null){ // was there not an ElementExtractorBlockEntity there? if so, stop it.
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
}
