package github.com.CraftKernel.publicskintotems.client;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class SkinTotemDrawer {
    private static final Map<UUID, ResolvableProfile> PROFILES = new ConcurrentHashMap<>();
    private static final Vector3f[] EXTENTS = {
            new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f)
    };
    private static final Supplier<Vector3f[]> EXTENTS_SUPPLIER = () -> EXTENTS;

    private static PlayerModel wideModel;
    private static PlayerModel slimModel;
    private static PlayerCapeModel<PlayerRenderState> capeModel;
    private static final PlayerRenderState CAPE_POSE = new PlayerRenderState();

    private SkinTotemDrawer() {
    }

    public static void apply(ItemStackRenderState.LayerRenderState layer, ItemStack stack) {
        layer.setupSpecialModel(SkinTotemSpecialRenderer.INSTANCE, extractArgument(stack));
        layer.setExtents(EXTENTS_SUPPLIER);
    }

    public static UUID extractArgument(ItemStack stack) {
        UUID owner = SkinTotemData.getOwner(stack);
        if (owner != null) {
            profile(owner);
        }
        return owner;
    }

    public static void getExtents(Set<Vector3f> output) {
        for (Vector3f v : EXTENTS) {
            output.add(v);
        }
    }

    private static ResolvableProfile profile(UUID owner) {
        return PROFILES.computeIfAbsent(owner,
                id -> new ResolvableProfile(Optional.empty(), Optional.of(id), new PropertyMap()));
    }

    public static void draw(UUID owner, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (owner == null) {
            return;
        }

        ResolvableProfile profile = profile(owner).pollResolve();
        PROFILES.put(owner, profile);
        PlayerSkin skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile.gameProfile());
        PlayerModel model = getModel(skin.model() == PlayerSkin.Model.SLIM);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);
        float scale = 0.46f;
        poseStack.scale(scale, scale, scale);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        poseStack.translate(0.0f, -1.587f, 0.0f);

        model.renderToBuffer(poseStack, buffers.getBuffer(RenderType.entityTranslucent(skin.texture())), light, overlay);

        if (skin.capeTexture() != null) {
            if (capeModel == null) {
                capeModel = new PlayerCapeModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_CAPE));
            }
            capeModel.setupAnim(CAPE_POSE);
            capeModel.renderToBuffer(poseStack, buffers.getBuffer(RenderType.entitySolid(skin.capeTexture())),
                    light, overlay);
        }
        poseStack.popPose();
    }

    private static PlayerModel getModel(boolean slim) {
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
