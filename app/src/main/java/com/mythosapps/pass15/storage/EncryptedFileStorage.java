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

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.ByteArrayInputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by andreas on 31.05.17.
 *
 * Encrypted file storage.
 */

public class EncryptedFileStorage extends FileStorage implements ConfigStorageFacade {

    private static final String DEFAULT_CONFIG_FILE = "Pass15_sec.conf";
    private static final String UNLOCK_CODE_CONFIG_FILE = "Pass15_sec.lock";

    private ConfigXmlParser parser = new ConfigXmlParser();

    private Activity activity;

    public EncryptedFileStorage() {
    }

    public String loadUnlockCode(Activity activity) {
        this.activity = activity;

        verifyStoragePermissions(activity);

        String loadedUnlockCode = null;

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
        Log.i(getClass().getName(), "Loaded unlock code from ConfigFileStorage.");

        return loadedUnlockCode;
    }

    @Override
    public boolean saveUnlockCode(String selectedUnlockCode) {
        String filename = UNLOCK_CODE_CONFIG_FILE;

        this.activity = activity;

        verifyStoragePermissions(activity);

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

        return loadedConfig;
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
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



    private void fatal(String method, String msg) {
        Log.e(getClass().getName(), method + " : " + msg);

        if (activity != null) {
            Toast.makeText(activity.getApplicationContext(), method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }
}