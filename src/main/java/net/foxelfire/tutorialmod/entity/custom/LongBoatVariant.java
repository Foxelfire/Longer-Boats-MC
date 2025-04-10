package net.foxelfire.tutorialmod.entity.custom;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public enum LongBoatVariant {
    
    OAK (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_oak.png"), false),
    BIRCH (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_birch.png"), false),
    CHERRY (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_cherry.png"), false),
    SPRUCE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_spruce.png"), false),
    DARK_OAK (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_dark_oak.png"), false),
    ACACIA (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_acacia.png"), false),
    JUNGLE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_jungle.png"), false),
    MANGROVE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_mangrove.png"), false),
    BAMBOO (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_raft_bamboo.png"), true);
    
    private final Identifier TEXTURE;
    private final boolean ISRAFT;
    private Item ITEM;
    private static Item[] items;

    LongBoatVariant(Identifier identifier, boolean isRaft) {
        this.TEXTURE = identifier;
        this.ISRAFT = isRaft;
    }

    public Identifier getTexture(){
        return this.TEXTURE;
    }

    public Item getItem(){
        /* this is a crazy hack i had to do bc the items aren't registered yet when enums are created, so when the constructor is ran and fields are assigned, everything in ModItems is null.
        therefore, we can't just put these in the constructor or this.ITEM will be null. We have to reference ModItems only during gameplay. */
        Item[] items = {ModItems.LONG_BOAT_OAK_ITEM, ModItems.LONG_BOAT_BIRCH_ITEM, ModItems.LONG_BOAT_CHERRY_ITEM, ModItems.LONG_BOAT_SPRUCE_ITEM, ModItems.LONG_BOAT_DARK_OAK_ITEM,
        ModItems.LONG_BOAT_ACACIA_ITEM, ModItems.LONG_BOAT_JUNGLE_ITEM, ModItems.LONG_BOAT_MANGROVE_ITEM, ModItems.LONG_BOAT_BAMBOO_ITEM};
        this.ITEM = items[this.ordinal()];
        return this.ITEM;
    }
}