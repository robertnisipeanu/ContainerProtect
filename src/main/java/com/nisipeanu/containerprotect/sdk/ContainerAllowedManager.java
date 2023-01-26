package com.nisipeanu.containerprotect.sdk;

public interface ContainerAllowedManager {

    void register(String uniqueName, String prefix, Class<? extends ContainerAllowed> handler);

}
