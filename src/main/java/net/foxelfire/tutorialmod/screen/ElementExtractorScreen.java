package net.foxelfire.tutorialmod.screen;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.entity.ElementExtractorBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElementExtractorScreen extends HandledScreen<ElementExtractorScreenHandler>{

    private static final Identifier TEXTURE = new Identifier(TutorialMod.MOD_ID, "textures/gui/element_extractor_gui.png");

    @Override
    protected void init(){
        super.init();
        titleY = 5; // yes, this is what looks good to me. i CAN tell the difference.
    }

    public ElementExtractorScreen(ElementExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY){
        RenderSystem.setShader(GameRenderer::getPositionTexProgram); // normal shader menu stuff, apparently
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE); // breaking news: menus are shaders
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight); // u and v (the 2 zeroes) are offsets
        renderProgressArrow(x, y, context);
    }

    private void renderProgressArrow(int x, int y, DrawContext context) {
        if(handler.isCrafting()){
            /* ok so drawTexture() is a mouthful. I would say Yarn mappings don't name it right, but I can't come up with a better name.
             * the weird added numbers are the top left corner of a square overlapping where the black arrow (in pixels) 
             * is on TEXTURE, starting from TEXTURE's own top left corner (0,0). (yes, this means positive y values go 
             * further *DOWN* in the TEXTURE) the 5th parameter, int u, is the x coordinate (176) of the top left corner of a
             * new square relative to TEXTURE, where a smaller texture within TEXTURE (the sub-texture) we want to draw on top
             * of the other square (overtop the black arrow) is. the next number is the y coordinate of the sub-texture start, 
             * which is 0 because the white arrow that comprises the sub-texture is at the top of TEXTURE. The arrow
             * has a width of 8 pixels which we put in the 6th argument, and a height of whatever the handler wants in the last,
             * which will numerically increase and spacially grow downwards as we "fill in" the black arrow on TEXTURE with the
             * white arrow on the sub-texture.
             * here is drawContextInstance.drawTexture(fullTexture, drawLocationX, drawLocationY, subtextureX, subtextureY,
             * subtextureWidth, subtextureHeight). God, I love Minecraft's texture system.
             */
            context.drawTexture(TEXTURE, x+85, y+35, 176, 0, 8, handler.getArrowScaledProgress());
        }
        if(!handler.getFuel().equals(Items.AIR)){
            /* textures for fuel storage bars are stored 5 pixels down from each other,
             * in the same order as they're stored in the array POSSIBLE_FUELS, so we
             * can perform some pixel offset magic to get the Y-coordinate from their 
             * array indices. Textures immediately start 26 pixels down from top left
             * corner, which makes the first fuel's index, 0, cancel out nicely. */
            int yCord = (Arrays.asList(ElementExtractorBlockEntity.POSSIBLE_FUELS)
            .indexOf(handler.getFuel()) * 5) + 26;
            context.drawTexture(TEXTURE, x+60, y+55, 176, yCord, handler.getFuelRemaining(), 6);
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
