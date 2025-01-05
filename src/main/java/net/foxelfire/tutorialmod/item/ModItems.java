package net.foxelfire.tutorialmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.item.custom.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item CEDAR_BOAT_ITEM = registerItem("cedar_boat", new CedarBoatItem(ModEntities.CEDAR_BOAT, new FabricItemSettings()));

    private static void itemGroupToAddToNaturalTab(FabricItemGroupEntries entries){
        entries.add(ModBlocks.CEDAR_SAPLING);
        entries.add(ModBlocks.CEDAR_LEAVES);
        entries.add(ModBlocks.CEDAR_LOG);
        entries.add(ModBlocks.CEDAR_WOOD);
    }
    private static void itemGroupToAddToBuildingTab(FabricItemGroupEntries entries){
        entries.add(ModBlocks.CEDAR_PLANKS);
        entries.add(ModBlocks.CEDAR_LOG);
        entries.add(ModBlocks.CEDAR_WOOD);
        entries.add(ModBlocks.STRIPPED_CEDAR_LOG);
        entries.add(ModBlocks.STRIPPED_CEDAR_WOOD);
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(TutorialMod.MOD_ID, name), item);
    }
    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registering Mod Items for: " + TutorialMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModItems::itemGroupToAddToNaturalTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(ModItems::itemGroupToAddToBuildingTab);
    }
}
