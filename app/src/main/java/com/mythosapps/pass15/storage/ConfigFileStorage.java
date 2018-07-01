package com.mythosapps.pass15.storage;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.ConfigXmlParser;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * File store, plain xml.
 */

public class ConfigFileStorage extends FileStorage implements ConfigStorageFacade {

    private static final String DEFAULT_CONFIG_FILE = "Pass15.conf";
    private static final String UNLOCK_CODE_CONFIG_FILE = "Pass15.lock";

    private ConfigXmlParser parser = new ConfigXmlParser();

    EncryptedFileStorage encryptedParallel = new EncryptedFileStorage();

    private Activity activity;

    public ConfigFileStorage() {
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
            loadedUnlockCode = IOUtils.toString(fis);
            if (loadedUnlockCode == null || loadedUnlockCode.length() != 4) {
                Log.w(getClass().getName(), "loadUnlockCode : unlock code invalid in " + filename);
                return null;
            }

        } catch (Throwable e) {
            fatal("loadUnlockCode", "Error loading unlock code from file " + filename + " " + e.getMessage());
            Log.e(getClass().getName(), "Error loading unlock code from file " + filename, e);
        }
        Log.i(getClass().getName(), "Loaded unlock code from ConfigFileStorage.");

        String decrypted = encryptedParallel.loadUnlockCode(activity);
        if ((loadedUnlockCode == null && decrypted == null) || (loadedUnlockCode.equals(decrypted))) {
            Log.i(getClass().getName(), "loadUnlockCode : same as encrypted, all ok.");
            return decrypted;
        } else {
            Log.e(getClass().getName(), "loadUnlockCode : not equal to encrypted, error!.");
            // migration from 1.0:
            boolean saveSuccess = false;
            if (loadedUnlockCode != null) {
                saveSuccess = encryptedParallel.saveUnlockCode(loadedUnlockCode);
            }
            fatal("decryption", "Migrate to encrypted unlock code:" + saveSuccess);
            return loadedUnlockCode;
        }
    }

    @Override
    public boolean saveUnlockCode(String selectedUnlockCode) {
        String filename = UNLOCK_CODE_CONFIG_FILE;

        this.activity = activity;

        verifyStoragePermissions(activity);

        if (!initialized && !init()) {
            return false;
        }

        File file = new File(storageDir, filename);
        boolean result = false;
        String toWrite = selectedUnlockCode == null ? "" : selectedUnlockCode;
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            IOUtils.write(toWrite, fos);
            fos.flush();
            fos.close();
            Log.i(getClass().getName(), "Saved file " + filename + " for code " + selectedUnlockCode);
            result = true;
        } catch (IOException e) {
            fatal("saveUnlockCode", "Error saving unlock code to file " + filename);
            Log.e(getClass().getName(), "Error saving unlock code to file " + filename + " as " + file.getAbsolutePath(), e);
        }
        if (result == true) {
            encryptedParallel.saveUnlockCode(selectedUnlockCode);
        }
        return result;
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

        verifyStoragePermissions(activity);

        List<PasswordEntry> loadedConfig = new ArrayList<PasswordEntry>();

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

            loadedConfig = parser.parse(fis);
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
        Log.i(getClass().getName(), "Loaded " + loadedConfig.size() + " entries from ConfigFileStorage.");

        List<PasswordEntry> decrypted = encryptedParallel.loadConfigXml(activity);
        if (loadedConfig.size() == decrypted.size()) {
            Log.i(getClass().getName(), "loadConfigXml : same as encrypted, all ok.");
            return decrypted;
        } else {
            Log.e(getClass().getName(), "loadConfigXml : not equal to encrypted, error!.");
            // migration from 1.0:
            boolean saveSuccess = false;
            if (loadedConfig != null) {
                saveSuccess = encryptedParallel.saveExternalConfigXml(activity, loadedConfig);
            }
            fatal("decryption", "Migrate to encrypted content:" + saveSuccess);
            return loadedConfig;
        }
    }

    @Override
    public boolean saveExternalConfigXml(Activity activity, List<PasswordEntry> tasks) {
        String filename = DEFAULT_CONFIG_FILE;

        this.activity = activity;

        if (!initialized && !init()) {
            return false;
        }

        File file = new File(storageDir, filename);
        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file, false);

            PrintWriter pw = new PrintWriter(fos);
            pw.println(XML_PROLOG);
            for (PasswordEntry task : tasks) {
                pw.println(task.toXmlConfig());
            }
            pw.println(XML_END);
            pw.flush();
            pw.close();
            fos.close();

            Log.i(getClass().getName(), "Saved file " + filename);
            result = true;
        } catch (IOException e) {
            fatal("saveExternalConfigXml", "Error saving file " + filename);
            Log.e(getClass().getName(), "Error saving file " + filename + " as " + file.getAbsolutePath(), e);
        }

        if (result == true) {
            encryptedParallel.saveExternalConfigXml(activity, tasks);
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