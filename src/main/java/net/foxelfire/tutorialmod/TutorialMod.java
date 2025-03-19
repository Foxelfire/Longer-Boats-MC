package net.foxelfire.tutorialmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.screen.CedarBoatScreenHandler;
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
		ModBlocks.registerModBlocks();
		FuelItems.registerFuelInstances();
		ModSounds.registerSounds();
		ModScreenHandlers.registerScreenHandlers();

		StrippableBlockRegistry.register(ModBlocks.CEDAR_WOOD, ModBlocks.STRIPPED_CEDAR_WOOD);
		StrippableBlockRegistry.register(ModBlocks.CEDAR_LOG, ModBlocks.STRIPPED_CEDAR_LOG);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_LEAVES, new FlammableBlockRegistry.Entry(30, 60));
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_PLANKS, new FlammableBlockRegistry.Entry(5, 20));
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_LOG, new FlammableBlockRegistry.Entry(5, 5));
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_WOOD, new FlammableBlockRegistry.Entry(5, 5));
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_CEDAR_LOG, new FlammableBlockRegistry.Entry(5, 5));
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_CEDAR_WOOD, new FlammableBlockRegistry.Entry(5, 5));
		ServerPlayNetworking.registerGlobalReceiver(ModNetworkingConstants.INVENTORY_C2S_SYNCING_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				byte invSize = buf.readByte();
				DefaultedList<ItemStack> invContents = DefaultedList.of();
				for(int i = 0; i < invSize; i++){
					invContents.add(buf.readItemStack());
				}
				int entityID = buf.readInt();
				CedarBoatEntity entity = (CedarBoatEntity)player.getWorld().getEntityById(entityID);
				int tabOffset = buf.readInt();
                for(int i = 0; i < invSize; i++){
					entity.getInventory().set(i + tabOffset*27, invContents.get(i));
				}
				int nextTab = buf.readInt();
				entity.sendS2CInventoryPacket(entity.getInventory(), true, nextTab);
				CedarBoatScreenHandler.manageActiveEntityInventory(nextTab);
			});
		});
	}
}