package net.foxelfire.tutorialmod;

import net.fabricmc.api.ModInitializer;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.block.entity.ModBlockEntities;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.screen.ModScreenHandlers;
import net.foxelfire.tutorialmod.sound.ModSounds;
import net.foxelfire.tutorialmod.util.ModCustomTrades;
import net.foxelfire.tutorialmod.util.ModLootTableModifiers;
import net.foxelfire.tutorialmod.villager.ModVillagers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialMod implements ModInitializer {
	public static final String MOD_ID = "tutorialmod";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		FuelItems.registerFuelInstances();
		ModLootTableModifiers.modifyLootTables();
		ModCustomTrades.registerCustomTrades();
		ModVillagers.registerVillagers();
		ModSounds.registerSounds();
		ModEntities.registerModEntities();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
	}
}