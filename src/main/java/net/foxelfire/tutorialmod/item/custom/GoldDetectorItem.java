package net.foxelfire.tutorialmod.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class GoldDetectorItem extends OreDetectorItem{
    public GoldDetectorItem(Settings settings){
        super(settings);
    }
    @Override
    protected boolean isValuableBlock(BlockState state) {
        return state.isOf(Blocks.GOLD_ORE);
    }

}