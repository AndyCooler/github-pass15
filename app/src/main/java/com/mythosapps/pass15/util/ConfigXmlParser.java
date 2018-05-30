package com.mythosapps.pass15.util;


import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.mythosapps.pass15.types.PasswordEntry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by andreas on 09.02.17.
 */
public class ConfigXmlParser {

    // XML
    // <config>
    //   <entry>
    //     <category>
    //     <name>
    //     <username>
    //     <password>
    //     <created>
    //     <lastModified>

    private static final String XML_ENTRY = "entry";
    private static final String XML_CATEGORY = "category";
    private static final String XML_NAME = "name";
    private static final String XML_USERNAME = "username";
    private static final String XML_PASSWORD = "password";
    private static final String XML_CREATED = "created";
    private static final String XML_LASTMODIFIED = "lastModified";

    public List<PasswordEntry> parse(InputStream fis) {
        Document doc = getDocument(fis);

        List<PasswordEntry> result = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName(XML_ENTRY);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            String category = getValue(e, XML_CATEGORY);
            String name = getValue(e, XML_NAME);
            String username = getValue(e, XML_USERNAME);
            String password = getValue(e, XML_PASSWORD);
            String created = getValue(e, XML_CREATED);
            String lastModified = getValue(e, XML_LASTMODIFIED);

            PasswordEntry task = new PasswordEntry(category, name, username, password, created, lastModified);
            result.add(task);
        }

        Log.i("Info:", "Parsed " + result.size() + " elements.");

        return result;
    }

    public Document getDocument(InputStream inputStream) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(inputStream);
            document = db.parse(inputSource);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return document;
    }

    public Document getDocumentFromResource(String resourceFileName, Activity activity) {

        AssetManager manager = activity.getAssets();
        InputStream stream;
        Document doc = null;

        try {
            stream = manager.open(resourceFileName);
            doc = getDocument(stream);
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
        } finally {
            manager.close();
        }
        return doc;
    }

    private String getValue(Element item, String name) {
        NodeList nodes = item.getElementsByTagName(name);
        return this.getTextNodeValue(nodes.item(0));
    }

    private String getTextNodeValue(Node node) {
        Node child;
        if (node != null) {
            if (node.hasChildNodes()) {
                child = node.getFirstChild();
                while (child != null) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        String value = child.getNodeValue();
                        return value.trim();
                    }
                    child = child.getNextSibling();
                }
            }
        }
        return "";
    }
}
