package com.qatrend.pomegranate.reflection;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.qatrend.pomegranate.logging.PLogger;

public class DumpObjectConfig {
	private static final Logger logger = PLogger.getLogger();
			
    private static boolean dumpObjEnabled = true;
    private static boolean dumpXMLFormat = false;
    private static boolean overwriteDumpFile = false;
 

	public static String DUMP_OBJ_ENABLED = "dump_obj_enabled" ;
    public static String DUMP_XML_FORMAT = "dump_xml_format" ;
    public static String OVERWRITE_DUMP_FILE = "overwrite_dump_file" ;

    
    public static void setDumpRBOEnabled(boolean b) {
    	dumpObjEnabled = b;
    }
    public static boolean isDumpRBOEnabled() {
    	return dumpObjEnabled;
    }
    
    public static boolean isDumpXMLForamt() {
    	return dumpXMLFormat;
    }
    public static void setDumpXMLForamt(boolean b) {
    	dumpXMLFormat = b;
    }

    public static boolean isOverwriteDumpFile() {
		return overwriteDumpFile;
	}
	public static void setOverwriteDumpFile(boolean overwriteDumpFile) {
		DumpObjectConfig.overwriteDumpFile = overwriteDumpFile;
	}
 
    public static void configMe() {
    	try{
    		InputStream input = new FileInputStream("config.properties");
        	Properties properties = new Properties();
        	properties.load(input);
        			if ( properties.containsKey(DUMP_OBJ_ENABLED) )
        				DumpObjectConfig.setDumpRBOEnabled(properties.getProperty(DUMP_OBJ_ENABLED).equals("1"));

        			if ( properties.containsKey(DUMP_XML_FORMAT))
        				DumpObjectConfig.setDumpXMLForamt(properties.getProperty(DUMP_XML_FORMAT).equals("1"));

        			if ( properties.containsKey(OVERWRITE_DUMP_FILE))
        				DumpObjectConfig.setOverwriteDumpFile(properties.getProperty(OVERWRITE_DUMP_FILE).equals("1"));
    	} catch(Exception ex){
    		logger.equals(ex);
    	}
  	
    }
}

