package github.com.CraftKernel.publicskintotems;

import net.minecraft.nbt.CompoundTag;
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

    public static boolean isClaimed(ItemStack stack) {
        if (!isTotem(stack)) {
            return false;
        }
        CompoundTag nbt = stack.getTagElement(KEY);
        return nbt != null && nbt.hasUUID(OWNER_UUID);
    }

    public static boolean isUnclaimedTotem(ItemStack stack) {
        return isTotem(stack) && !isClaimed(stack);
    }

    public static void claim(ItemStack stack, UUID uuid, String name) {
        CompoundTag nbt = stack.getOrCreateTagElement(KEY);
        nbt.putUUID(OWNER_UUID, uuid);
        nbt.putString(OWNER_NAME, name == null ? "" : name);
    }

    public static void unclaim(ItemStack stack) {
        stack.removeTagKey(KEY);
    }

    public static UUID getOwner(ItemStack stack) {
        CompoundTag nbt = stack.getTagElement(KEY);
        return nbt != null && nbt.hasUUID(OWNER_UUID) ? nbt.getUUID(OWNER_UUID) : null;
    }

    public static String getOwnerName(ItemStack stack) {
        CompoundTag nbt = stack.getTagElement(KEY);
        return nbt == null ? "" : nbt.getString(OWNER_NAME);
    }
}
