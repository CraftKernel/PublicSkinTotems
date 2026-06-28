package github.com.Endyboii.publicskintotems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.UUID;

public class Publicskintotems implements ModInitializer {

    /** Re-scan inventories this often (ticks). Cheap; no need to do it every tick. */
    private static final int SCAN_INTERVAL = 8;

    @Override
    public void onInitialize() {
        // Auto-claim: any plain totem sitting in a player's inventory becomes that
        // player's skin totem. Totems already owned (by you or anyone else) are left
        // untouched, so you can carry and use other people's totems with their skin.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % SCAN_INTERVAL != 0) {
                return;
            }
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                claimInInventory(player);
            }
        });
    }

    private static void claimInInventory(ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        UUID uuid = player.getUuid();
        String name = player.getGameProfile().getName();
        claimList(inv.main, uuid, name);
        claimList(inv.offHand, uuid, name);
    }

    private static void claimList(DefaultedList<ItemStack> list, UUID uuid, String name) {
        for (ItemStack stack : list) {
            if (SkinTotemData.isUnclaimedTotem(stack)) {
                SkinTotemData.claim(stack, uuid, name);
            }
        }
    }
}
