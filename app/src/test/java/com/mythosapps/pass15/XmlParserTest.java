package com.mythosapps.pass15;

import com.mythosapps.pass15.storage.ConfigStorageFacade;
import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.ConfigXmlParser;
import com.mythosapps.pass15.util.EncryptionUtil;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for special characters.
 */
public class XmlParserTest {

    @Test
    public void testNormalValue() throws Exception {
        String expected = "1111";
        PasswordEntry entry = new PasswordEntry(expected, expected, expected, expected, expected, expected);

        String xml = ConfigStorageFacade.XML_PROLOG + entry.toXmlConfig() +
                ConfigStorageFacade.XML_END;

        ConfigXmlParser parser = new ConfigXmlParser();
        List<PasswordEntry> list = parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        String actual = list.get(0).getPassword();

        assertEquals(expected, actual);
    }

    @Test
    public void testAmpersandValue() throws Exception {
        String expected = "1111&";
        PasswordEntry entry = new PasswordEntry(expected, expected, expected, expected, expected, expected);

        String xml = ConfigStorageFacade.XML_PROLOG + entry.toXmlConfig() +
                ConfigStorageFacade.XML_END;

        ConfigXmlParser parser = new ConfigXmlParser();
        List<PasswordEntry> list = parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        String actual = list.get(0).getPassword();

        assertEquals(expected, actual);
    }

    @Test
    public void testLessThanValue() throws Exception {
        String expected = "1111<";
        PasswordEntry entry = new PasswordEntry(expected, expected, expected, expected, expected, expected);

        String xml = ConfigStorageFacade.XML_PROLOG + entry.toXmlConfig() +
                ConfigStorageFacade.XML_END;

        ConfigXmlParser parser = new ConfigXmlParser();
        List<PasswordEntry> list = parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        String actual = list.get(0).getPassword();

        assertEquals(expected, actual);
    }


    @Test
    public void testSpecialValue() throws Exception {
        String expected = "111\n\\§$%&/()=?_-#'+~*!@;:'&<<>>[]{}&1234567890#";
        PasswordEntry entry = new PasswordEntry(expected, expected, expected, expected, expected, expected);

        String xml = ConfigStorageFacade.XML_PROLOG + entry.toXmlConfig() +
                ConfigStorageFacade.XML_END;

        ConfigXmlParser parser = new ConfigXmlParser();
        List<PasswordEntry> list = parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        String actual = list.get(0).getPassword();

        assertEquals(expected, actual);
    }

}
