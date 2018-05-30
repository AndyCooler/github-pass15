package com.mythosapps.pass15.storage;

import android.app.Activity;

import com.mythosapps.pass15.types.PasswordEntry;

import java.util.List;

/**
 * Created by andreas on 10.02.17.
 */

public interface ConfigStorageFacade {

    String XML_PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<config>\n";
    String XML_END = "</config>\n";

    // TODO saveUnlockCode..

    public String loadUnlockCode(Activity activity);

    List<PasswordEntry> loadConfigXml(Activity activity);

    boolean saveExternalConfigXml(Activity activity, List<PasswordEntry> tasks);

    boolean saveUnlockCode(String selectedUnlockCode);
}
