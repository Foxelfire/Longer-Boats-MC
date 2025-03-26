package net.foxelfire.tutorialmod.entity.client;

import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
        public static final EntityModelLayer LONG_BOAT =
                new EntityModelLayer(new Identifier(TutorialMod.MOD_ID, "long_boat"), "main");
        public static final EntityModelLayer LONG_RAFT =
                new EntityModelLayer(new Identifier(TutorialMod.MOD_ID, "long_raft"), "main");
}

