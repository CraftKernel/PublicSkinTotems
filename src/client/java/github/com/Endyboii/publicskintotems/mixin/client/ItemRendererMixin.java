package github.com.Endyboii.publicskintotems.mixin.client;

import github.com.Endyboii.publicskintotems.SkinTotemData;
import github.com.Endyboii.publicskintotems.client.SkinTotemItemRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    /**
     * For a claimed totem, draw the owner's skin as a 3D player model and skip the
     * vanilla flat totem. Unclaimed totems fall through and render normally.
     */
    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void publicskintotems$renderSkinTotem(ItemStack stack, ModelTransformationMode renderMode,
                                                   boolean leftHanded, MatrixStack matrices,
                                                   VertexConsumerProvider vertexConsumers, int light,
                                                   int overlay, BakedModel model, CallbackInfo ci) {
        if (SkinTotemData.isClaimed(stack)) {
            SkinTotemItemRenderer.render(stack, renderMode, leftHanded, matrices, vertexConsumers,
                    light, overlay, model);
            ci.cancel();
        }
    }
}
