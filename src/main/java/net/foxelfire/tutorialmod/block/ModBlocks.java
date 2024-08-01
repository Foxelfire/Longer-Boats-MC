package net.foxelfire.tutorialmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.custom.DewfruitCropBlock;
import net.foxelfire.tutorialmod.block.custom.ElementExtractorBlock;
import net.foxelfire.tutorialmod.block.custom.SoundBlock;
import net.foxelfire.tutorialmod.sound.ModSounds;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.PressurePlateBlock.ActivationRule;
import net.minecraft.entity.effect.StatusEffects;

public class ModBlocks {
    public static final Block LIGHT_CRYSTAL_BLOCK = registerBlock("light_crystal_block", new Block(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(MapColor.TERRACOTTA_WHITE).luminance(state -> 7)));
    public static final Block LIGHT_LANTERN_BLOCK = registerBlock("light_lantern_block", new SoundBlock(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(MapColor.WHITE).luminance(state -> 14).sounds(ModSounds.LIGHT_LANTERN_SOUNDS), SoundEvents.BLOCK_BEACON_ACTIVATE));
    public static final Block PYRITE_BLOCK = registerBlock("pyrite_block", new Block(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK).mapColor(MapColor.YELLOW).velocityMultiplier(1.05f).strength(2.5f,6.0f)));
    public static final Block PYRITE_STAIRS = registerBlock("pyrite_stairs", new StairsBlock(ModBlocks.PYRITE_BLOCK.getDefaultState(),FabricBlockSettings.copyOf(ModBlocks.PYRITE_BLOCK)));
    public static final Block PYRITE_SLAB = registerBlock("pyrite_slab", new SlabBlock(FabricBlockSettings.copyOf(ModBlocks.PYRITE_BLOCK)));
    public static final Block PYRITE_BUTTON = registerBlock("pyrite_button", new ButtonBlock(FabricBlockSettings.copyOf(ModBlocks.PYRITE_BLOCK), BlockSetType.GOLD, 10, true));
    public static final Block PYRITE_PRESSURE_PLATE = registerBlock("pyrite_pressure_plate", new PressurePlateBlock(ActivationRule.EVERYTHING, FabricBlockSettings.copyOf(ModBlocks.PYRITE_BLOCK), BlockSetType.GOLD));
    public static final Block PYRITE_WALL = registerBlock("pyrite_wall", new WallBlock(FabricBlockSettings.copyOf(ModBlocks.PYRITE_BLOCK)));
    // if you need a fence or fence gate, go to 3:39 here: https://www.youtube.com/watch?v=TgDh216TelA&list=PLKGarocXCE1EO43Dlf5JGh7Yk-kRAXUEJ&index=13... also fences and fence gates need tags in datagen, BlockTags.FENCES and FENCE_GATES respectively
    // and if you need an ore or other item or xp-dropping block, go to 8:45 in https://www.youtube.com/watch?v=6plE7wdRCwc&list=PLKGarocXCE1EO43Dlf5JGh7Yk-kRAXUEJ&index=5

    public static final Block PYRITE_DOOR = registerBlock("pyrite_door", new DoorBlock(FabricBlockSettings.copyOf(ModBlocks.PYRITE_BLOCK).nonOpaque(), BlockSetType.GOLD));
    public static final Block PYRITE_TRAPDOOR = registerBlock("pyrite_trapdoor", new TrapdoorBlock(FabricBlockSettings.copyOf(ModBlocks.PYRITE_BLOCK).nonOpaque(), BlockSetType.GOLD));

    public static final Block DEWFRUIT_CROP = Registry.register(Registries.BLOCK, new Identifier(TutorialMod.MOD_ID, "dewfruit_crop"), new DewfruitCropBlock(FabricBlockSettings.copyOf(Blocks.BEETROOTS)));
    public static final Block CLOVER_FLOWER = registerBlock("clover_flower", new FlowerBlock(StatusEffects.LUCK, 25, FabricBlockSettings.copyOf(Blocks.ALLIUM).nonOpaque().noCollision()));
    public static final Block CLOVER = registerBlock("clover", new PlantBlock(FabricBlockSettings.copyOf(Blocks.FERN).nonOpaque().noCollision()));
    public static final Block POTTED_CLOVER = Registry.register(Registries.BLOCK, new Identifier(TutorialMod.MOD_ID, "potted_clover_flower"), new FlowerPotBlock(CLOVER_FLOWER, FabricBlockSettings.copyOf(Blocks.POTTED_ALLIUM).nonOpaque()));

    public static final Block ELEMENT_EXTRACTOR_BLOCK = registerBlock("element_extractor", new ElementExtractorBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK).nonOpaque().mapColor(MapColor.DEEPSLATE_GRAY).luminance(state -> 4)));

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(TutorialMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(TutorialMod.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }


    public static void registerModBlocks(){
        TutorialMod.LOGGER.info("Registering Mod Blocks for: " + TutorialMod.MOD_ID);
    }
}
