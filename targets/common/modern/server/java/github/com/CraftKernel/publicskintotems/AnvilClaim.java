package github.com.CraftKernel.publicskintotems;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public final class AnvilClaim {
    private static final int INPUT = 0;
    private static final int RESULT = 2;
    private static final int FIRST_PLAYER_SLOT = 3;

    private AnvilClaim() {
    }

    public static void update(AnvilMenu menu) {
        if (menu.slots.size() <= FIRST_PLAYER_SLOT) {
            return;
        }
        ItemStack input = menu.getSlot(INPUT).getItem();
        ItemStack result = menu.getSlot(RESULT).getItem();
        if (!SkinTotemData.isTotem(input) || !SkinTotemData.isTotem(result)) {
            return;
        }
        String name = result.getHoverName().getString().trim();
        if (!ProfileResolver.isValidName(name)) {
            return;
        }
        if (!(menu.getSlot(FIRST_PLAYER_SLOT).container instanceof Inventory inventory)) {
            return;
        }
        Player player = inventory.player;
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return;
        }
        ProfileInfo profile = ProfileResolver.cached(server, name);
        if (profile != null) {
            SkinTotemData.claim(result, profile.id(), profile.name());
            menu.broadcastChanges();
            return;
        }
        ProfileResolver.request(server, name, found -> {
            if (found != null && player.containerMenu == menu) {
                menu.createResult();
            }
        });
    }
}
