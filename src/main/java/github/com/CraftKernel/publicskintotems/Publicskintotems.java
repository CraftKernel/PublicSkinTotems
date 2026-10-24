package github.com.CraftKernel.publicskintotems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.UUID;

public class Publicskintotems implements ModInitializer {
    private static final int SCAN_INTERVAL = 8;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SkinTotemCommands.register(dispatcher));
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
