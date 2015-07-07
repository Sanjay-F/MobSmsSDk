package com.lifesense.mobsmssdk;

import android.content.Context;


public class StringUtil {
    public static boolean isEmpty(String text) {
        return "".equals(text) || text == null;
    }


    public static String format(Context context, int format, Object... args) {
        if (context == null) {
            return null;
        }
        return String.format(context.getString(format), args);
    }

}
