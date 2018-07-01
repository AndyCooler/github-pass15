package com.mythosapps.pass15.storage;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.ConfigXmlParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads config from file within the app ("asset").
 */
public class ConfigAssetStorage implements ConfigStorageFacade {

    private static final String DEFAULT_ASSET_FILE = "Pass15.conf";

    private final ConfigXmlParser parser;

    private boolean saveActionProcessed = false;

    private String saveActionXml = "";

    private String unlockCode = "1111"; //null; //"1111"; // null um zu testen wie die app startet wenn der unlock code scon gesetzt wurde

    public ConfigAssetStorage(ConfigXmlParser parser) {
        this.parser = parser;
    }

    public List<PasswordEntry> loadConfigXml(Activity activity) {

        String resourceFileName = DEFAULT_ASSET_FILE;
        List<PasswordEntry> result = new ArrayList<>();

        try {
            InputStream stream = null;
            if (saveActionProcessed) {
                stream = new ByteArrayInputStream(saveActionXml.getBytes());
                Log.i(getClass().getName(), "Loading from cached XML.");
                result = parser.parse(stream);
            } else {
                Log.i(getClass().getName(), "Loading from AssetStorage.");
                AssetManager manager = activity.getAssets();
                stream = manager.open(resourceFileName);
                result = parser.parse(stream);
                // save/load again to ensure same order at initial load as when after a save
                saveExternalConfigXml(activity, result);
                stream = new ByteArrayInputStream(saveActionXml.getBytes());
                result = parser.parse(stream);
            }

        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
        }
        Log.i(getClass().getName(), "Loaded " + result.size() + " entries from AssetStorage.");
        return result;
    }

    @Override
    public boolean saveExternalConfigXml(Activity activity, List<PasswordEntry> tasks) {

        saveActionProcessed = true;

        boolean result = false;
        try {
            StringBuilder xml = new StringBuilder(XML_PROLOG);

            for (PasswordEntry task : tasks) {
                xml.append(task.toXmlConfig());
            }
            xml.append(XML_END);

            saveActionXml = xml.toString();
            Log.i(getClass().getName(), "Saved XML : \n" + xml);
            result = true;
        } catch (Throwable e) {
            Log.e(getClass().getName(), "Error saving XML ", e);
        }
        return result;
    }

    @Override
    public String loadUnlockCode(Activity activity) {
        return unlockCode;
    }

    @Override
    public boolean saveUnlockCode(String selectedUnlockCode) {
        unlockCode = selectedUnlockCode;
        return true;
    }

    @Override
    public boolean exportConfigXml(Activity passwordsActivity, List<PasswordEntry> list, String filename) {
        return false;
    }
}
