package net.foxelfire.tutorialmod;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.foxelfire.tutorialmod.item.ModItems;

public class FuelItems {
    public static void registerFuelInstances(){ // TODO make it so you can burn the boat
        TutorialMod.LOGGER.info("Registering Fuel Entries for the Mod Items of: " + TutorialMod.MOD_ID);
        FuelRegistry.INSTANCE.add(ModItems.CEDAR_BOAT_ITEM, 800);
    }
}
