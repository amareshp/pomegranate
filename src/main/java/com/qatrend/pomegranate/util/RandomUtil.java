package com.qatrend.pomegranate.util;

import java.math.BigInteger;
import java.util.Random;

import org.apache.log4j.Logger;

public class RandomUtil {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());
    
    public static String getThirteenDigitRandomNum() {
        Random r = new Random();
        final long LOW = 1000000000000L;
        final long HIGH = 9999999999999L;
        // This gives you a random number in between LOW (inclusive) and HIGH (exclusive)
        long randomNum = LOW+((long)(r.nextDouble()*(HIGH-LOW)));
        String randomNumStr = String.valueOf(randomNum);
        logger.debug("Thirteen digit random number: " + randomNumStr);
        return randomNumStr;
    }
    
    public static int randInt(int min, int max) {
        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }   

    public static long randLong(long min, long max) {
        Random r = new Random();
        long randomNum = min +((long)(r.nextDouble()*(max-min)));
        return randomNum;
    }   

    public static long randLong(int digits) {
        Random r = new Random();
        long min = BigInteger.TEN.pow(digits-1).longValue();
        long max = BigInteger.TEN.pow(digits).longValue() - 1;
        long randomNum = min +((long)(r.nextDouble()*(max-min)));
        return randomNum;
    }
    
}
