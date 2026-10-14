package github.com.Endyboii.publicskintotems.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves a player's skin texture (and slim/wide arm model) from their UUID on
 * the client. Lookup order:
 * <ol>
 *   <li>Online player -> read straight from the tab-list.</li>
 *   <li>Seen earlier this session -> cached value (survives the owner logging off).</li>
 *   <li>Never seen -> fetch from Mojang's session servers in the background, showing
 *       the default Steve/Alex skin until the real one arrives.</li>
 * </ol>
 */
public final class SkinResolver {

    private static final Map<UUID, Identifier> SKINS = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> SLIM = new ConcurrentHashMap<>();
    /** UUIDs we've already kicked off a background fetch for, so we only ask once. */
    private static final Set<UUID> REQUESTED = ConcurrentHashMap.newKeySet();

    private SkinResolver() {
    }

    public static Identifier getSkinTexture(UUID uuid, String name) {
        MinecraftClient mc = MinecraftClient.getInstance();

        // 1. Online player -> the tab-list is authoritative (picks up skin changes). Only write the
        //    caches when the skin actually changed; this runs on every render of a claimed totem.
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

        // 2. Seen earlier this session (or already fetched).
        Identifier cached = SKINS.get(uuid);
        if (cached != null) {
            return cached;
        }

        // 3. Never seen -> fetch from the session servers once, in the background.
        requestSkin(uuid, name);
        return DefaultSkinHelper.getTexture(uuid);
    }

    public static boolean isSlim(UUID uuid) {
        return SLIM.getOrDefault(uuid, Boolean.FALSE);
    }

    private static void requestSkin(UUID uuid, String name) {
        if (name == null || name.isEmpty() || !REQUESTED.add(uuid)) {
            return;
        }

        GameProfile profile = new GameProfile(uuid, name);
        // Fill in the textures property from the session servers, then download the skin.
        SkullBlockEntity.loadProperties(profile, filled ->
                MinecraftClient.getInstance().getSkinProvider().loadSkin(filled, (type, id, texture) -> {
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        SKINS.put(uuid, id);
                        SLIM.put(uuid, texture != null && "slim".equals(texture.getMetadata("model")));
                    }
                }, false));
    }
}
