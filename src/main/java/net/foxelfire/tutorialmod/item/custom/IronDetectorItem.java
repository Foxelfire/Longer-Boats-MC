package net.foxelfire.tutorialmod.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class IronDetectorItem extends OreDetectorItem{
    public IronDetectorItem(Settings settings){
        super(settings);
    }

    @Override
    protected boolean isValuableBlock(BlockState state) {
        return state.isOf(Blocks.IRON_ORE);
    }

}
