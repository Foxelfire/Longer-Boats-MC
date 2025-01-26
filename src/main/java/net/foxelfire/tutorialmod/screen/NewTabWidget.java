package net.foxelfire.tutorialmod.screen;

import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget.NarrationSupplier;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class NewTabWidget extends ClickableWidget {

    private static final ButtonTextures TEXTURES = new ButtonTextures(new Identifier(TutorialMod.MOD_ID, "textures/gui/boat_buttons"), new Identifier(TutorialMod.MOD_ID, "textures/gui/boat_selected_buttons"));
    protected static final NarrationSupplier DEFAULT_NARRATION_SUPPLIER = textSupplier -> (MutableText)textSupplier.get();
    protected final NarrationSupplier narrationSupplier;
    protected final PressAction onPress;
    protected final boolean isLeft;

    public NewTabWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier, boolean isLeft) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.narrationSupplier = narrationSupplier;
        this.isLeft = isLeft;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress.onPress(this);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(TEXTURES.get(true, this.isHovered()), this.getX(), this.getY(), 0, this.isLeft ? 0 : 17, this.getWidth(), this.getHeight());
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private boolean isLeft;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

        public Builder(Text message, boolean isLeft, PressAction onPress) {
            this.isLeft = isLeft;
            this.message = message;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public NewTabWidget build() {
            NewTabWidget buttonWidget = new NewTabWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier, this.isLeft);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface PressAction {
        public void onPress(NewTabWidget var1);
    }

}
