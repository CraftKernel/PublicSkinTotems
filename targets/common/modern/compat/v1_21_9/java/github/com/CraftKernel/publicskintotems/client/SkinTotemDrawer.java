package github.com.CraftKernel.publicskintotems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.Map;
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
    private static PlayerCapeModel capeModel;
    private static final AvatarRenderState POSE = new AvatarRenderState();

    private SkinTotemDrawer() {
    }

    public static void apply(ItemStackRenderState.LayerRenderState layer, ItemStack stack) {
        layer.setupSpecialModel(SkinTotemSpecialRenderer.INSTANCE, extractArgument(stack));
        layer.setExtents(EXTENTS_SUPPLIER);
    }

    public static UUID extractArgument(ItemStack stack) {
        UUID owner = SkinTotemData.getOwner(stack);
        if (owner != null) {
            PROFILES.computeIfAbsent(owner, ResolvableProfile::createUnresolved);
        }
        return owner;
    }

    public static void getExtents(Set<Vector3f> output) {
        for (Vector3f v : EXTENTS) {
            output.add(v);
        }
    }

    public static void draw(UUID owner, PoseStack poseStack, SubmitNodeCollector collector,
                            int light, int overlay, int outlineColor) {
        if (owner == null) {
            return;
        }
        ResolvableProfile profile = PROFILES.computeIfAbsent(owner, ResolvableProfile::createUnresolved);
        PlayerSkinRenderCache.RenderInfo info = Minecraft.getInstance().playerSkinRenderCache().getOrDefault(profile);
        PlayerModel model = getModel(info.playerSkin().model() == PlayerModelType.SLIM);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);
        float scale = 0.46f;
        poseStack.scale(scale, scale, scale);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        Compat.rotateY(poseStack, 180.0f);
        poseStack.translate(0.0f, -1.587f, 0.0f);

        Compat.submitModel(collector, model, POSE, poseStack, info.renderType(), light, overlay, outlineColor);

        if (info.playerSkin().cape() != null) {
            if (capeModel == null) {
                capeModel = new PlayerCapeModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_CAPE));
            }
            capeModel.setupAnim(POSE);
            Compat.submitModel(collector, capeModel, POSE, poseStack,
                    Compat.capeType(info.playerSkin().cape().texturePath()), light, overlay, outlineColor);
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
