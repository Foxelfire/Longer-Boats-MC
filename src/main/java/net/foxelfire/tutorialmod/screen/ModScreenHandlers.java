package net.foxelfire.tutorialmod.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static ScreenHandlerType<ElementExtractorScreenHandler> ELEMENT_EXTRACTOR_SCREEN_HANDLER =
    Registry.register(Registries.SCREEN_HANDLER, new Identifier(TutorialMod.MOD_ID, "element_extractor_screen"),
    new ExtendedScreenHandlerType<>(ElementExtractorScreenHandler::new));

    public static void registerScreenHandlers(){
        TutorialMod.LOGGER.info("Registering Screen Handlers for: " + TutorialMod.MOD_ID);
    }

}
