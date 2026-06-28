package github.com.Endyboii.publicskintotems;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

/**
 * Helpers for the owner data we stamp onto a Totem of Undying.
 *
 * The item is left as a real {@link Items#TOTEM_OF_UNDYING}, so the vanilla
 * cheat-death behaviour keeps working for everyone no matter whose skin is on it.
 * We only attach a small NBT sub-compound holding the owner's UUID and name.
 */
public final class SkinTotemData {

    /** Sub-NBT key on the totem stack. */
    public static final String KEY = "PublicSkinTotem";

    private static final String OWNER_UUID = "OwnerUUID";
    private static final String OWNER_NAME = "OwnerName";

    private SkinTotemData() {
    }

    /** True if the stack is a Totem of Undying (claimed or not). */
    public static boolean isTotem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.isOf(Items.TOTEM_OF_UNDYING);
    }

    /** True if this totem has been claimed by a player. */
    public static boolean isClaimed(ItemStack stack) {
        if (!isTotem(stack)) {
            return false;
        }
        NbtCompound nbt = stack.getSubNbt(KEY);
        return nbt != null && nbt.containsUuid(OWNER_UUID);
    }

    /** True if this is a plain, unclaimed totem that should be auto-claimed. */
    public static boolean isUnclaimedTotem(ItemStack stack) {
        return isTotem(stack) && !isClaimed(stack);
    }

    /** Lock this totem to the given player. */
    public static void claim(ItemStack stack, UUID uuid, String name) {
        NbtCompound nbt = stack.getOrCreateSubNbt(KEY);
        nbt.putUuid(OWNER_UUID, uuid);
        nbt.putString(OWNER_NAME, name == null ? "" : name);
    }

    /** Owner UUID, or null if unclaimed. */
    public static UUID getOwner(ItemStack stack) {
        NbtCompound nbt = stack.getSubNbt(KEY);
        if (nbt == null || !nbt.containsUuid(OWNER_UUID)) {
            return null;
        }
        return nbt.getUuid(OWNER_UUID);
    }

    /** Owner name (may be empty), used to look skins up when the owner is offline. */
    public static String getOwnerName(ItemStack stack) {
        NbtCompound nbt = stack.getSubNbt(KEY);
        return nbt == null ? "" : nbt.getString(OWNER_NAME);
    }
}
