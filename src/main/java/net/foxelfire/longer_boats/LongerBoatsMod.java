package net.foxelfire.longer_boats;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.foxelfire.longer_boats.entity.custom.AbstractLongBoatEntity;
import net.foxelfire.longer_boats.item.ModItems;
import net.foxelfire.longer_boats.screen.LongBoatScreenHandler;
import net.foxelfire.longer_boats.screen.ModScreenHandlers;
import net.foxelfire.longer_boats.util.InventorySyncC2SPayload;
import net.foxelfire.longer_boats.util.InventorySyncS2CPayload;
import net.foxelfire.longer_boats.util.MovementInputS2CPayload;
import net.minecraft.item.ItemStack;

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
        PayloadTypeRegistry.playC2S().register(InventorySyncC2SPayload.ID, InventorySyncC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(InventorySyncS2CPayload.ID, InventorySyncS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MovementInputS2CPayload.ID, MovementInputS2CPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(InventorySyncC2SPayload.ID, (payload, context) -> {
            ArrayList<ItemStack> inventory = payload.inventory();
            int entityId = payload.entityId();
            int prevTab = payload.prevTab();
            int tab = payload.tab();
            context.server().execute(() -> {
				AbstractLongBoatEntity entity = (AbstractLongBoatEntity)context.player().getWorld().getEntityById(entityId);
                if(entity != null) {
                    for (int i = 0; i < inventory.size(); i++) {
                        entity.getInventory().set(i + prevTab * 27, inventory.get(i));
                    }
                    ServerPlayNetworking.send(context.player(), new InventorySyncS2CPayload(inventory, true, entityId, tab));
                    LongBoatScreenHandler.manageActiveEntityInventory(tab);
                }
			});
		});
	}
}