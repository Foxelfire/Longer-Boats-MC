package net.foxelfire.tutorialmod.villager;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class ModVillagers {

    public static final RegistryKey<PointOfInterestType> METAL_POI_KEY = poiKey("metalpoi");
    public static final PointOfInterestType METAL_POI = registerPoi("metalpoi", Blocks.ANVIL);
    public static final VillagerProfession MINER_PROFESSION = registerVillagerProfession("miner", METAL_POI_KEY);

    private static VillagerProfession registerVillagerProfession(String name, RegistryKey<PointOfInterestType> type){
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(TutorialMod.MOD_ID, name),
        new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type), 
        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON));
    } // well, this is fucking insane. look at vanilla VillagerProfession if curious ig? and bring good luck
      // PointOfInterestType(s) are more important to look at tho

    public static void registerVillagers(){
        TutorialMod.LOGGER.info("Registering Villagers for: " + TutorialMod.MOD_ID);
    }

    private static PointOfInterestType registerPoi(String name, Block block){
        return PointOfInterestHelper.register(new Identifier(TutorialMod.MOD_ID, name), 1, 1, block); 
        // ticketCount is how many villagers can get a profession from the point of interest (workstation), and searchDistance is how far away they can be to get it
    }

    private static RegistryKey<PointOfInterestType> poiKey(String name){
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(TutorialMod.MOD_ID, name));
    }

}
