package github.com.CraftKernel.publicskintotems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class SkinTotemItemRenderer {
    private static PlayerModel<AbstractClientPlayer> wideModel;
    private static PlayerModel<AbstractClientPlayer> slimModel;

    private SkinTotemItemRenderer() {
    }

    public static void render(ItemStack stack, ItemDisplayContext mode, boolean leftHanded, PoseStack poseStack,
                              MultiBufferSource buffers, int light, int overlay, BakedModel itemModel) {
        UUID owner = SkinTotemData.getOwner(stack);
        if (owner == null) {
            return;
        }

        ResourceLocation skin = SkinResolver.getSkinTexture(owner, SkinTotemData.getOwnerName(stack));
        PlayerModel<AbstractClientPlayer> model = getModel(SkinResolver.isSlim(owner));

        poseStack.pushPose();
        itemModel.getTransforms().getTransform(mode).apply(leftHanded, poseStack);
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        poseStack.translate(0.5f, 0.0f, 0.5f);
        float scale = 0.46f;
        poseStack.scale(scale, scale, scale);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        poseStack.translate(0.0f, -1.587f, 0.0f);

        model.young = false;
        model.crouching = false;

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(skin));
        model.renderToBuffer(poseStack, consumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);

        ResourceLocation cape = SkinResolver.getCapeTexture(owner);
        if (cape != null) {
            poseStack.pushPose();
            poseStack.translate(0.0f, 0.0f, 0.125f);
            poseStack.mulPose(Axis.XP.rotationDegrees(6.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            model.renderCloak(poseStack, buffers.getBuffer(RenderType.entitySolid(cape)), light, overlay);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static PlayerModel<AbstractClientPlayer> getModel(boolean slim) {
        Minecraft mc = Minecraft.getInstance();
        if (slim) {
            if (slimModel == null) {
                slimModel = new PlayerModel<>(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true);
            }
            return slimModel;
        }
        if (wideModel == null) {
            wideModel = new PlayerModel<>(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
        }
        return wideModel;
    }
}
