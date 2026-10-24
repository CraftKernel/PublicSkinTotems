package github.com.CraftKernel.publicskintotems;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ServerCompat {
    private ServerCompat() {
    }

    public static boolean canUse(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    public static ProfileInfo online(MinecraftServer server, String name) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        return player == null ? null : new ProfileInfo(player.getUUID(), player.getGameProfile().getName());
    }

    public static ProfileInfo lookup(MinecraftServer server, String name) {
        return server.getProfileCache().get(name).map(p -> new ProfileInfo(p.getId(), p.getName())).orElse(null);
    }
}
