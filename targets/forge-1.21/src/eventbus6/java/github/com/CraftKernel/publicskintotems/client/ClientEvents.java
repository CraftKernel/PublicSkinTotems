package github.com.CraftKernel.publicskintotems.client;

import github.com.CraftKernel.publicskintotems.SkinTotemData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onTooltip);
    }

    private static void onTooltip(ItemTooltipEvent event) {
        if (SkinTotemData.isClaimed(event.getItemStack())) {
            String name = SkinTotemData.getOwnerName(event.getItemStack());
            event.getToolTip().add(Component.literal("Skin Totem of " + (name.isEmpty() ? "?" : name))
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
