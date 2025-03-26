package net.foxelfire.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider{

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.LONG_BOAT_ACACIA_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_BIRCH_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_CHERRY_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_DARK_OAK_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_JUNGLE_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_MANGROVE_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_OAK_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_SPRUCE_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LONG_BOAT_BAMBOO_ITEM, Models.GENERATED);
    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // No blocks!
    }

}
