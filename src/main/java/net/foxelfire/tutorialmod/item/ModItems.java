package net.foxelfire.tutorialmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.item.custom.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item LONG_BOAT_ITEM = registerItem("long_boat", new LongBoatItem(ModEntities.LONG_BOAT, new FabricItemSettings()));
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(TutorialMod.MOD_ID, name), item);
    }
    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registering Mod Items for: " + TutorialMod.MOD_ID);
    }
}
