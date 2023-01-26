package com.nisipeanu.containerprotect.sdk;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ContainerAllowedManagerImplementation implements ContainerAllowedManager {
    private static ArrayList<AllowedInfo> implementations = new ArrayList<>();

    public void register(String uniqueName, String prefix, Class<? extends ContainerAllowed> handler) {
        implementations.add(new AllowedInfo(uniqueName, prefix, handler));
    }
    
    public @Nullable AllowedInfo getImplementationByIdentifier(String identifier) {
        for (var implementation : implementations) {
            if (implementation.identifier.equals(identifier)) return implementation;
        }

        return null;
    }
    
    public @Nullable AllowedInfo getImplementationByPrefix(String prefix) {
        for (var implementation : implementations) {
            if (implementation.prefix.equals(prefix)) return implementation;
        }

        return null;
    }

    public @Nullable AllowedInfo getImplementationByValue(byte[] value) {
        ByteBuffer buffer = ByteBuffer.wrap(value);

        int identifierLength = buffer.getInt();
        byte[] identifierBytes = new byte[identifierLength];
        buffer.get(identifierBytes);

        return this.getImplementationByIdentifier(new String(identifierBytes, StandardCharsets.UTF_8));
    }

}
