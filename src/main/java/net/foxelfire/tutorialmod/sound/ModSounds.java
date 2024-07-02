package net.foxelfire.tutorialmod.sound;

import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent BAR_BRAWL = registerSoundEvent("bar_brawl");

    public static final SoundEvent METAL_DETECTOR_FOUND_ORE = registerSoundEvent("metal_detector_found_ore");

    public static final SoundEvent LIGHT_LANTERN_BREAK = registerSoundEvent("light_lantern_break");
    public static final SoundEvent LIGHT_LANTERN_PLACE = registerSoundEvent("light_lantern_place");
    public static final SoundEvent LIGHT_LANTERN_STEP = registerSoundEvent("light_lantern_step");
    public static final SoundEvent LIGHT_LANTERN_HIT = registerSoundEvent("light_lantern_hit");
    public static final SoundEvent LIGHT_LANTERN_FALL = registerSoundEvent("light_lantern_fall");

    public static final BlockSoundGroup LIGHT_LANTERN_SOUNDS = new BlockSoundGroup(1f, 1f, LIGHT_LANTERN_BREAK, LIGHT_LANTERN_STEP, LIGHT_LANTERN_PLACE, LIGHT_LANTERN_HIT, LIGHT_LANTERN_FALL);
    

    private static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(TutorialMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));

    }

    public static void registerSounds(){
        TutorialMod.LOGGER.info("Registering sounds for " + TutorialMod.MOD_ID);
    }
}
