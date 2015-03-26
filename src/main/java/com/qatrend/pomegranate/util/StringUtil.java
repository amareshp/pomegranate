package com.qatrend.pomegranate.util;

import org.apache.log4j.Logger;

public class StringUtil {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());
    
    public static boolean nullOrEmpty(String input) {
        if (input == null || input.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(String input) {
        if (input.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isPrintableAscii(byte value) {
        return (value > 32) && (value < 127);
    }

    public static String readableText(byte[] buffer) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < buffer.length; ++index) {
            byte current = buffer[index];
            if (isPrintableAscii(current)) {
                builder.append((char) current);
            } else {
                builder.append('.');
            }
        }

        return builder.toString();
    }

    public static String readableText(byte[] buffer, boolean printByteValue) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < buffer.length; ++index) {
            byte current = buffer[index];
            if (isPrintableAscii(current)) {
                builder.append((char) current);
            } else if(printByteValue) {
                builder.append( "#" + (int)current );
            } else {
                builder.append( '.' );
            }
        }

        return builder.toString();
    }
    
}
