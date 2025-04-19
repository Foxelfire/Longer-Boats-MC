package net.foxelfire.longer_boats.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.foxelfire.longer_boats.LongerBoatsMod;
import net.foxelfire.longer_boats.entity.ModEntities;
import net.foxelfire.longer_boats.entity.custom.LongBoatVariant;
import net.foxelfire.longer_boats.item.custom.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item LONG_BOAT_OAK_ITEM = registerItem("long_boat_oak", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.OAK, new FabricItemSettings()));
    public static final Item LONG_BOAT_BIRCH_ITEM = registerItem("long_boat_birch", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.BIRCH, new FabricItemSettings()));
    public static final Item LONG_BOAT_CHERRY_ITEM = registerItem("long_boat_cherry", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.CHERRY, new FabricItemSettings()));
    public static final Item LONG_BOAT_DARK_OAK_ITEM = registerItem("long_boat_dark_oak", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.DARK_OAK, new FabricItemSettings()));
    public static final Item LONG_BOAT_JUNGLE_ITEM = registerItem("long_boat_jungle", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.JUNGLE, new FabricItemSettings()));
    public static final Item LONG_BOAT_MANGROVE_ITEM = registerItem("long_boat_mangrove", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.MANGROVE, new FabricItemSettings()));
    public static final Item LONG_BOAT_SPRUCE_ITEM = registerItem("long_boat_spruce", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.SPRUCE, new FabricItemSettings()));
    public static final Item LONG_BOAT_ACACIA_ITEM = registerItem("long_boat_acacia", new LongBoatItem(ModEntities.LONG_BOAT, LongBoatVariant.ACACIA, new FabricItemSettings()));
    public static final Item LONG_BOAT_BAMBOO_ITEM = registerItem("long_raft_bamboo", new LongRaftItem(ModEntities.LONG_RAFT, new FabricItemSettings()));


    private static void itemGroupToAddToToolsTab(FabricItemGroupEntries entries){
        entries.add(LONG_BOAT_OAK_ITEM);
        entries.add(LONG_BOAT_BIRCH_ITEM);
        entries.add(LONG_BOAT_SPRUCE_ITEM);
        entries.add(LONG_BOAT_DARK_OAK_ITEM);
        entries.add(LONG_BOAT_ACACIA_ITEM);
        entries.add(LONG_BOAT_JUNGLE_ITEM);
        entries.add(LONG_BOAT_CHERRY_ITEM);
        entries.add(LONG_BOAT_MANGROVE_ITEM);
        entries.add(LONG_BOAT_BAMBOO_ITEM);
    }

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(LongerBoatsMod.MOD_ID, name), item);
    }
    public static void registerModItems(){
        LongerBoatsMod.LOGGER.info("Registering Mod Items for: " + LongerBoatsMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::itemGroupToAddToToolsTab);
    }
}
