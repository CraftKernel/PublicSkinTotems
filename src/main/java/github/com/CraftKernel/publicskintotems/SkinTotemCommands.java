package github.com.CraftKernel.publicskintotems;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

public final class SkinTotemCommands {
    private SkinTotemCommands() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("skintotem")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("reset")
                        .executes(ctx -> reset(ctx.getSource(), List.of(ctx.getSource().getPlayerOrThrow())))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(ctx -> reset(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets")))))
                .then(CommandManager.literal("give")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .executes(ctx -> give(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                        List.of(ctx.getSource().getPlayerOrThrow())))
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(ctx -> give(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                EntityArgumentType.getPlayers(ctx, "targets")))))));
    }

    private static int reset(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        int count = 0;
        for (ServerPlayerEntity player : players) {
            PlayerInventory inventory = player.getInventory();
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack stack = inventory.getStack(slot);
                if (SkinTotemData.isClaimed(stack)) {
                    SkinTotemData.unclaim(stack);
                    count++;
                }
            }
        }
        int total = count;
        source.sendFeedback(() -> Text.literal("Reset " + total + " skin totem(s)"), true);
        return count;
    }

    private static int give(ServerCommandSource source, String name, Collection<ServerPlayerEntity> players) {
        if (!ProfileResolver.isValidName(name)) {
            source.sendError(Text.literal(name + " is not a valid username"));
            return 0;
        }
        ProfileResolver.request(source.getServer(), name, profile -> {
            if (profile == null) {
                source.sendError(Text.literal("Could not find a player named " + name));
                return;
            }
            for (ServerPlayerEntity target : players) {
                ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
                SkinTotemData.claim(totem, profile.id(), profile.name());
                if (!target.getInventory().insertStack(totem)) {
                    target.getWorld().spawnEntity(new ItemEntity(target.getWorld(), target.getX(), target.getY(), target.getZ(), totem));
                }
            }
            source.sendFeedback(() -> Text.literal("Gave " + profile.name() + " totem to " + players.size()
                    + " player(s)"), true);
        });
        return 1;
    }
}
