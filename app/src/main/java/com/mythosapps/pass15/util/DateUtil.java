package com.mythosapps.pass15.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String today() {
        Date date = new Date();
        return DATE_FORMAT.format(date);
    }
}
