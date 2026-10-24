package github.com.CraftKernel.publicskintotems.api;

import java.util.UUID;

public final class SkinTotemProtocol {
    public static final String KEY = "PublicSkinTotem";

    public static final String OWNER_UUID = "OwnerUUID";

    public static final String OWNER_NAME = "OwnerName";

    public static final String TOTEM_ITEM_ID = "minecraft:totem_of_undying";

    private SkinTotemProtocol() {
    }

    public static int[] uuidToInts(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        return new int[] {(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

    public static UUID intsToUuid(int[] ints) {
        if (ints == null || ints.length != 4) {
            throw new IllegalArgumentException("UUID int-array must have length 4");
        }
        long most = ((long) ints[0] << 32) | (ints[1] & 0xFFFFFFFFL);
        long least = ((long) ints[2] << 32) | (ints[3] & 0xFFFFFFFFL);
        return new UUID(most, least);
    }
}
