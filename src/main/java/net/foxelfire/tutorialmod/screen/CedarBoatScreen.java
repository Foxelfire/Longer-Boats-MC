package net.foxelfire.tutorialmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CedarBoatScreen extends HandledScreen<CedarBoatScreenHandler>{

    private static final Identifier TEXTURE = new Identifier(TutorialMod.MOD_ID, "textures/gui/boat_tab.png");

    public CedarBoatScreen(CedarBoatScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init(){
        super.init();
        titleX = 32;
        titleY = 5;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram); // normal shader menu stuff, apparently
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE); // breaking news: menus are shaders
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight); // u and v (the 2 zeroes) are offsets
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    } 
}