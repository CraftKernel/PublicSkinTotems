package github.com.CraftKernel.publicskintotems;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("publicskintotems")
public class Publicskintotems {
    private static final int SCAN_INTERVAL = 8;

    public Publicskintotems() {
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> SkinTotemCommands.register(event.getDispatcher()));
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            var server = event.getServer();
            if (server.getTickCount() % SCAN_INTERVAL != 0) {
                return;
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                claimInInventory(player);
            }
        });
    }

    private static void claimInInventory(ServerPlayer player) {
        Inventory inv = player.getInventory();
        for (int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack stack = inv.getItem(slot);
            if (SkinTotemData.isUnclaimedTotem(stack)) {
                SkinTotemData.claim(stack, player.getUUID(), player.getGameProfile().getName());
            }
        }
    }
}
