package net.foxelfire.tutorialmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.foxelfire.tutorialmod.entity.custom.AbstractLongBoatEntity;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.screen.LongBoatScreenHandler;
import net.foxelfire.tutorialmod.screen.ModScreenHandlers;
import net.foxelfire.tutorialmod.sound.ModSounds;
import net.foxelfire.tutorialmod.util.ModNetworkingConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

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
		FuelItems.registerFuelInstances();
		ModSounds.registerSounds();
		ModScreenHandlers.registerScreenHandlers();

		ServerPlayNetworking.registerGlobalReceiver(ModNetworkingConstants.INVENTORY_C2S_SYNCING_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				byte invSize = buf.readByte();
				DefaultedList<ItemStack> invContents = DefaultedList.of();
				for(int i = 0; i < invSize; i++){
					invContents.add(buf.readItemStack());
				}
				int entityID = buf.readInt();
				AbstractLongBoatEntity entity = (AbstractLongBoatEntity)player.getWorld().getEntityById(entityID);
				int tabOffset = buf.readInt();
                for(int i = 0; i < invSize; i++){
					entity.getInventory().set(i + tabOffset*27, invContents.get(i));
				}
				int nextTab = buf.readInt();
				entity.sendS2CInventoryPacket(entity.getInventory(), true, nextTab);
				LongBoatScreenHandler.manageActiveEntityInventory(nextTab);
			});
		});
	}
}