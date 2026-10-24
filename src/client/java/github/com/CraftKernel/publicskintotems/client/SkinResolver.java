package github.com.CraftKernel.publicskintotems.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class SkinResolver {
    private static final Map<UUID, Identifier> SKINS = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> SLIM = new ConcurrentHashMap<>();
    private static final Map<UUID, Identifier> CAPES = new ConcurrentHashMap<>();

    private static final Set<UUID> REQUESTED = ConcurrentHashMap.newKeySet();

    private SkinResolver() {
    }

    public static Identifier getSkinTexture(UUID uuid, String name) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.getNetworkHandler() != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(uuid);
            if (entry != null) {
                Identifier id = entry.getSkinTexture();
                if (!id.equals(SKINS.get(uuid))) {
                    SKINS.put(uuid, id);
                    SLIM.put(uuid, "slim".equals(entry.getModel()));
                }
                return id;
            }
        }

        Identifier cached = SKINS.get(uuid);
        if (cached != null) {
            return cached;
        }

        requestSkin(uuid, name);
        return DefaultSkinHelper.getTexture(uuid);
    }

    public static Identifier getCapeTexture(UUID uuid) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(uuid);
            if (entry != null) {
                Identifier id = entry.getCapeTexture();
                if (id != null) {
                    CAPES.put(uuid, id);
                } else {
                    CAPES.remove(uuid);
                }
                return id;
            }
        }
        return CAPES.get(uuid);
    }

    public static boolean isSlim(UUID uuid) {
        return SLIM.getOrDefault(uuid, Boolean.FALSE);
    }

    private static void requestSkin(UUID uuid, String name) {
        if (name == null || name.isEmpty() || !REQUESTED.add(uuid)) {
            return;
        }

        GameProfile profile = new GameProfile(uuid, name);

        SkullBlockEntity.loadProperties(profile, filled ->
                MinecraftClient.getInstance().getSkinProvider().loadSkin(filled, (type, id, texture) -> {
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        SKINS.put(uuid, id);
                        SLIM.put(uuid, texture != null && "slim".equals(texture.getMetadata("model")));
                    } else if (type == MinecraftProfileTexture.Type.CAPE) {
                        CAPES.put(uuid, id);
                    }
                }, false));
    }
}
