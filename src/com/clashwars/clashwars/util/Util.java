package com.clashwars.clashwars.util;

import com.clashwars.cwcore.utils.CWUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4CW&8] &6" + msg);
    }
}
