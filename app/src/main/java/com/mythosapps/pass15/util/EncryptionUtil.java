package com.mythosapps.pass15.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class EncryptionUtil {

    private static final byte[] SALT = "1111111111111111".getBytes();

    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static byte[] encrypt(String message) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SALT, "AES");
        //Cipher cipher = Cipher.getInstance(TRANSFORMATION, "SunJCE");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message.getBytes(Charset.forName("UTF-8")));
    }

    public static String decrypt(byte[] encryptedBytes) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SALT, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encryptedBytes), Charset.forName("UTF-8"));
    }

    public static String readAndDecrypt(InputStream fis) throws Exception {

        String decryptedData = "";
            try {

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int b;
                byte[] d = new byte[8];
                while((b = fis.read(d)) != -1) {
                    bos.write(d, 0, b);
                }

                decryptedData = EncryptionUtil.decrypt(bos.toByteArray());
                fis.close();
                fis = null;

            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
            return "".equals(decryptedData) ? null : decryptedData;
    }

    public static void writeAndEncrypt(OutputStream fos, String dataToEncrypt) throws Exception {
        String toEncrypt = dataToEncrypt == null ? "" : dataToEncrypt;

        try {
            fos.write(EncryptionUtil.encrypt(toEncrypt));
            fos.flush();
            fos.close();
            fos = null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
