package github.com.CraftKernel.publicskintotems.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class SkinResolver {
    private static final Map<UUID, ResourceLocation> SKINS = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> SLIM = new ConcurrentHashMap<>();
    private static final Map<UUID, ResourceLocation> CAPES = new ConcurrentHashMap<>();

    private static final Set<UUID> REQUESTED = ConcurrentHashMap.newKeySet();

    private SkinResolver() {
    }

    public static ResourceLocation getSkinTexture(UUID uuid, String name) {
        ResourceLocation cached = SKINS.get(uuid);
        Minecraft mc = Minecraft.getInstance();

        if (mc.getConnection() != null) {
            PlayerInfo entry = mc.getConnection().getPlayerInfo(uuid);
            if (entry != null) {
                ResourceLocation id = entry.getSkinLocation();
                if (!id.equals(cached)) {
                    SKINS.put(uuid, id);
                    SLIM.put(uuid, "slim".equals(entry.getModelName()));
                }
                return id;
            }
        }

        if (cached != null) {
            return cached;
        }

        requestSkin(uuid, name);
        return DefaultPlayerSkin.getDefaultSkin(uuid);
    }

    public static ResourceLocation getCapeTexture(UUID uuid) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) {
            PlayerInfo entry = mc.getConnection().getPlayerInfo(uuid);
            if (entry != null) {
                ResourceLocation id = entry.getCapeLocation();
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
        SkullBlockEntity.updateGameprofile(profile, filled ->
                Minecraft.getInstance().getSkinManager().registerSkins(filled, (type, id, texture) -> {
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        SKINS.put(uuid, id);
                        SLIM.put(uuid, texture != null && "slim".equals(texture.getMetadata("model")));
                    } else if (type == MinecraftProfileTexture.Type.CAPE) {
                        CAPES.put(uuid, id);
                    }
                }, false));
    }
}
