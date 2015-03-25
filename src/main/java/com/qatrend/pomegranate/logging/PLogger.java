package com.qatrend.pomegranate.logging;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class PLogger {
	private static Logger logger = null;
	private static final String LOG4J_CONFIG = "src/main/resources/log4j.properties";
	
	public static Logger getLogger() {
    	Properties prop = new Properties();
		if (logger == null) {
			logger = Logger.getLogger("com.qatrend");
			// DEBUG, INFO, WARN, ERROR - default = INFO
			String logLevel = getEnvOrSystemProperty("PLOGGER_LEVEL");
			if (logLevel == null) {
				logLevel = "INFO";
			}
			logLevel = logLevel.toUpperCase();
			logLevel = logLevel.trim();
			// Map<Integer, String> logLevelMap = new HashMap<Integer,
			// String>();

			if (logLevel.equals("ERROR")) {
				logLevel = "ERROR";
			} else if (logLevel.equals("WARN")) {
				//LogManager.getRootLogger().setLevel((Level)Level.WARN);
				logLevel = "WARN";
			} else if (logLevel.equals("INFO")) {
				logLevel = "INFO";
			} else if (logLevel.equals("DEBUG")) {
				logLevel = "DEBUG";
			} else {
				logLevel = "INFO";
			}
			
	    	try {
	    		//prop.load(new FileInputStream( LOG4J_CONFIG ));
	    		//set the properties value
	    		prop.setProperty("log4j.rootLogger", logLevel + ", file, stdout");
	    		prop.setProperty("log4j.appender.file", "org.apache.log4j.RollingFileAppender" );
	    		prop.setProperty("log4j.appender.file.File", "bluefinFiles/bluefinLogs/platypus.log" );
	    		prop.setProperty("log4j.appender.file.MaxFileSize", "1MB" );
	    		prop.setProperty("log4j.appender.file.MaxBackupIndex", "1");
	    		prop.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout" );
	    		//prop.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
	    		prop.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %m%n");
	    		prop.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
	    		prop.setProperty("log4j.appender.stdout.Target", "System.out");
	    		prop.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
	    		//prop.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
	    		prop.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %m%n");
	    		//save properties to project root folder
	    		//prop.store(new FileOutputStream( LOG4J_CONFIG ), null);
	 
	    	} catch (Exception ex) {
	    		ex.printStackTrace();
	        }
	    	
			PropertyConfigurator.configure( prop );			
		}

		return logger;
	}

	private static String getEnvOrSystemProperty(String propName) {
		String propValue = null;
		propValue = System.getenv(propName);
		if (propValue == null) {
			propValue = System.getProperty(propName);
		}
		return propValue;
	}

	private static void updateLog4jConfiguration(String property, String value) { 
	    Properties props = new Properties(); 
	    try { 
	        InputStream configStream = PLogger.class.getClass().getResourceAsStream( "/log4j.properties"); 
	        props.load(configStream); 
	        configStream.close(); 
	    } catch (IOException e) { 
	        System.out.println("Error: Cannot laod configuration file "); 
	    } 
	    //props.setProperty("log4j.appender.FILE.file", logFile);
	    props.setProperty(property, value);
	    LogManager.resetConfiguration(); 
	    PropertyConfigurator.configure(props); 
	 }

}
