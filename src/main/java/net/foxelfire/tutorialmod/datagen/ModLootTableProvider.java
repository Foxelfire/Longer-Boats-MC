package net.foxelfire.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;

public class ModLootTableProvider extends FabricBlockLootTableProvider{

    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.CEDAR_PLANKS);
        addDrop(ModBlocks.CEDAR_LOG);
        addDrop(ModBlocks.CEDAR_WOOD);
        addDrop(ModBlocks.STRIPPED_CEDAR_LOG);
        addDrop(ModBlocks.STRIPPED_CEDAR_WOOD);
    }
}
