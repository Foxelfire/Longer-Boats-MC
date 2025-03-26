package net.foxelfire.tutorialmod.entity.custom;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public enum LongBoatVariant {
    
    OAK (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_oak.png"), false, ModItems.LONG_BOAT_OAK_ITEM),
    BIRCH (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_birch.png"), false, ModItems.LONG_BOAT_BIRCH_ITEM),
    CHERRY (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_cherry.png"), false, ModItems.LONG_BOAT_BIRCH_ITEM),
    SPRUCE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_spruce.png"), false, ModItems.LONG_BOAT_SPRUCE_ITEM),
    DARK_OAK (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_dark_oak.png"), false, ModItems.LONG_BOAT_DARK_OAK_ITEM),
    ACACIA (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_acacia.png"), false, ModItems.LONG_BOAT_ACACIA_ITEM),
    JUNGLE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_jungle.png"), false, ModItems.LONG_BOAT_JUNGLE_ITEM),
    MANGROVE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_mangrove.png"), false, ModItems.LONG_BOAT_MANGROVE_ITEM),
    BAMBOO (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_bamboo.png"), true, ModItems.LONG_BOAT_BAMBOO_ITEM);
    
    private final Identifier TEXTURE;
    private final boolean ISRAFT;
    private final Item ITEM;

    LongBoatVariant(Identifier identifier, boolean isRaft, Item item) {
        this.TEXTURE = identifier;
        this.ISRAFT = isRaft;
        this.ITEM = item;
    }

    public Identifier getTexture(){
        return this.TEXTURE;
    }
}