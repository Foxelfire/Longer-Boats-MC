package net.foxelfire.longer_boats;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.foxelfire.longer_boats.item.ModItems;

public class FuelItems {
    public static void registerFuelInstances(){
        LongerBoatsMod.LOGGER.info("Registering Fuel Entries for the Mod Items of: " + LongerBoatsMod.MOD_ID);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_OAK_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_SPRUCE_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_BIRCH_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_DARK_OAK_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_ACACIA_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_JUNGLE_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_CHERRY_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_MANGROVE_ITEM, 800);
        FuelRegistry.INSTANCE.add(ModItems.LONG_BOAT_BAMBOO_ITEM, 800);
    }
}
