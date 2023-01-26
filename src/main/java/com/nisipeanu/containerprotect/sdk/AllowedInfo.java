package com.nisipeanu.containerprotect.sdk;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class AllowedInfo {
    String identifier;
    String prefix;
    public Class<? extends ContainerAllowed> handler;

    protected AllowedInfo(String identifier, String prefix, Class<? extends ContainerAllowed> handler) {
        this.identifier = identifier;
        this.prefix = prefix;
        this.handler = handler;
    }

    public byte[] serializeValue(String value) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var handler = this.handler.getDeclaredConstructor().newInstance();

        // Let the third party implementation process and serialize the string value (they may want to map a name to an id and serialize the id)
        handler.setValue(value);

        byte[] serializedValue = handler.serialize();
        byte[] serializedKey = this.identifier.getBytes(StandardCharsets.UTF_8);

        // 2 ints to store: identifier length, value length
        ByteBuffer buffer = ByteBuffer.allocate(2 * 4 + serializedKey.length + serializedValue.length);

        // identifier
        buffer.putInt(serializedKey.length);
        buffer.put(serializedKey);

        // value
        buffer.putInt(serializedValue.length);
        buffer.put(serializedValue);

        return buffer.array();
    }

    public ContainerAllowed deserialize(byte[] value) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var handler = this.handler.getDeclaredConstructor().newInstance();

        ByteBuffer buffer = ByteBuffer.wrap(value);

        // calculate offset (identifier end)
        int identifierLength = buffer.getInt();
        byte[] identifier = new byte[identifierLength];
        buffer.get(identifier);

        // get value
        int valueLength = buffer.getInt();
        byte[] containedValue = new byte[valueLength];
        buffer.get(containedValue);

        handler.deserialize(containedValue);

        return handler;
    }
}
