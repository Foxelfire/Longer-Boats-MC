package net.foxelfire.tutorialmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.item.custom.*;
import net.foxelfire.tutorialmod.sound.ModSounds;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item PYRITE = registerItem("pyrite", new Item(new FabricItemSettings())); // Registers the item (could you tell?)
    public static final Item LIGHT_SHARD = registerItem("light_shard", new Item(new FabricItemSettings()));
    public static final Item PYRITE_INGOT = registerItem("pyrite_ingot", new Item(new FabricItemSettings()));
    // why are pyrite tools' attack speeds negative??? well,  each tool has an invisible default of 4 attack speed and then whatever you put into the constructor is applied to that base of 4
    public static final Item PYRITE_AXE = registerItem("pyrite_axe", new AxeItem(ModToolMaterial.PYRITE, 5.0f, -1.2f, new FabricItemSettings()));
    public static final Item PYRITE_PICKAXE = registerItem("pyrite_pickaxe", new PickaxeItem(ModToolMaterial.PYRITE, 1, -1.6f, new FabricItemSettings()));
    public static final Item PYRITE_SHOVEL = registerItem("pyrite_shovel", new ShovelItem(ModToolMaterial.PYRITE, 1.5f, -2.0f, new FabricItemSettings()));
    public static final Item PYRITE_SWORD = registerItem("pyrite_sword", new SwordItem(ModToolMaterial.PYRITE, 2, -0.2f, new FabricItemSettings()));
    public static final Item PYRITE_HOE = registerItem("pyrite_hoe", new HoeItem(ModToolMaterial.PYRITE, 0, 0.0f, new FabricItemSettings()));

    public static final Item PYRITE_HELMET = registerItem("pyrite_helmet", new ModArmorItem(ModArmorMaterials.PYRITE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item PYRITE_CHESTPLATE = registerItem("pyrite_chestplate", new ModArmorItem(ModArmorMaterials.PYRITE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item PYRITE_LEGGINGS = registerItem("pyrite_leggings", new ModArmorItem(ModArmorMaterials.PYRITE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item PYRITE_BOOTS = registerItem("pyrite_boots", new ModArmorItem(ModArmorMaterials.PYRITE, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item COAL_DETECTOR = registerItem("coal_detector", new CoalDetectorItem(new FabricItemSettings().maxDamage(128)));
    public static final Item COPPER_DETECTOR = registerItem("copper_detector", new CopperDetectorItem(new FabricItemSettings().maxDamage(128)));
    public static final Item IRON_DETECTOR = registerItem("iron_detector", new IronDetectorItem(new FabricItemSettings().maxDamage(64)));
    public static final Item GOLD_DETECTOR = registerItem("gold_detector", new GoldDetectorItem(new FabricItemSettings().maxDamage(64)));
    public static final Item FIRE_STALK = registerItem("fire_stalk", new Item(new FabricItemSettings()));

    public static final Item KONPEITO = registerItem("konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.KONPEITO).maxCount(16)));
    public static final Item BLACK_KONPEITO = registerItem("black_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.BLACK_KONPEITO).maxCount(16)));
    public static final Item BLUE_KONPEITO = registerItem("blue_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.BLUE_KONPEITO).maxCount(16)));
    public static final Item BROWN_KONPEITO = registerItem("brown_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.BROWN_KONPEITO).maxCount(16)));
    public static final Item CYAN_KONPEITO = registerItem("cyan_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.CYAN_KONPEITO).maxCount(16)));
    public static final Item GRAY_KONPEITO = registerItem("gray_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.GRAY_KONPEITO).maxCount(16)));
    public static final Item GREEN_KONPEITO = registerItem("green_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.GREEN_KONPEITO).maxCount(16)));
    public static final Item LIGHT_BLUE_KONPEITO = registerItem("light_blue_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.LIGHT_BLUE_KONPEITO).maxCount(16)));
    public static final Item LIGHT_GRAY_KONPEITO = registerItem("light_gray_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.LIGHT_GRAY_KONPEITO).maxCount(16)));
    public static final Item LIME_KONPEITO = registerItem("lime_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.LIME_KONPEITO).maxCount(16)));
    public static final Item MAGENTA_KONPEITO = registerItem("magenta_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.MAGENTA_KONPEITO).maxCount(16)));
    public static final Item ORANGE_KONPEITO = registerItem("orange_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.ORANGE_KONPEITO).maxCount(16)));
    public static final Item PINK_KONPEITO = registerItem("pink_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.PINK_KONPEITO).maxCount(16)));
    public static final Item PURPLE_KONPEITO = registerItem("purple_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.PURPLE_KONPEITO).maxCount(16)));
    public static final Item RED_KONPEITO = registerItem("red_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.RED_KONPEITO).maxCount(16)));
    public static final Item YELLOW_KONPEITO = registerItem("yellow_konpeito", new Item(new FabricItemSettings().food(ModFoodComponents.YELLOW_KONPEITO).maxCount(16)));

    public static final Item LIGHT_DUST = registerItem("light_dust", new Item(new FabricItemSettings()));
    public static final Item WIND_DUST = registerItem("wind_dust", new Item(new FabricItemSettings()));
    public static final Item FIRE_DUST = registerItem("fire_dust", new Item(new FabricItemSettings()));
    public static final Item EARTH_DUST = registerItem("earth_dust", new Item(new FabricItemSettings()));
    public static final Item WATER_DUST = registerItem("water_dust", new Item(new FabricItemSettings()));
    public static final Item PLANT_DUST = registerItem("plant_dust", new Item(new FabricItemSettings()));
    public static final Item ICE_DUST = registerItem("ice_dust", new Item(new FabricItemSettings()));
    public static final Item ELECTRIC_DUST = registerItem("electric_dust", new Item(new FabricItemSettings()));
    public static final Item SOUND_DUST = registerItem("sound_dust", new Item(new FabricItemSettings()));
    public static final Item ARCANE_DUST = registerItem("arcane_dust", new Item(new FabricItemSettings()));

    public static final Item LUMINOUS_WAND = registerItem("light_wand", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item BAR_BRAWL_MUSIC_DISC = registerItem("bar_brawl_music_disc", new MusicDiscItem(7, ModSounds.BAR_BRAWL, new FabricItemSettings().maxCount(1), 122));
    public static final Item PORCUPINE_SPAWN_EGG = registerItem("porcupine_spawn_egg", new SpawnEggItem(ModEntities.PORCUPINE, 0xa86518, 0x3b260f, new FabricItemSettings()));
    public static final Item PORCUPINE_QUILL = registerItem("porcupine_quill", new Item(new FabricItemSettings()));

    public static final Item DEWFRUIT_SEEDS = registerItem("dewfruit_seeds", new AliasedBlockItem(ModBlocks.DEWFRUIT_CROP, new FabricItemSettings()));
    public static final Item DEWFRUIT = registerItem("dewfruit", new Item(new FabricItemSettings().food(ModFoodComponents.DEWFRUIT)));

    private static void itemGroupToAddToIngredientTab(FabricItemGroupEntries entries){ // Put all registered entries to add to the game in here!
        entries.add(PYRITE);
        entries.add(LIGHT_SHARD);
        entries.add(LIGHT_DUST);
        entries.add(WIND_DUST);
        entries.add(FIRE_DUST);
        entries.add(EARTH_DUST);
        entries.add(WATER_DUST);
        entries.add(PLANT_DUST);
        entries.add(ICE_DUST);
        entries.add(ELECTRIC_DUST);
        entries.add(SOUND_DUST);
        entries.add(ARCANE_DUST);
        entries.add(PYRITE_INGOT);
        entries.add(PORCUPINE_QUILL);
    }
    private static void itemGroupToAddToNaturalTab(FabricItemGroupEntries entries){
        entries.add(ModBlocks.LIGHT_CRYSTAL_BLOCK);
        entries.add(FIRE_STALK);
        entries.add(DEWFRUIT_SEEDS);
        entries.add(ModBlocks.CLOVER_FLOWER);
        entries.add(ModBlocks.CLOVER);
    }
    private static void itemGroupToAddToFoodTab(FabricItemGroupEntries entries){
        entries.add(KONPEITO);
        entries.add(BLACK_KONPEITO);
        entries.add(BLUE_KONPEITO);
        entries.add(BROWN_KONPEITO);
        entries.add(CYAN_KONPEITO);
        entries.add(GREEN_KONPEITO);
        entries.add(GRAY_KONPEITO);
        entries.add(LIGHT_BLUE_KONPEITO);
        entries.add(LIGHT_GRAY_KONPEITO);
        entries.add(LIME_KONPEITO);
        entries.add(MAGENTA_KONPEITO);
        entries.add(ORANGE_KONPEITO);
        entries.add(PINK_KONPEITO);
        entries.add(PURPLE_KONPEITO);
        entries.add(RED_KONPEITO);
        entries.add(YELLOW_KONPEITO);
        entries.add(DEWFRUIT);
    }
    private static void itemGroupToAddToBuildingTab(FabricItemGroupEntries entries){
        entries.add(ModBlocks.LIGHT_LANTERN_BLOCK);
        entries.add(ModBlocks.LIGHT_CRYSTAL_BLOCK);
        entries.add(ModBlocks.PYRITE_BLOCK);
        entries.add(ModBlocks.PYRITE_BUTTON);
        entries.add(ModBlocks.PYRITE_PRESSURE_PLATE);
        entries.add(ModBlocks.PYRITE_SLAB);
        entries.add(ModBlocks.PYRITE_STAIRS);
        entries.add(ModBlocks.PYRITE_DOOR);
        entries.add(ModBlocks.PYRITE_TRAPDOOR);
        entries.add(ModBlocks.PYRITE_WALL);
    }
    private static void itemGroupToAddToToolsTab(FabricItemGroupEntries entries){
        entries.add(COAL_DETECTOR);
        entries.add(COPPER_DETECTOR);
        entries.add(IRON_DETECTOR);
        entries.add(GOLD_DETECTOR);
        entries.add(LUMINOUS_WAND);
        entries.add(PYRITE_SWORD);
        entries.add(PYRITE_PICKAXE);
        entries.add(PYRITE_AXE);
        entries.add(PYRITE_SHOVEL);
        entries.add(PYRITE_HOE);
        entries.add(BAR_BRAWL_MUSIC_DISC);
    }
    private static void itemGroupToAddToCombatTab(FabricItemGroupEntries entries){
        entries.add(PYRITE_SWORD);
        entries.add(PYRITE_AXE);
        entries.add(PYRITE_BOOTS);
        entries.add(PYRITE_CHESTPLATE);
        entries.add(PYRITE_HELMET);
        entries.add(PYRITE_LEGGINGS);
    }
    private static void itemGroupToAddToSpawnEggsTab(FabricItemGroupEntries entries){
        entries.add(PORCUPINE_SPAWN_EGG);
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(TutorialMod.MOD_ID, name), item);
    }
    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registering Mod Items for: " + TutorialMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::itemGroupToAddToIngredientTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModItems::itemGroupToAddToNaturalTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(ModItems::itemGroupToAddToBuildingTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::itemGroupToAddToToolsTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(ModItems::itemGroupToAddToFoodTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::itemGroupToAddToCombatTab);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::itemGroupToAddToSpawnEggsTab);
    }
}
