package net.foxelfire.tutorialmod.entity.custom;

import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public enum LongBoatVariant {
    
    OAK (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_oak.png"), false),
    BIRCH (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_birch.png"), false),
    SPRUCE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_spruce.png"), false),
    DARK_OAK (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_dark_oak.png"), false),
    ACACIA (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_acacia.png"), false),
    JUNGLE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_jungle.png"), false),
    MANGROVE (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_mangrove.png"), false),
    BAMBOO (new Identifier(TutorialMod.MOD_ID, "textures/entity/long_boat_bamboo.png"), true);

    LongBoatVariant(Identifier identifier, boolean isRaft) {
        this.TEXTURE = identifier;
        this.ISRAFT = isRaft;
    }
    
        private final Identifier TEXTURE;
        private final boolean ISRAFT;
    /*private final Item ITEM;
    */
}