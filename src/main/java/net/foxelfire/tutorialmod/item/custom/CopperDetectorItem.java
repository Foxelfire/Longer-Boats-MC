package net.foxelfire.tutorialmod.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class CopperDetectorItem extends OreDetectorItem{
    public CopperDetectorItem(Settings settings){
        super(settings);
    }

    @Override
    protected boolean isValuableBlock(BlockState state) {
        return state.isOf(Blocks.COPPER_ORE);
    }

}
