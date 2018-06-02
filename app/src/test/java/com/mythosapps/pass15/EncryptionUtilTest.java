package com.mythosapps.pass15;

import com.mythosapps.pass15.util.EncryptionUtil;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * Tests EncryptionUtil symmetric enc -> dec.
 */
public class EncryptionUtilTest {

    @Test
    public void testEncryptDecrypt() throws Exception {
        String expected = "1111";

        byte[] temp = EncryptionUtil.encrypt(expected);

        String actual = EncryptionUtil.decrypt(temp);

        System.out.println("testEncryptDecrypt result:'" + temp + "'");

        assertEquals(expected, actual);
    }

    @Test
    public void testEncryptDecryptStream() throws Exception {
        String expected = "1111";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        EncryptionUtil.writeAndEncrypt(os, expected);

        String temp = os.toString("UTF-8");

        System.out.println("testEncryptDecryptStream result:'" + temp + "'");

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        String actual = EncryptionUtil.readAndDecrypt(is);
        System.out.println("testEncryptDecryptStream result: actual:'" + actual + "'");

        assertEquals(expected, actual);
    }

    @Test
    public void testEncryptDecryptStreamXML() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<config>\n" +
                "    <entry>\n" +
                "        <category>Privat</category>\n" +
                "        <name>Meine Webseite</name>\n" +
                "        <username>Mein Username</username>\n" +
                "        <password>Mein Passwort</password>\n" +
                "        <created>2018-05-24</created>\n" +
                "        <lastModified>2018-05-24</lastModified>\n" +
                "    </entry>\n" +
                "    <entry>\n" +
                "        <category>Privat</category>\n" +
                "        <name>Meine Webseite 2</name>\n" +
                "        <username>Mein Username 2</username>\n" +
                "        <password>Mein Passwort 2</password>\n" +
                "        <created>2018-05-24</created>\n" +
                "        <lastModified>2018-05-24</lastModified>\n" +
                "    </entry>\n" +
                "    <entry>\n" +
                "        <category>Beruflich</category>\n" +
                "        <name>Meine Firmenseite</name>\n" +
                "        <username>Mein Username 3</username>\n" +
                "        <password>Mein Passwort 3</password>\n" +
                "        <created>2018-05-24</created>\n" +
                "        <lastModified>2018-05-24</lastModified>\n" +
                "    </entry>\n" +
                "\n" +
                "</config>";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        EncryptionUtil.writeAndEncrypt(os, expected);

        String temp = os.toString("UTF-8");

        System.out.println("testEncryptDecryptStreamXML result:'" + temp + "'");

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        String actual = EncryptionUtil.readAndDecrypt(is);
        System.out.println("testEncryptDecryptStreamXML result: actual:'" + actual + "'");

        assertEquals(expected, actual);
    }


    @Test
    public void testEncryptDecryptStreamWithNullParam() throws Exception {
        String expected = null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        EncryptionUtil.writeAndEncrypt(os, expected);

        String temp = os.toString("UTF-8");

        System.out.println("testEncryptDecryptStreamWithNullParam result:'" + temp + "'");

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        String actual = EncryptionUtil.readAndDecrypt(is);
        System.out.println("testEncryptDecryptStreamWithNullParam result: actual:'" + actual + "'");

        assertEquals(expected, actual);
    }

}