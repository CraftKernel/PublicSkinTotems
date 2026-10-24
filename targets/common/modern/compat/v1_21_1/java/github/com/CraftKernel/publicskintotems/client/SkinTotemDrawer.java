package github.com.CraftKernel.publicskintotems.client;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class SkinTotemDrawer {
    private static final Map<UUID, ResolvableProfile> PROFILES = new ConcurrentHashMap<>();

    private static Model wideModel;
    private static Model slimModel;

    private SkinTotemDrawer() {
    }

    private static ResolvableProfile profile(UUID owner) {
        return PROFILES.computeIfAbsent(owner, id -> {
            ResolvableProfile unresolved = new ResolvableProfile(Optional.empty(), Optional.of(id), new PropertyMap());

            unresolved.resolve().thenAccept(resolved -> PROFILES.put(id, resolved));
            return unresolved;
        });
    }

    public static void draw(ItemStack stack, ItemDisplayContext mode, boolean leftHanded, PoseStack poseStack,
                            MultiBufferSource buffers, int light, int overlay, BakedModel itemModel) {
        UUID owner = SkinTotemData.getOwner(stack);
        if (owner == null) {
            return;
        }
        PlayerSkin skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile(owner).gameProfile());
        Model model = getModel(skin.model() == PlayerSkin.Model.SLIM);

        poseStack.pushPose();
        itemModel.getTransforms().getTransform(mode).apply(leftHanded, poseStack);
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        poseStack.translate(0.5f, 0.0f, 0.5f);
        float scale = 0.46f;
        poseStack.scale(scale, scale, scale);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        poseStack.translate(0.0f, -1.587f, 0.0f);

        model.renderToBuffer(poseStack, buffers.getBuffer(RenderType.entityTranslucent(skin.texture())),
                light, overlay, -1);

        if (skin.capeTexture() != null) {
            poseStack.pushPose();
            poseStack.translate(0.0f, 0.0f, 0.125f);
            poseStack.mulPose(Axis.XP.rotationDegrees(6.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            ((PlayerModel) model).renderCloak(poseStack,
                    buffers.getBuffer(RenderType.entitySolid(skin.capeTexture())), light, overlay);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Model getModel(boolean slim) {
        if (slim) {
            if (slimModel == null) {
                slimModel = new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true);
            }
            return slimModel;
        }
        if (wideModel == null) {
            wideModel = new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
        }
        return wideModel;
    }
}
