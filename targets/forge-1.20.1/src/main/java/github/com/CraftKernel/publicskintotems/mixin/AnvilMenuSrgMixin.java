package github.com.CraftKernel.publicskintotems.mixin;

import github.com.CraftKernel.publicskintotems.AnvilClaim;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuSrgMixin {
    @Inject(method = {"createResult", "m_6640_"}, at = @At("TAIL"))
    private void publicskintotems$claimFromName(CallbackInfo ci) {
        AnvilClaim.update((AnvilMenu) (Object) this);
    }
}
