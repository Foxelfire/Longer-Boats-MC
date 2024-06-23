package net.foxelfire.tutorialmod.block.custom;

import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public class DewfruitCropBlock extends CropBlock{

    public static final int MAX_AGE = 15;
    public static final IntProperty AGE = Properties.AGE_15;

    /* this is to override the out-of-bounds indexing in getOutlineShape(), it thinks all CropBlocks have less than or equal to 8 stages
    the tutorial does not account for this, making this the first minecraft source-related error i fixed with no help :)
    the max y-values are weird bc our custom textures are weird */
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), 
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), 
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), 
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0), 
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0), 
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0), 
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0), 
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    public DewfruitCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected IntProperty getAgeProperty(){
        return AGE;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.DEWFRUIT_SEEDS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[this.getAge(state)];
    }

}
