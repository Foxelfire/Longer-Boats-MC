package net.foxelfire.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider{

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerLog(ModBlocks.CEDAR_LOG).log(ModBlocks.CEDAR_LOG).wood(ModBlocks.CEDAR_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_CEDAR_LOG).log(ModBlocks.STRIPPED_CEDAR_LOG).wood(ModBlocks.STRIPPED_CEDAR_WOOD);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CEDAR_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CEDAR_LEAVES);
        blockStateModelGenerator.registerTintableCross(ModBlocks.CEDAR_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // generates the json files for literally every item. don't blame me, blame mojang. or the fabric API devs if you're a forge fan
        itemModelGenerator.register(ModItems.CEDAR_BOAT_ITEM, Models.GENERATED);
    }

}
