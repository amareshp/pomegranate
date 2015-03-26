package com.qatrend.pomegranate.dataprovider;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A utility class intended to serve as a helper class for miscellaneous operations being done by
 * {@link SimpleExcelDataProvider} and {@link YamlDataProvider}.
 * 
 */
final class DataProviderHelper {

    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());

    private DataProviderHelper() {

    }

    /**
     * This function will parse the index string into separated individual indexes as needed. Calling the method with a
     * string containing "1, 3, 5-7, 11, 12-14, 8" would return a list of Integers {1, 3, 5, 6, 7, 11, 12, 13, 14, 8}
     * 
     * @param value
     *            the input string represent the indexes to be parse.
     * @return a list of indexes represented as Integers
     * @throws DataProviderException
     */
    public static List<Integer> parseIndexString(String value) throws DataProviderException {
        List<Integer> rows = new ArrayList<Integer>();
        int begin, end;
        String[] parsed;
        String[] parsedIndex = value.split(",");
        for (String index : parsedIndex) {
            if (index.contains("-")) {
                parsed = index.split("-");
                begin = Integer.parseInt(parsed[0].trim());
                end = Integer.parseInt(parsed[1].trim());
                for (int i = begin; i <= end; i++) {
                    rows.add(i);
                }
            } else {
                try {
                    rows.add(Integer.parseInt(index.trim()));
                } catch (NumberFormatException e) {
                    String msg = "Index '" + index + "' is invalid. Please "
                            + "provide either individual numbers or ranges.";
                    msg += "Range needs to be de-marked by '-'";
                    throw new DataProviderException(msg, e);
                }
            }

        }
        return rows;
    }

}
