package github.com.CraftKernel.publicskintotems;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.List;

public final class SkinTotemCommands {
    private SkinTotemCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("skintotem")
                .requires(ServerCompat::canUse)
                .then(Commands.literal("reset")
                        .executes(ctx -> reset(ctx.getSource(), List.of(ctx.getSource().getPlayerOrException())))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> reset(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets")))))
                .then(Commands.literal("give")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> give(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                        List.of(ctx.getSource().getPlayerOrException())))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> give(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                EntityArgument.getPlayers(ctx, "targets")))))));
    }

    private static int reset(CommandSourceStack source, Collection<ServerPlayer> players) {
        int count = 0;
        for (ServerPlayer player : players) {
            Inventory inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (SkinTotemData.isClaimed(stack)) {
                    SkinTotemData.unclaim(stack);
                    count++;
                }
            }
        }
        int total = count;
        source.sendSuccess(() -> Component.literal("Reset " + total + " skin totem(s)"), true);
        return count;
    }

    private static int give(CommandSourceStack source, String name, Collection<ServerPlayer> players) {
        if (!ProfileResolver.isValidName(name)) {
            source.sendFailure(Component.literal(name + " is not a valid username"));
            return 0;
        }
        ProfileResolver.request(source.getServer(), name, profile -> {
            if (profile == null) {
                source.sendFailure(Component.literal("Could not find a player named " + name));
                return;
            }
            for (ServerPlayer target : players) {
                ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
                SkinTotemData.claim(totem, profile.id(), profile.name());
                if (!target.getInventory().add(totem)) {
                    target.level().addFreshEntity(new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), totem));
                }
            }
            source.sendSuccess(() -> Component.literal("Gave " + profile.name() + " totem to " + players.size()
                    + " player(s)"), true);
        });
        return 1;
    }
}
