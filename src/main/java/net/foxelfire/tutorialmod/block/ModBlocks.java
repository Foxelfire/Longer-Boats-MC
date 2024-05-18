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
import net.minecraft.block.MapColor;

public class ModBlocks {
    public static final Block LIGHT_CRYSTAL_BLOCK = registerBlock("light_crystal_block", new Block(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(MapColor.TERRACOTTA_WHITE).luminance(state -> 7)));
    // if you need an ore or other item or xp-dropping block, go to 8:45 in https://www.youtube.com/watch?v=6plE7wdRCwc&list=PLKGarocXCE1EO43Dlf5JGh7Yk-kRAXUEJ&index=5
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
