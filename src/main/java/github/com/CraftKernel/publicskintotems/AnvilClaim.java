package github.com.CraftKernel.publicskintotems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.MinecraftServer;

public final class AnvilClaim {
    private static final int INPUT = 0;
    private static final int RESULT = 2;
    private static final int FIRST_PLAYER_SLOT = 3;

    private AnvilClaim() {
    }

    public static void update(AnvilScreenHandler handler) {
        if (handler.slots.size() <= FIRST_PLAYER_SLOT) {
            return;
        }
        ItemStack input = handler.getSlot(INPUT).getStack();
        ItemStack result = handler.getSlot(RESULT).getStack();
        if (!SkinTotemData.isTotem(input) || !SkinTotemData.isTotem(result)) {
            return;
        }
        String name = result.getName().getString().trim();
        if (!ProfileResolver.isValidName(name)) {
            return;
        }
        if (!(handler.getSlot(FIRST_PLAYER_SLOT).inventory instanceof PlayerInventory inventory)) {
            return;
        }
        PlayerEntity player = inventory.player;
        MinecraftServer server = player.getWorld().getServer();
        if (server == null) {
            return;
        }
        ProfileInfo profile = ProfileResolver.cached(server, name);
        if (profile != null) {
            SkinTotemData.claim(result, profile.id(), profile.name());
            handler.sendContentUpdates();
            return;
        }
        ProfileResolver.request(server, name, found -> {
            if (found != null && player.currentScreenHandler == handler) {
                handler.updateResult();
            }
        });
    }
}
