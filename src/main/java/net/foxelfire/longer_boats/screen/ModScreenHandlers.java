package net.foxelfire.longer_boats.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.foxelfire.longer_boats.LongerBoatsMod;
import net.foxelfire.longer_boats.util.EntityIdPayload;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static ScreenHandlerType<LongBoatScreenHandler> LONG_BOAT_SCREEN_HANDLER =
    Registry.register(Registries.SCREEN_HANDLER, Identifier.of(LongerBoatsMod.MOD_ID, "long_boat_screen"),
    new ExtendedScreenHandlerType<>(LongBoatScreenHandler::new, EntityIdPayload.CODEC));

    public static void registerScreenHandlers(){
        LongerBoatsMod.LOGGER.info("Registering Screen Handlers for: " + LongerBoatsMod.MOD_ID);
    }

}
