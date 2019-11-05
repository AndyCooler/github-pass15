package com.mythosapps.pass15.util;

import java.util.Random;

public class PasswordGenerator {
    public static String generate() {

        int length = 16;

        String upper= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!-_+#/=";

        Random upperR = new Random();
        Random lowerR = new Random();
        Random digitsR = new Random();
        Random specialR = new Random();

        Random whichR = new Random();

        String generated = "";
        for (int i = 0; i < length; i++) {
            switch (whichR.nextInt(4)) {
                case 0: generated += generateNext(upper, upperR); break;
                case 1: generated += generateNext(lower, lowerR); break;
                case 2: generated += generateNext(digits, digitsR); break;
                case 3: generated += generateNext(special, specialR); break;
                default: { throw new IllegalArgumentException("PasswordGenerator unknown type");}
            }
        }
        return generated;
    }

    private static String generateNext(String source, Random sourceR) {
        int selected = sourceR.nextInt(source.length());
        return source.substring(selected, selected+1);
    }
}
