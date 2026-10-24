package github.com.CraftKernel.publicskintotems;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class ProfileResolver {
    private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_]{3,16}");
    private static final long MISS_MS = 60_000L;

    private static final Map<String, ProfileInfo> FOUND = new ConcurrentHashMap<>();
    private static final Map<String, Long> MISSING = new ConcurrentHashMap<>();
    private static final Set<String> PENDING = ConcurrentHashMap.newKeySet();
    private static final ExecutorService POOL = Executors.newFixedThreadPool(2, task -> {
        Thread thread = new Thread(task, "publicskintotems-lookup");
        thread.setDaemon(true);
        return thread;
    });

    private ProfileResolver() {
    }

    public static boolean isValidName(String name) {
        return name != null && VALID_NAME.matcher(name).matches();
    }

    public static ProfileInfo cached(MinecraftServer server, String name) {
        String key = name.toLowerCase(Locale.ROOT);
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(name);
        if (player != null) {
            ProfileInfo online = new ProfileInfo(player.getUuid(), player.getGameProfile().getName());
            FOUND.put(key, online);
            return online;
        }
        return FOUND.get(key);
    }

    private static boolean isKnownMissing(String key) {
        Long when = MISSING.get(key);
        return when != null && System.currentTimeMillis() - when < MISS_MS;
    }

    public static void request(MinecraftServer server, String name, Consumer<ProfileInfo> done) {
        ProfileInfo hit = cached(server, name);
        if (hit != null) {
            done.accept(hit);
            return;
        }
        String key = name.toLowerCase(Locale.ROOT);
        if (isKnownMissing(key)) {
            done.accept(null);
            return;
        }
        if (!PENDING.add(key)) {
            return;
        }
        POOL.execute(() -> {
            ProfileInfo found = null;
            try {
                found = server.getUserCache().findByName(name).map(p -> new ProfileInfo(p.getId(), p.getName())).orElse(null);
            } catch (RuntimeException ignored) {
            }
            if (found != null) {
                FOUND.put(key, found);
            } else {
                MISSING.put(key, System.currentTimeMillis());
            }
            PENDING.remove(key);
            ProfileInfo result = found;
            server.execute(() -> done.accept(result));
        });
    }
}
