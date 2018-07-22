package com.mythosapps.pass15.types;


import com.mythosapps.pass15.util.ConfigXmlParser;

import java.util.List;

/**
 * Describes what kind of day is stored in a DaysData.
 */
public class PasswordEntry {

    private String category;

    private String name;

    private String username;

    private String password;

    private String created;

    private String lastModified;

    public PasswordEntry(String category, String name, String username, String password, String created, String lastModified) {
        this.category = category;
        this.name = name;
        this.username = username;
        this.password = password;
        this.created = created;
        this.lastModified = lastModified;
    }

    public static boolean replaceByNameCat(List<PasswordEntry> list, PasswordEntry newEntry, String oldName, String oldCategory) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCategory().equals(oldCategory) && list.get(i).getName().equals(oldName)) {
                index = i;
            }
        }
        if (index >= 0) {
            list.remove(index);
            list.add(index, newEntry);
            return true;
        }
        return false;
    }


    public static boolean deleteByIndex(List<PasswordEntry> list, int listIndex) {

        if (listIndex >= 0) {
            list.remove(listIndex);
            return true;
        }
        return false;
    }


    public static void addEntryToCategory(List<PasswordEntry> list, PasswordEntry entry) {
        String category = entry.getCategory();
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCategory().equals(category)) {
                index = i;
            }
        }
        if (index >=0) {
            list.add(index+1, entry);
        } else {
            list.add(entry);
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }


    @Override
    public String toString() {
        return "Text:" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof PasswordEntry) {
            PasswordEntry other = (PasswordEntry) o;
            return other.name.equals(name) &&
                    other.category.equals(category);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String toXmlConfig() {
        return "    <entry>\n" +
                "        <category>" + ConfigXmlParser.escapeTextValue(getCategory()) + "</category>\n" +
                "        <name>" + ConfigXmlParser.escapeTextValue(getName()) + "</name>\n" +
                "        <username>" + ConfigXmlParser.escapeTextValue(getUsername()) + "</username>\n" +
                "        <password>" + ConfigXmlParser.escapeTextValue(getPassword()) + "</password>\n" +
                "        <created>" + ConfigXmlParser.escapeTextValue(getCreated()) + "</created>\n" +
                "        <lastModified>" + ConfigXmlParser.escapeTextValue(getLastModified()) + "</lastModified>\n" +
                "    </entry>\n";
    }

    public boolean isEmpty() {
        return isEmptyAttribute(name) && isEmptyAttribute(category) && isEmptyAttribute(username) && isEmptyAttribute(password);
    }
    private boolean isEmptyAttribute(String attr) {
        return attr == null || "".equals(attr);
    }
}

