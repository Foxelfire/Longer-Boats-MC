package net.foxelfire.longer_boats;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.foxelfire.longer_boats.entity.custom.AbstractLongBoatEntity;
import net.foxelfire.longer_boats.item.ModItems;
import net.foxelfire.longer_boats.screen.LongBoatScreenHandler;
import net.foxelfire.longer_boats.screen.ModScreenHandlers;
import net.foxelfire.longer_boats.util.ChangeTabC2SPayload;
import net.foxelfire.longer_boats.util.InventorySizeS2CPayload;
import net.foxelfire.longer_boats.util.MovementInputS2CPayload;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        PayloadTypeRegistry.playC2S().register(ChangeTabC2SPayload.ID, ChangeTabC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MovementInputS2CPayload.ID, MovementInputS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(InventorySizeS2CPayload.ID, InventorySizeS2CPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ChangeTabC2SPayload.ID, (payload, context) -> {
                    PlayerEntity player = context.player();
                    context.server().execute(() -> {
                        Entity entity = player.getWorld().getEntityById(payload.entityId());
                        if (!(entity instanceof AbstractLongBoatEntity boat)) {
                            return;
                        }
                        if (player.currentScreenHandler instanceof LongBoatScreenHandler handler && handler.entity == boat) {
                            handler.setCurrentTab(payload.tab());
                            handler.sendContentUpdates();
                        }
                    });
                }
        );
	}
}