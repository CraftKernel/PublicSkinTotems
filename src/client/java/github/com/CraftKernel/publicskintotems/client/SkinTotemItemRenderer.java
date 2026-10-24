package github.com.CraftKernel.publicskintotems.client;

import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.UUID;

public final class SkinTotemItemRenderer {
    private static PlayerEntityModel<AbstractClientPlayerEntity> wideModel;
    private static PlayerEntityModel<AbstractClientPlayerEntity> slimModel;

    private SkinTotemItemRenderer() {
    }

    public static void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded,
                              MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              int light, int overlay, BakedModel model) {
        UUID owner = SkinTotemData.getOwner(stack);
        if (owner == null) {
            return;
        }

        Identifier skin = SkinResolver.getSkinTexture(owner, SkinTotemData.getOwnerName(stack));
        boolean slim = SkinResolver.isSlim(owner);
        PlayerEntityModel<AbstractClientPlayerEntity> playerModel = getModel(slim);

        matrices.push();
        model.getTransformation().getTransformation(mode).apply(leftHanded, matrices);
        matrices.translate(-0.5f, -0.5f, -0.5f);

        matrices.translate(0.5f, 0.0f, 0.5f);
        float scale = 0.46f;
        matrices.scale(scale, scale, scale);
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrices.translate(0.0f, -1.587f, 0.0f);

        playerModel.child = false;
        playerModel.sneaking = false;

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin));
        playerModel.render(matrices, consumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);

        Identifier cape = SkinResolver.getCapeTexture(owner);
        if (cape != null) {
            matrices.push();
            matrices.translate(0.0f, 0.0f, 0.125f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            VertexConsumer capeConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cape));
            playerModel.renderCape(matrices, capeConsumer, light, overlay);
            matrices.pop();
        }

        matrices.pop();
    }

    private static PlayerEntityModel<AbstractClientPlayerEntity> getModel(boolean slim) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (slim) {
            if (slimModel == null) {
                slimModel = new PlayerEntityModel<>(
                        mc.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_SLIM), true);
            }
            return slimModel;
        }
        if (wideModel == null) {
            wideModel = new PlayerEntityModel<>(
                    mc.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER), false);
        }
        return wideModel;
    }
}
