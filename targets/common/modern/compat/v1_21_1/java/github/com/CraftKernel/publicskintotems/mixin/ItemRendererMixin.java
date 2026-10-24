package github.com.CraftKernel.publicskintotems.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import github.com.CraftKernel.publicskintotems.client.SkinTotemDrawer;
import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void publicskintotems$renderSkinTotem(ItemStack stack, ItemDisplayContext mode, boolean leftHanded,
                                                   PoseStack poseStack, MultiBufferSource buffers, int light,
                                                   int overlay, BakedModel model, CallbackInfo ci) {
        if (SkinTotemData.isClaimed(stack)) {
            SkinTotemDrawer.draw(stack, mode, leftHanded, poseStack, buffers, light, overlay, model);
            ci.cancel();
        }
    }
}
