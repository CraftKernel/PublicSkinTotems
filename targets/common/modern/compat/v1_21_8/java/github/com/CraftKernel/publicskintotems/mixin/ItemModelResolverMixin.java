package github.com.CraftKernel.publicskintotems.mixin;

import github.com.CraftKernel.publicskintotems.access.SkinTotemRenderStateAccess;
import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {
    @Inject(method = "appendItemLayers", at = @At("TAIL"))
    private void publicskintotems$skinTotem(ItemStackRenderState output, ItemStack item, ItemDisplayContext context,
                                            Level level, LivingEntity owner, int seed, CallbackInfo ci) {
        if (SkinTotemData.isClaimed(item)) {
            ((SkinTotemRenderStateAccess) output).publicskintotems$applySkin(item);

            UUID skinOwner = SkinTotemData.getOwner(item);
            output.appendModelIdentityElement(skinOwner);
            output.setAnimated();
        }
    }
}
