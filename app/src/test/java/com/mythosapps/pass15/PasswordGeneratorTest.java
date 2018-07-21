package com.mythosapps.pass15;

import com.mythosapps.pass15.util.PasswordGenerator;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PasswordGeneratorTest {

    @Test
    public void testEncryptDecrypt() throws Exception {
        assertEquals(16, PasswordGenerator.generate().length());
    }
}
