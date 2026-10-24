package github.com.CraftKernel.publicskintotems;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class SkinTotemData {
    public static final String KEY = "PublicSkinTotem";

    private static final String OWNER_UUID = "OwnerUUID";
    private static final String OWNER_NAME = "OwnerName";

    private SkinTotemData() {
    }

    public static boolean isTotem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(Items.TOTEM_OF_UNDYING);
    }

    private static CompoundTag data(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) {
            return null;
        }
        CompoundTag tag = custom.getUnsafe();
        return tag.contains(KEY, Tag.TAG_COMPOUND) ? tag.getCompound(KEY) : null;
    }

    public static boolean isClaimed(ItemStack stack) {
        return isTotem(stack) && getOwner(stack) != null;
    }

    public static boolean isUnclaimedTotem(ItemStack stack) {
        return isTotem(stack) && !isClaimed(stack);
    }

    public static void claim(ItemStack stack, UUID uuid, String name) {
        CompoundTag owner = new CompoundTag();
        owner.putIntArray(OWNER_UUID, UUIDUtil.uuidToIntArray(uuid));
        owner.putString(OWNER_NAME, name == null ? "" : name);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(KEY, owner));
    }

    public static UUID getOwner(ItemStack stack) {
        CompoundTag nbt = data(stack);
        if (nbt == null) {
            return null;
        }
        if (!nbt.contains(OWNER_UUID, Tag.TAG_INT_ARRAY)) {
            return null;
        }
        int[] raw = nbt.getIntArray(OWNER_UUID);
        return raw.length == 4 ? UUIDUtil.uuidFromIntArray(raw) : null;
    }

    public static String getOwnerName(ItemStack stack) {
        CompoundTag nbt = data(stack);
        return nbt == null ? "" : nbt.getString(OWNER_NAME);
    }

    public static void unclaim(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(KEY));
    }

    public static String playerName(ServerPlayer player) {
        return player.getGameProfile().getName();
    }
}
