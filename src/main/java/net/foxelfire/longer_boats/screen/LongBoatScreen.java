package net.foxelfire.longer_boats.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class LongBoatScreen extends HandledScreen<LongBoatScreenHandler>{
    public NewTabWidget previous;
    public NewTabWidget next;
    public DefaultedList<ItemStack> tabInventory;
    private static final Identifier TEXTURE = new Identifier(LongerBoatsMod.MOD_ID, "textures/gui/boat_tab.png");

    public LongBoatScreen(LongBoatScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init(){
        super.init();
        titleX = 4;
        titleY = -10;
        playerInventoryTitleX = 4;
        playerInventoryTitleY = 78;
        int x = (width - backgroundWidth) / 2 + (int)(backgroundWidth/2.2);
        int y = (height - backgroundHeight)/2;
        previous = NewTabWidget.builder(Text.literal("Previous Tab"), true, button -> {
            handler.switchTab(handler.currentTab - 1);
        })
        .dimensions(x - (int)(backgroundWidth / 8), y, 24, 24)
        .tooltip(Tooltip.of(Text.literal("Previous Tab Button")))
        .build();
        next = NewTabWidget.builder(Text.literal("Next Tab"), false, button -> {
            handler.switchTab(handler.currentTab + 1);
        })
        .dimensions(x + (int)(backgroundWidth / 8), y, 24, 24)
        .tooltip(Tooltip.of(Text.literal("Next Tab Button")))
        .build();
        addDrawableChild(previous);
        addDrawableChild(next);
    }
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram); // normal shader menu stuff, apparently
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE); // breaking news: menus are shaders
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight)/2;
        context.drawTexture(TEXTURE, x, y-16, 0, 0, backgroundWidth, backgroundHeight+32); // u and v (the 2 zeroes) are offsets
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        int magicXOffset = (width - backgroundWidth) / 2 + (int)(backgroundWidth/2.4);
        int magicYOffset = (int)((height - backgroundHeight-32)/2 + backgroundHeight/7.75);
        context.drawText(textRenderer, (handler.currentTab+1) + " / " + handler.entity.getNumberOfChests(), magicXOffset, magicYOffset, 0x303030, false);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}