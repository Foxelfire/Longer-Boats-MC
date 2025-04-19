package net.foxelfire.longer_boats.entity.client;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
        public static final EntityModelLayer LONG_BOAT =
                new EntityModelLayer(new Identifier(LongerBoatsMod.MOD_ID, "long_boat"), "main");
        public static final EntityModelLayer LONG_RAFT =
                new EntityModelLayer(new Identifier(LongerBoatsMod.MOD_ID, "long_raft"), "main");
}

