package github.com.Endyboii.publicskintotems.client;

import github.com.Endyboii.publicskintotems.SkinTotemData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.UUID;

/**
 * Draws a claimed totem as a small 3D player model wearing the owner's skin.
 * Called from {@code ItemRendererMixin} in place of vanilla totem rendering.
 */
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

        // Position the model exactly where the vanilla totem item would sit for this
        // render mode (GUI, hand, ground, item frame...), then drop into the standard
        // 0..1 item-cube space.
        model.getTransformation().getTransformation(mode).apply(leftHanded, matrices);
        matrices.translate(-0.5f, -0.5f, -0.5f);

        // Place a ~2-block-tall player model inside that one-block slot.
        matrices.translate(0.5f, 0.0f, 0.5f);   // centre horizontally, sit on the floor
        float scale = 0.46f;                     // shrink to fit the slot (tweak to taste)
        matrices.scale(scale, scale, scale);
        matrices.scale(-1.0f, -1.0f, 1.0f);      // entity models are built upside-down
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f)); // face the viewer
        matrices.translate(0.0f, -1.95f, 0.0f);  // raise the (now upright) feet to the floor

        // Neutral standing pose. We never call setAngles, so all part angles stay 0.
        playerModel.child = false;
        playerModel.sneaking = false;

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin));
        playerModel.render(matrices, consumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);

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
