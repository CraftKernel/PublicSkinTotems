package github.com.CraftKernel.publicskintotems.client;

import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "publicskintotems", value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (SkinTotemData.isClaimed(event.getItemStack())) {
            String name = SkinTotemData.getOwnerName(event.getItemStack());
            event.getToolTip().add(Component.literal("Skin Totem of " + (name.isEmpty() ? "?" : name))
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
