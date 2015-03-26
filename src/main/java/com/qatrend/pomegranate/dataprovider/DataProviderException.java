package com.qatrend.pomegranate.dataprovider;

/**
 * This Exception class is specific to data reader.
 */
public class DataProviderException extends Exception {

    private static final long serialVersionUID = 3290312548375984346L;

    public DataProviderException() {

    }

    public DataProviderException(String message) {
        super(message);
    }

    public DataProviderException(Throwable exception) {
        super(exception);
    }

    public DataProviderException(String message, Throwable exception) {
        super(message, exception);
    }
}
