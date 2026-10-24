package github.com.CraftKernel.publicskintotems.mixin;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface LayerRenderStateAccessor {
    @Accessor("model")
    BakedModel publicskintotems$getModel();
}
