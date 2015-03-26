package com.qatrend.pomegranate.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class TimeUtil {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());

    private long timeElapsed = 0;
    private long startTime = System.currentTimeMillis();

    public long timeElapsedMillis() {
        long currentTime = System.currentTimeMillis();
        timeElapsed = currentTime - startTime;
        return timeElapsed;
    }

    public long timeElapsedSeconds() {
        long currentTime = System.currentTimeMillis();
        timeElapsed = (currentTime - startTime) / 1000;
        return timeElapsed;
    }
    
    public static Date getGmtTime() {
        Calendar c = Calendar.getInstance();

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();
        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        c.add(Calendar.MINUTE, (-offsetMins));
        logger.info("GMT Time: " + c.getTime());
        return c.getTime();
    }

    public static String getDateTimePart(String format) {
        String value = null;
        Date date = new Date();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            value = formatter.format(date);
        } catch (Exception ex) {
            logger.error(ex);
        }
        return value;
    }

    public static String getDateTimePart(String format, Date date) {
        String value = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            value = formatter.format(date);
        } catch (Exception ex) {
            logger.error(ex);
        }
        return value;
    }
    

}
