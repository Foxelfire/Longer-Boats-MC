package net.foxelfire.tutorialmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item PYRITE = registerItem("pyrite", new Item(new FabricItemSettings())); // Registers the item (could you tell?)
    
    
    
    private static void itemGroupToAddToIngredientTab(FabricItemGroupEntries entries){ // Put all registered entries to add to the game in here!
        entries.add(PYRITE);
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(TutorialMod.MOD_ID, name), item);
    }
    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registering Mod Items for: " + TutorialMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::itemGroupToAddToIngredientTab); // ItemGroups is the class for all the item tabs in the creative menu in vanilla Minecraft. Each instance is one tab. We have added RUBY to the "Ingredients" tab in-game
    }
}
