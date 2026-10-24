package github.com.CraftKernel.publicskintotems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.ResourceLocation;

final class Compat {
    private Compat() {
    }

    static void rotateY(PoseStack poseStack, float degrees) {
        poseStack.mulPose(Axis.YP.rotationDegrees(degrees));
    }

    static <S> void submitModel(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack poseStack,
                                RenderType type, int light, int overlay, int outline) {
        collector.submitModel(model, state, poseStack, type, light, overlay, outline, null);
    }

    static RenderType capeType(ResourceLocation texture) {
        return RenderType.entitySolid(texture);
    }
}
