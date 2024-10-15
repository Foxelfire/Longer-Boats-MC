package net.foxelfire.tutorialmod.entity.client;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.util.Identifier;

public class CedarBoatRenderer extends EntityRenderer<CedarBoatEntity>{
    private static final Identifier TEXTURE = new Identifier(TutorialMod.MOD_ID, "textures/entity/cedar_boat.png");

    public CedarBoatRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(CedarBoatEntity var1) {
        return TEXTURE;
    }

}
