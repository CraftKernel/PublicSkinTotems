package github.com.CraftKernel.publicskintotems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.UUID;

public final class SkinTotemSpecialRenderer implements SpecialModelRenderer<UUID> {
    public static final SkinTotemSpecialRenderer INSTANCE = new SkinTotemSpecialRenderer();

    private SkinTotemSpecialRenderer() {
    }

    @Override
    public @Nullable UUID extractArgument(ItemStack stack) {
        return SkinTotemDrawer.extractArgument(stack);
    }

    @Override
    public void submit(@Nullable UUID owner, PoseStack poseStack, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasFoil, int outlineColor) {
        SkinTotemDrawer.draw(owner, poseStack, collector, light, overlay, outlineColor);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        SkinTotemDrawer.getExtents(output);
    }
}
