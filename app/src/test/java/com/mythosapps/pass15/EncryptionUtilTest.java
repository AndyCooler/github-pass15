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
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EncryptionUtilTest {

    @Test
    public void testEncryptDecrypt() throws Exception {
        String expected = "1111";

        byte[] temp = EncryptionUtil.encrypt(expected);

        String actual = EncryptionUtil.decrypt(temp);

        System.out.println("result:'" + temp + "'");

        assertEquals(expected, actual);
    }

    @Test
    public void testWriteAndEncrypt() throws Exception {
        String expected = "1111";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        EncryptionUtil.writeAndEncrypt(os, expected);

        String temp = os.toString("UTF-8");

        System.out.println("testWriteAndEncrypt result:'" + temp + "'");

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        String actual = EncryptionUtil.readAndDecrypt(is);
        System.out.println("testWriteAndEncrypt result: actual:'" + actual + "'");

        assertEquals(expected, actual);
    }

    @Test
    public void testWriteAndEncryptNull() throws Exception {
        String expected = null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        EncryptionUtil.writeAndEncrypt(os, expected);

        String temp = os.toString("UTF-8");

        System.out.println("testWriteAndEncrypt result:'" + temp + "'");

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        String actual = EncryptionUtil.readAndDecrypt(is);
        System.out.println("testWriteAndEncrypt result: actual:'" + actual + "'");

        assertEquals(expected, actual);
    }

}