package com.mythosapps.pass15;

import com.mythosapps.pass15.util.EncryptionUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EncryptionUtilTest {

    @Test
    public void testEncrypt() throws Exception {
        String expected = "1111";

        byte[] temp = EncryptionUtil.encrypt(expected);

        String actual = EncryptionUtil.decrypt(temp);

        System.out.println("result:'" + temp + "'");

        assertEquals(expected, actual);
    }
}