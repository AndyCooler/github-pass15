package com.mythosapps.pass15.storage;

import android.os.Build;

import com.mythosapps.pass15.util.ConfigXmlParser;

/**
 * Created by andreas on 14.02.16.
 */
public class StorageFactory {

    private static ConfigStorageFacade INSTANCE_CONFIG_STORE = null;

    private static ConfigStorageFacade INSTANCE_ENCRYPTED_CONFIG_STORE = null;

    public static ConfigStorageFacade getConfigStorage() {
        if (INSTANCE_CONFIG_STORE == null) {
            INSTANCE_CONFIG_STORE = createConfigStorage();
        }
        return INSTANCE_CONFIG_STORE;
    }

    public static ConfigStorageFacade getEncryptedStorage() {
        if (INSTANCE_ENCRYPTED_CONFIG_STORE == null) {
            INSTANCE_ENCRYPTED_CONFIG_STORE = createEncryptedStorage();
        }
        return INSTANCE_ENCRYPTED_CONFIG_STORE;
    }

    private static ConfigStorageFacade createConfigStorage() {
        if (Build.FINGERPRINT.contains("generic")) {
            ConfigAssetStorage storage = new ConfigAssetStorage(new ConfigXmlParser()); // running on emulator
            return storage;
        }
        return new ConfigFileStorage();
    }

    private static ConfigStorageFacade createEncryptedStorage() {
        if (Build.FINGERPRINT.contains("generic")) {
            ConfigAssetStorage storage = new ConfigAssetStorage(new ConfigXmlParser()); // running on emulator
            return storage;
        }
        return new EncryptedFileStorage();
    }
}
