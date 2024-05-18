package net.foxelfire.tutorialmod.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class CoalDetectorItem extends OreDetectorItem{
    public CoalDetectorItem(Settings settings){
        super(settings);
    }

    @Override
    protected boolean isValuableBlock(BlockState state) {
        return state.isOf(Blocks.COAL_ORE);
    }

}
