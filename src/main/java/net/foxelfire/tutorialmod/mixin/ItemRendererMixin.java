package net.foxelfire.tutorialmod.mixin;
// No comments, sorry future me - see disclaimer in ItemRendererAccessor if you're sure u want to remember why
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useLuminousStaffModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.LUMINOUS_WAND) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.FIXED) {
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(TutorialMod.MOD_ID, "light_wand_3d", "inventory"));
        } // jk: put another copy-pasted if here for another 3d item
        // also the existing one might throw an error cause we're giving variant of "inventory" to something in an item frame - no clue how to fix that if it happens
        return value;
    }
}