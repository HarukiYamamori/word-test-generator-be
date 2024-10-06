package com.application.pdfapplication.util;

public class SubString {
    public static final String EMPTY_STR = "";
    public static String getSubString(String val, String preStr, String postStr) {
        if(val == null)
            return EMPTY_STR;
        if(existValue(preStr)) {
            int idx = val.indexOf(preStr);
            if(idx < 0)
                return EMPTY_STR;
            val = val.substring(idx + preStr.length());
        }
        if(existValue(postStr)) {
            int idx = val.indexOf(postStr);
            if(idx < 0)
                return val;
            val = val.substring(0, idx);
        }
        return val;
    }

    private static boolean existValue(String str) {
        if(str.length() == 0) {
            return false;
        }
        if(str == null) {
            return false;
        }
        return true;
    }
}
