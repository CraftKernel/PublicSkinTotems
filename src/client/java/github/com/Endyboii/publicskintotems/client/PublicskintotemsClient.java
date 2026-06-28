package github.com.Endyboii.publicskintotems.client;

import github.com.Endyboii.publicskintotems.SkinTotemData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PublicskintotemsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Show whose skin a claimed totem carries.
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (SkinTotemData.isClaimed(stack)) {
                String name = SkinTotemData.getOwnerName(stack);
                lines.add(Text.literal("Skin Totem of " + (name.isEmpty() ? "?" : name))
                        .formatted(Formatting.GOLD));
            }
        });
    }
}
