package github.com.CraftKernel.publicskintotems.mixin;

import github.com.CraftKernel.publicskintotems.access.SkinTotemRenderStateAccess;
import github.com.CraftKernel.publicskintotems.client.SkinTotemDrawer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStackRenderState.class)
public abstract class ItemStackRenderStateMixin implements SkinTotemRenderStateAccess {
    @Shadow
    private int activeLayerCount;

    @Shadow
    private ItemStackRenderState.LayerRenderState[] layers;

    @Override
    public void publicskintotems$applySkin(ItemStack stack) {
        for (int i = 0; i < this.activeLayerCount; i++) {
            SkinTotemDrawer.apply(this.layers[i], stack);
        }
    }
}
