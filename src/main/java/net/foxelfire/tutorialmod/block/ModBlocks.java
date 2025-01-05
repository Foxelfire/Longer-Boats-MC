package net.foxelfire.tutorialmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PillarBlock;

public class ModBlocks {

    public static final Block CEDAR_LOG = registerBlock("cedar_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_LOG).strength(4.5f)));
    public static final Block CEDAR_WOOD = registerBlock("cedar_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_WOOD).strength(4.5f)));
    public static final Block STRIPPED_CEDAR_LOG = registerBlock("stripped_cedar_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_LOG).strength(4.5f)));
    public static final Block STRIPPED_CEDAR_WOOD = registerBlock("stripped_cedar_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_WOOD).strength(4.5f)));
    public static final Block CEDAR_PLANKS = registerBlock("cedar_planks", new Block(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).strength(4.5f)));
    public static final Block CEDAR_LEAVES = registerBlock("cedar_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_LEAVES).nonOpaque()));
    public static final Block CEDAR_SAPLING = registerBlock("cedar_sapling", new Block(FabricBlockSettings.copyOf(Blocks.SPRUCE_SAPLING)));

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
