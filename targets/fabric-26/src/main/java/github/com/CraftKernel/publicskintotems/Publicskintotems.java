package github.com.CraftKernel.publicskintotems;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class Publicskintotems implements ModInitializer {
    private static final int SCAN_INTERVAL = 8;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SkinTotemCommands.register(dispatcher));
        ServerTickEvents.END_SERVER_TICK.register(server -> {
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
