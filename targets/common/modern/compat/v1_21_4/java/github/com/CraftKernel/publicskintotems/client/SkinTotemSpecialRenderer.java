package github.com.CraftKernel.publicskintotems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class SkinTotemSpecialRenderer implements SpecialModelRenderer<UUID> {
    public static final SkinTotemSpecialRenderer INSTANCE = new SkinTotemSpecialRenderer();

    private SkinTotemSpecialRenderer() {
    }

    @Override
    public UUID extractArgument(ItemStack stack) {
        return SkinTotemDrawer.extractArgument(stack);
    }

    @Override
    public void render(UUID owner, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers,
                       int light, int overlay, boolean hasFoil) {
        SkinTotemDrawer.draw(owner, poseStack, buffers, light, overlay);
    }
}
