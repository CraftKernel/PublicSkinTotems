package github.com.CraftKernel.publicskintotems;

import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public final class SkinTotemData {
    public static final String KEY = "PublicSkinTotem";

    private static final String OWNER_UUID = "OwnerUUID";
    private static final String OWNER_NAME = "OwnerName";

    private SkinTotemData() {
    }

    public static boolean isTotem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.isOf(Items.TOTEM_OF_UNDYING);
    }

    public static boolean isClaimed(ItemStack stack) {
        if (!isTotem(stack)) {
            return false;
        }
        NbtCompound nbt = stack.getSubNbt(KEY);
        return nbt != null && nbt.containsUuid(OWNER_UUID);
    }

    public static boolean isUnclaimedTotem(ItemStack stack) {
        return isTotem(stack) && !isClaimed(stack);
    }

    public static void claim(ItemStack stack, UUID uuid, String name) {
        NbtCompound nbt = stack.getOrCreateSubNbt(KEY);
        nbt.putUuid(OWNER_UUID, uuid);
        nbt.putString(OWNER_NAME, name == null ? "" : name);
    }

    public static void unclaim(ItemStack stack) {
        stack.removeSubNbt(KEY);
    }

    public static UUID getOwner(ItemStack stack) {
        NbtCompound nbt = stack.getSubNbt(KEY);
        if (nbt == null || !nbt.containsUuid(OWNER_UUID)) {
            return null;
        }
        return nbt.getUuid(OWNER_UUID);
    }

    public static String getOwnerName(ItemStack stack) {
        NbtCompound nbt = stack.getSubNbt(KEY);
        return nbt == null ? "" : nbt.getString(OWNER_NAME);
    }
}
