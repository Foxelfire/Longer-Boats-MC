package net.foxelfire.tutorialmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.item.custom.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item PYRITE = registerItem("pyrite", new Item(new FabricItemSettings())); // Registers the item (could you tell?)
    public static final Item LIGHT_SHARD = registerItem("light_shard", new Item(new FabricItemSettings()));
    public static final Item PYRITE_INGOT = registerItem("pyrite_ingot", new Item(new FabricItemSettings()));
    public static final Item COAL_DETECTOR = registerItem("coal_detector", new CoalDetectorItem(new FabricItemSettings().maxDamage(128)));
    public static final Item COPPER_DETECTOR = registerItem("copper_detector", new CopperDetectorItem(new FabricItemSettings().maxDamage(128)));
    public static final Item IRON_DETECTOR = registerItem("iron_detector", new IronDetectorItem(new FabricItemSettings().maxDamage(64)));
    public static final Item GOLD_DETECTOR = registerItem("gold_detector", new GoldDetectorItem(new FabricItemSettings().maxDamage(64)));
    
    private static void itemGroupToAddToIngredientTab(FabricItemGroupEntries entries){ // Put all registered entries to add to the game in here!
        entries.add(PYRITE);
        entries.add(LIGHT_SHARD);
        entries.add(PYRITE_INGOT);
    }
    private static void itemGroupToAddToNaturalTab(FabricItemGroupEntries entries){
        entries.add(ModBlocks.LIGHT_CRYSTAL_BLOCK);
    }
    private static void itemGroupToAddToBuildingTab(FabricItemGroupEntries entries){
        entries.add(ModBlocks.LIGHT_LANTERN_BLOCK);
    }
    private static void itemGroupToaddToToolsTab(FabricItemGroupEntries entries){
        entries.add(COAL_DETECTOR);
        entries.add(COPPER_DETECTOR);
        entries.add(IRON_DETECTOR);
        entries.add(GOLD_DETECTOR);
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(TutorialMod.MOD_ID, name), item);
    }
    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registering Mod Items for: " + TutorialMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::itemGroupToAddToIngredientTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModItems::itemGroupToAddToNaturalTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(ModItems::itemGroupToAddToBuildingTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::itemGroupToaddToToolsTab);
    }
}
