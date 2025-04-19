package net.foxelfire.longer_boats.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static ScreenHandlerType<LongBoatScreenHandler> LONG_BOAT_SCREEN_HANDLER =
    Registry.register(Registries.SCREEN_HANDLER, new Identifier(LongerBoatsMod.MOD_ID, "long_boat_screen"),
    new ExtendedScreenHandlerType<LongBoatScreenHandler>(LongBoatScreenHandler::new));

    public static void registerScreenHandlers(){
        LongerBoatsMod.LOGGER.info("Registering Screen Handlers for: " + LongerBoatsMod.MOD_ID);
    }

}
