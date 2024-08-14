package net.foxelfire.tutorialmod.datagen;

import java.util.Optional;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.block.custom.DewfruitCropBlock;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider{

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // generates blockstates (apparently, don't trust me on this) and both the block model and item model json files for literally every block.
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LIGHT_CRYSTAL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LIGHT_LANTERN_BLOCK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ELEMENT_EXTRACTOR_BLOCK);
        
        BlockStateModelGenerator.BlockTexturePool pyritePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.PYRITE_BLOCK);
        pyritePool.button(ModBlocks.PYRITE_BUTTON);
        pyritePool.slab(ModBlocks.PYRITE_SLAB);
        pyritePool.pressurePlate(ModBlocks.PYRITE_PRESSURE_PLATE);
        pyritePool.wall(ModBlocks.PYRITE_WALL);
        pyritePool.stairs(ModBlocks.PYRITE_STAIRS);
        blockStateModelGenerator.registerDoor(ModBlocks.PYRITE_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.PYRITE_TRAPDOOR);
        blockStateModelGenerator.registerCrop(ModBlocks.DEWFRUIT_CROP, DewfruitCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.CLOVER_FLOWER, ModBlocks.POTTED_CLOVER, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerTintableCross(ModBlocks.CLOVER, BlockStateModelGenerator.TintType.NOT_TINTED);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // generates the json files for literally every item. don't blame me, blame mojang. or the fabric API devs if you're a forge fan
        itemModelGenerator.register(ModItems.LIGHT_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRITE_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.FIRE_STALK, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEWFRUIT, Models.GENERATED);

        itemModelGenerator.register(ModItems.LIGHT_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.WIND_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.FIRE_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.EARTH_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.PLANT_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.ICE_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.ELECTRIC_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.SOUND_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARCANE_DUST, Models.GENERATED);


        itemModelGenerator.register(ModItems.PYRITE_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYRITE_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYRITE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYRITE_HOE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYRITE_SHOVEL, Models.HANDHELD);

        itemModelGenerator.registerArmor((ArmorItem)ModItems.PYRITE_HELMET); // wacky casting - I don't think we need its name so its fine
        itemModelGenerator.registerArmor((ArmorItem)ModItems.PYRITE_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.PYRITE_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem)ModItems.PYRITE_BOOTS);

        itemModelGenerator.register(ModItems.BAR_BRAWL_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.PORCUPINE_SPAWN_EGG, new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty())); // spawn egg textures are auto-generated
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
        itemModelGenerator.register(ModItems.PORCUPINE_QUILL, Models.GENERATED);
    }

}
