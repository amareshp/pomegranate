package com.qatrend.pomegranate.dataprovider;

/**
 * This Exception is supposed to wrap exceptions thrown While trying to read an Excel file
 */
public class ExcelDataProviderException extends DataProviderException {

    private static final long serialVersionUID = -8591071132355816927L;

    public ExcelDataProviderException(String msg) {
        super(msg);
    }

    public ExcelDataProviderException(String msg, Throwable e) {
        super(msg, e);
    }

}
