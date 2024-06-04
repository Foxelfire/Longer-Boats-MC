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
        // generates blockstates (apparently, don't trust me on this) and both the block model and item model json files for literally every block.
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LIGHT_CRYSTAL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LIGHT_LANTERN_BLOCK);
        
        BlockStateModelGenerator.BlockTexturePool pyritePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.PYRITE_BLOCK);
        pyritePool.button(ModBlocks.PYRITE_BUTTON);
        pyritePool.slab(ModBlocks.PYRITE_SLAB);
        pyritePool.pressurePlate(ModBlocks.PYRITE_PRESSURE_PLATE);
        pyritePool.wall(ModBlocks.PYRITE_WALL);
        pyritePool.stairs(ModBlocks.PYRITE_STAIRS);
        blockStateModelGenerator.registerDoor(ModBlocks.PYRITE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.PYRITE_TRAPDOOR);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // generates the json files for literally every item. don't blame me, blame mojang. or the fabric API devs if you're a forge fan
        itemModelGenerator.register(ModItems.LIGHT_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRITE_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.FIRE_STALK, Models.GENERATED);

        itemModelGenerator.register(ModItems.COPPER_DETECTOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.COAL_DETECTOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLD_DETECTOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.IRON_DETECTOR, Models.GENERATED);

        itemModelGenerator.register(ModItems.KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLACK_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLUE_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROWN_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.CYAN_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.GRAY_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.GREEN_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIGHT_BLUE_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIGHT_GRAY_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIME_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.MAGENTA_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.ORANGE_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.PINK_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.PURPLE_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.RED_KONPEITO, Models.GENERATED);
        itemModelGenerator.register(ModItems.YELLOW_KONPEITO, Models.GENERATED);
    }

}
