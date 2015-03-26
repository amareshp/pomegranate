package com.qatrend.pomegranate.dataprovider;

/**
 * This exception is supposed to wrap exception being thrown while reading a YAML file.
 */
public class YamlDataProviderException extends DataProviderException {

    /**
     * 
     */
    private static final long serialVersionUID = -7652446945694720141L;

    public YamlDataProviderException(String msg, Throwable e) {
        super(msg, e);
    }

}
