package github.com.CraftKernel.publicskintotems;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ServerCompat {
    private ServerCompat() {
    }

    public static boolean canUse(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    public static ProfileInfo online(MinecraftServer server, String name) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        return player == null ? null : new ProfileInfo(player.getUUID(), SkinTotemData.playerName(player));
    }

    public static ProfileInfo lookup(MinecraftServer server, String name) {
        return server.services().nameToIdCache().get(name).map(p -> new ProfileInfo(p.id(), p.name())).orElse(null);
    }
}
