package github.com.CraftKernel.publicskintotems.client;

import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PublicskintotemsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (SkinTotemData.isClaimed(stack)) {
                String name = SkinTotemData.getOwnerName(stack);
                lines.add(Text.literal("Skin Totem of " + (name.isEmpty() ? "?" : name))
                        .formatted(Formatting.GOLD));
            }
        });
    }
}
