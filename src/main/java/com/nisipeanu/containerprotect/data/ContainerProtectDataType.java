package com.nisipeanu.containerprotect.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class ContainerProtectDataType implements PersistentDataType<byte[], ContainerProtectData> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ContainerProtectData> getComplexType() {
        return ContainerProtectData.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull ContainerProtectData chestProtectData,
                                        @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        // (AllowedList + Owner) * 2 * sizeof(long) + (2 * sizeof(int))
        ByteBuffer bb = ByteBuffer.allocate(
                (chestProtectData.allowedList.size() + 1) * 16 + (2 * 4) // Owner + allowed list
                + (chestProtectData.additionalAllowed.size() + 1) * 4 // Lengths for each additionalAllowed
                + chestProtectData.additionalAllowed.stream().mapToInt(a -> a.length).sum()
        );
        bb.putInt(chestProtectData.protectionType.ordinal());

        bb.putLong(chestProtectData.owner.getUniqueId().getMostSignificantBits());
        bb.putLong(chestProtectData.owner.getUniqueId().getLeastSignificantBits());

        // We put the size of allowedList in the byte array just in case we are gonna add other things
        // later. This way we know where to stop reading the list and other data is coming.
        bb.putInt(chestProtectData.allowedList.size());

        for (var allowed : chestProtectData.allowedList) {
            bb.putLong(allowed.getUniqueId().getMostSignificantBits());
            bb.putLong(allowed.getUniqueId().getLeastSignificantBits());
        }

        bb.putInt(chestProtectData.additionalAllowed.size());

        for (var allowed : chestProtectData.additionalAllowed) {
            bb.putInt(allowed.length);
            bb.put(allowed);
        }

        return bb.array();
    }

    @Override
    public @NotNull ContainerProtectData fromPrimitive(byte @NotNull [] bytes,
                                                       @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        UUID owner;
        ArrayList<UUID> allowedList = new ArrayList<>();
        ArrayList<byte[]> additionalAllowedList = new ArrayList<>();

        int protectionType = bb.getInt();

        long mostBits = bb.getLong();
        long leastBits = bb.getLong();
        owner = new UUID(mostBits, leastBits);

        int allowedListSize = bb.getInt();

        for (int i = 0; i < allowedListSize; i++) {
            if (bb.remaining() < 16) break;

            mostBits = bb.getLong();
            leastBits = bb.getLong();
            allowedList.add(new UUID(mostBits, leastBits));
        }

        int additionalAllowedSize = 0;

        try {
            additionalAllowedSize = bb.getInt();
        } catch (BufferUnderflowException ignored) {
            // Protection created before adding additionalAllowed param
        }

        for (int i = 0; i < additionalAllowedSize; i++) {
            if (bb.remaining() < 4) break;
            int length = bb.getInt();

            if (bb.remaining() < length) break;
            byte[] value = new byte[length];
            bb.get(value);

            additionalAllowedList.add(value);
        }

        return new ContainerProtectData(owner, allowedList, ProtectionType.values()[protectionType], additionalAllowedList);
    }

}
