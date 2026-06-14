package net.foxelfire.longer_boats;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.foxelfire.longer_boats.entity.custom.AbstractLongBoatEntity;
import net.foxelfire.longer_boats.item.ModItems;
import net.foxelfire.longer_boats.screen.LongBoatScreenHandler;
import net.foxelfire.longer_boats.screen.ModScreenHandlers;
import net.foxelfire.longer_boats.util.InventorySyncC2SPayload;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class LongerBoatsMod implements ModInitializer {
	public static final String MOD_ID = "longer_boats";
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
		FuelItems.registerFuelInstances();
		ModScreenHandlers.registerScreenHandlers();

		ServerPlayNetworking.registerGlobalReceiver(InventorySyncC2SPayload.ID, (payload, context) -> {
            ArrayList<ItemStack> inventory = payload.inventory();
            int entityId = payload.entityId();
            int prevTab = payload.prevTab();
            int tab = payload.tab();

			context.server().execute(() -> {

				AbstractLongBoatEntity entity = (AbstractLongBoatEntity)context.server().getWorld().getEntityById(entityId);
                for(int i = 0; i < inventory.size(); i++){
					entity.getInventory().set(i + prevTab*27, invContents.get(i));
				}
				LongBoatScreenHandler.manageActiveEntityInventory(nextTab);
			});
		});
	}
}