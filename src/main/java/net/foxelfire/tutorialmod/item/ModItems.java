package net.foxelfire.tutorialmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item PYRITE = registerItem("pyrite", new Item(new FabricItemSettings())); // Registers the item (could you tell?)
    public static final Item LIGHT_SHARD = registerItem("light_shard", new Item(new FabricItemSettings()));
    public static final Item PYRITE_INGOT = registerItem("pyrite_ingot", new Item(new FabricItemSettings()));
    
    
    private static void itemGroupToAddToIngredientTab(FabricItemGroupEntries entries){ // Put all registered entries to add to the game in here!
        entries.add(PYRITE);
        entries.add(LIGHT_SHARD);
        entries.add(PYRITE_INGOT);
    }
    private static void itemGroupToAddToNaturalTab(FabricItemGroupEntries entries){
        entries.add(ModBlocks.LIGHT_CRYSTAL_BLOCK);
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(TutorialMod.MOD_ID, name), item);
    }
    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registering Mod Items for: " + TutorialMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::itemGroupToAddToIngredientTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModItems::itemGroupToAddToNaturalTab);
    }
}
