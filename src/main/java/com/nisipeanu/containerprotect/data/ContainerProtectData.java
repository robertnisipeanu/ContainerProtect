package com.nisipeanu.containerprotect.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.UUID;

public class ContainerProtectData {

    public ContainerProtectData() {
    }

    public ContainerProtectData(UUID owner) {
        this.owner = Bukkit.getOfflinePlayer(owner);
    }

    public ContainerProtectData(UUID owner, ArrayList<UUID> allowedList) {
        this.owner = Bukkit.getOfflinePlayer(owner);

        allowedList.forEach(allowed -> this.allowedList.add(Bukkit.getOfflinePlayer(allowed)));
    }

    public ContainerProtectData(UUID owner, ArrayList<UUID> allowedList, ProtectionType type) {
        this(owner, allowedList);

        this.protectionType = type;
    }

    public ContainerProtectData(UUID owner, ArrayList<UUID> allowedList, ProtectionType type, ArrayList<byte[]> additionalAllowed) {
        this(owner, allowedList, type);

        this.additionalAllowed = additionalAllowed;
    }

    public OfflinePlayer owner = null;

    public ArrayList<OfflinePlayer> allowedList = new ArrayList<>();

    public ProtectionType protectionType = ProtectionType.NONE;

    public ArrayList<byte[]> additionalAllowed = new ArrayList<>();

}
