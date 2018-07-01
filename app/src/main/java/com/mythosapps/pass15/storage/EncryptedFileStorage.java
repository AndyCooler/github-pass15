package com.mythosapps.pass15.storage;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.ConfigXmlParser;
import com.mythosapps.pass15.util.EncryptionUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreas on 31.05.17.
 * <p>
 * Encrypted file storage.
 */

public class EncryptedFileStorage extends FileStorage implements ConfigStorageFacade {

    private static final String DEFAULT_CONFIG_FILE = "Pass15_sec.conf";
    private static final String UNLOCK_CODE_CONFIG_FILE = "Pass15_sec.lock";

    private ConfigXmlParser parser = new ConfigXmlParser();

    private Activity activity;

    private ConfigStorageFacade plaintextStorage = StorageFactory.getConfigStorage();

    public EncryptedFileStorage() {
    }

    public String loadUnlockCode(Activity activity) {
        this.activity = activity;

        String loadedUnlockCode = null;

        if (!verifyStoragePermissions(activity)) {
            fatal("loadUnlockCode", "Grant permissions to access unlock file.");
            return loadedUnlockCode;
        }

        String filename = UNLOCK_CODE_CONFIG_FILE;

        if (!initialized && !init()) {
            fatal("loadUnlockCode", "Error loading file " + filename);
            return loadedUnlockCode;
        }

        File file = new File(storageDir, filename);
        if (!file.exists()) {
            Log.w(getClass().getName(), "loadUnlockCode : file not found " + filename);
            return loadedUnlockCode;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            loadedUnlockCode = EncryptionUtil.readAndDecrypt(fis);

            if (loadedUnlockCode == null || loadedUnlockCode.length() != 4) {
                Log.w(getClass().getName(), "loadUnlockCode : unlock code invalid in " + filename);
                return null;
            }
        } catch (Throwable e) {
            fatal("loadUnlockCode", "Error loading unlock code from file " + filename + " " + e.getMessage());
            Log.e(getClass().getName(), "Error loading unlock code from file " + filename, e);
        }
        Log.i(getClass().getName(), "Loaded unlock code from EncryptedFileStorage.");

        return loadedUnlockCode;
    }

    @Override
    public boolean saveUnlockCode(String selectedUnlockCode) {
        String filename = UNLOCK_CODE_CONFIG_FILE;

        this.activity = activity;

        String loadedUnlockCode = null;

        if (!verifyStoragePermissions(activity)) {
            fatal("saveUnlockCode", "Grant permissions to access unlock file.");
            return false;
        }

        if (!initialized && !init()) {
            return false;
        }

        boolean result = false;
        File file = new File(storageDir, filename);
        if (selectedUnlockCode == null) {
            if (file.delete()) {
                Log.i(getClass().getName(), "Deleted file " + filename + " for code " + selectedUnlockCode);
            } else {
                fatal("saveUnlockCode", "Error deleting file " + filename + " for code " + selectedUnlockCode);
                Log.e(getClass().getName(), "Error deleting file " + filename + " for code " + selectedUnlockCode);
            }
        } else {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file, false);

                EncryptionUtil.writeAndEncrypt(fos, selectedUnlockCode);

                Log.i(getClass().getName(), "Saved file " + filename + " for code " + selectedUnlockCode);
                result = true;
            } catch (Exception e) {
                fatal("saveUnlockCode", "Error saving unlock code to file " + filename);
                Log.e(getClass().getName(), "Error saving unlock code to file " + filename + " as " + file.getAbsolutePath(), e);
            }
        }

        if (result == true) {
            boolean migrationSuccess = plaintextStorage.saveUnlockCode(null);
            Log.i(getClass().getName(), "Migration: delete unencrpyted unlock code file:" + migrationSuccess);
        }
        return result;
    }

    @Override
    public boolean exportConfigXml(Activity passwordsActivity, List<PasswordEntry> list, String filename) {
        return false; // TODO move export method from config storage to here
    }

    /**
     * Load config from external XML file {@link #DEFAULT_CONFIG_FILE} if present. Loaded
     * tasks are activated as a side effect. In addition, returns a list of tasks from app storage
     * (asset storage) that can be activated by the caller. This way, the loaded tasks are always
     * first and override tasks from asset storage.
     *
     * @param activity
     * @return config loaded from external XML blended with asset config, or in
     * case of error, only asset config
     */
    public List<PasswordEntry> loadConfigXml(Activity activity) {

        this.activity = activity;

        List<PasswordEntry> loadedConfig = new ArrayList<PasswordEntry>();

        if (!verifyStoragePermissions(activity)) {
            fatal("loadConfigXml", "Grant permissions to access file.");
            return loadedConfig;
        }

        String filename = DEFAULT_CONFIG_FILE;

        if (!initialized && !init()) {
            fatal("loadConfigXml", "Error loading file " + filename);
            return loadedConfig;
        }

        File file = new File(storageDir, filename);
        if (!file.exists()) {
            Log.w(getClass().getName(), "loadConfigXml : file not found " + filename);
            return loadedConfig;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            String encryptedContent = EncryptionUtil.readAndDecrypt(fis);
            ByteArrayInputStream bis = new ByteArrayInputStream(encryptedContent.getBytes(Charset.forName("UTF-8")));
            loadedConfig = parser.parse(bis);
        } catch (Throwable e) {
            fatal("loadConfigXml", "Error loading config from file " + filename + " " + e.getMessage());
            Log.e(getClass().getName(), "Error loading config from file " + filename, e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        Log.i(getClass().getName(), "Loaded " + loadedConfig.size() + " entries from EncryptedFileStorage.");

        return loadedConfig;
    }

    @Override
    public boolean saveExternalConfigXml(Activity activity, List<PasswordEntry> tasks) {
        String filename = DEFAULT_CONFIG_FILE;

        this.activity = activity;

        if (!verifyStoragePermissions(activity)) {
            fatal("saveExternalConfigXml", "Grant permissions to access file.");
            return false;
        }

        if (!initialized && !init()) {
            return false;
        }

        File file = new File(storageDir, filename);
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fos = new FileOutputStream(file, false);

            String content = XML_PROLOG;
            for (PasswordEntry task : tasks) {
                content += task.toXmlConfig();
            }
            content += XML_END;
            EncryptionUtil.writeAndEncrypt(fos, content);
            fos.close();

            Log.i(getClass().getName(), "Saved file " + filename);
            result = true;
        } catch (Exception e) {
            fatal("saveExternalConfigXml", "Error saving file " + filename);
            Log.e(getClass().getName(), "Error saving file " + filename + " as " + file.getAbsolutePath(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        if (result == true) {
            // store empty list in unencrypted file so that when loading we know the real entries are in the encrypted file
            boolean migrationSuccess = plaintextStorage.saveExternalConfigXml(activity, new ArrayList<PasswordEntry>());
            Log.i(getClass().getName(), "Migration: make empty the unencrpyted entries file:" + migrationSuccess);
        }

        return result;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {

        boolean permissionsGranted = false;
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted = true;
        } else {
            // Prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        return permissionsGranted;
    }

    private void fatal(String method, String msg) {
        Log.e(getClass().getName(), method + " : " + msg);

        if (activity != null) {
            Toast.makeText(activity.getApplicationContext(), method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }
}