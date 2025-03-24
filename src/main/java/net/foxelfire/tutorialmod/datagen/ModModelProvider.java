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
        // generates the json files for literally every item. don't blame me, blame mojang. or the fabric API devs if you're a forge fan
        itemModelGenerator.register(ModItems.LONG_BOAT_ITEM, Models.GENERATED);
    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // No blocks!
    }

}
