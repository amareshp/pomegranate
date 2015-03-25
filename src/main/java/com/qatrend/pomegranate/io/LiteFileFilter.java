package com.qatrend.pomegranate.io;

import java.io.FilenameFilter;
import java.io.File;

public class LiteFileFilter implements FilenameFilter{
	String extn;
	public LiteFileFilter(String extn){
		this.extn = extn;
	}
    public boolean accept(File dir, String name) {
        return (name.endsWith( extn ));
    }

}
