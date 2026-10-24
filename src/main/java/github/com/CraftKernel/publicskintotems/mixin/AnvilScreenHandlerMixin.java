package github.com.CraftKernel.publicskintotems.mixin;

import github.com.CraftKernel.publicskintotems.AnvilClaim;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Inject(method = "updateResult", at = @At("TAIL"))
    private void publicskintotems$claimFromName(CallbackInfo ci) {
        AnvilClaim.update((AnvilScreenHandler) (Object) this);
    }
}
