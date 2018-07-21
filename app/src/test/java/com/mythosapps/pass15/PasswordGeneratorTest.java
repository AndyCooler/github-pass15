package com.mythosapps.pass15;

import com.mythosapps.pass15.util.PasswordGenerator;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PasswordGeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        for (int i = 0; i < 10000; i++) {
            assertEquals(16, PasswordGenerator.generate().length());
        }
    }
}
