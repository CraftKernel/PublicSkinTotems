package github.com.CraftKernel.publicskintotems;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod("publicskintotems")
public class Publicskintotems {
    private static final int SCAN_INTERVAL = 8;

    public Publicskintotems() {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> SkinTotemCommands.register(event.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> {
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
                SkinTotemData.claim(stack, player.getUUID(), SkinTotemData.playerName(player));
            }
        }
    }
}
