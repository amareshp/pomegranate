package com.qatrend.testutils.logging;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.common.io.Files;
import com.qatrend.testutils.regex.RegexUtil;

public class LogClipper {
	public String getLogs(String filePath, String startTime, String endTime, String regex) {
		String logMsg = "";
		long startLine = 1;
		long lastLine = 1;
		File file = new File(filePath);
		RegexUtil reUtil = new RegexUtil();
		Date startDate=null, endDate=null, logLineDate=null;
		DateFormat formatter;

		Charset charset = Charset.forName("US-ASCII");
		PLogger.getLogger().debug( "Printing logs between " + startTime + " and " + endTime + " that match pattern: " + regex);
		
		try { 
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startDate = (Date)formatter.parse(startTime);
			endDate = (Date)formatter.parse(endTime);
			
			BufferedReader reader = Files.newReader(file, charset);
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	if(reUtil.checkPatternExists(regex, line)) {
					//PLogger.getLogger().debug( line);
					logLineDate = reUtil.getLogDate(line);
					//PLogger.getLogger().debug( "Date is: " + reUtil.getLogDate(line));
					if( (logLineDate != null) && (  logLineDate.equals(startDate) || logLineDate.after(startDate) ) 
							&& (  logLineDate.equals(endDate) || logLineDate.before(endDate) ) ) {
						//PLogger.getLogger().debug( line);
						PLogger.getLogger().debug(  reUtil.getMatchedStr(regex, line));
					}
		    	}
		    }

		} catch (Exception x) {
			System.err.format("IOException: %s%n", x);
		}


		
		return logMsg;
	}

	public ArrayList<String> getLogs(String filePath, Date startTime, Date endTime, String regex) {
		ArrayList<String> logMsg = new ArrayList<String>();
		long startLine = 1;
		long lastLine = 1;
		File file = new File(filePath);
		RegexUtil reUtil = new RegexUtil();
		Date logLineDate=null;

		Charset charset = Charset.forName("US-ASCII");
		PLogger.getLogger().debug( "Printing logs between " + startTime + " and " + endTime + " that match pattern: " + regex);
		try { 
			
			BufferedReader reader = Files.newReader(file, charset);
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	if(reUtil.checkPatternExists(regex, line)) {
					//PLogger.getLogger().debug( line);
					logLineDate = reUtil.getLogDate(line);
					//PLogger.getLogger().debug( "Date is: " + reUtil.getLogDate(line));
					if( (logLineDate != null) && (  logLineDate.equals(startTime) || logLineDate.after(startTime) ) 
							&& (  logLineDate.equals(endTime) || logLineDate.before(endTime) ) ) {
						PLogger.getLogger().debug( line);
						logMsg.add(line + "\n");
					}
		    	}
		    }

		} catch (Exception x) {
			System.err.format("IOException: %s%n", x);
		}
		return logMsg;
	}
	
	
}
