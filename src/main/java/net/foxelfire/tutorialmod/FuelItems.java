package net.foxelfire.tutorialmod;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.foxelfire.tutorialmod.item.ModItems;

public class FuelItems {
    public static void registerFuelInstances(){
        TutorialMod.LOGGER.info("Registering Fuel Entries for the Mod Items of: " + TutorialMod.MOD_ID);
        FuelRegistry.INSTANCE.add(ModItems.FIRE_STALK, 1600);
    }
}
