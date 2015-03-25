package com.qatrend.pomegranate.regex;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	public static boolean checkPatternExists(String regex, String searchStr) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(searchStr);

		if (matcher.find())
			return true;
		else
			return false;

	}

	public static boolean checkPatternExists(String searchInStr, ArrayList<String> regexList){
		Pattern pattern;
		Matcher matcher;
		boolean patternExists = false;
		for(String regex : regexList){
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(searchInStr);
			if (matcher.find())
				patternExists = true;
		}
		return patternExists;
	}
	
	public Matcher regexMatch(String regex, String searchStr) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(searchStr);
		matcher.find();
		return matcher;
	}

	public ArrayList<String> getLinesList(String searchStr) {

		Pattern pattern = Pattern.compile("([^\n]+)");
		ArrayList<String> strLines = new ArrayList<String>();
		Matcher matcher = pattern.matcher(searchStr);

		while (matcher.find()) {
			strLines.add(matcher.group());
		}
		return strLines;

	}

	public Date getLogDate(String logLine) {

		Pattern pattern = Pattern
				.compile("^(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d)");
		Matcher matcher = pattern.matcher(logLine);
		String dateStr = "", yearStr = "", monthStr = "", dayStr = "", hrStr = "", minStr = "", secStr = "";
		int yearInt, monthInt, dayInt, hrInt, minInt, secInt;
		DateFormat formatter;
		Date date = null;

		if (matcher.find()) {
			dateStr = matcher.group();
			yearStr = matcher.group(1);
			yearInt = Integer.parseInt(yearStr);
			monthStr = matcher.group(2);
			monthInt = Integer.parseInt(monthStr);
			dayStr = matcher.group(3);
			dayInt = Integer.parseInt(dayStr);
			hrStr = matcher.group(4);
			hrInt = Integer.parseInt(hrStr);
			minStr = matcher.group(5);
			minInt = Integer.parseInt(minStr);
			secStr = matcher.group(6);
			secInt = Integer.parseInt(secStr);
		}
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// PLogger.getLogger().log(Level.FINE, "year: " + yearStr + " month: " + monthStr +
		// " day: " + dayStr + " hour: " + hrStr + " min: " + minStr + " sec: "
		// + secStr);
		// dateStr = yearStr + "-" + monthStr + "-" + dayStr;
		// PLogger.getLogger().log(Level.FINE, "date string: " + dateStr);
		if (dateStr != "") {
			try {
				date = (Date) formatter.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// PLogger.getLogger().log(Level.FINE, date);
		return date;
	}

	public static String getMatchedStr(String regex, String searchStr) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(searchStr);
		matcher.find();
		return matcher.group();
	}
	
	public static String getMatchedStr(String searchInStr, ArrayList<String> regexList) {
		Pattern pattern;
		Matcher matcher;
		String foundStr = null;
		for(String regex : regexList){
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(searchInStr);
			if( matcher.find()){
				foundStr = matcher.group();
				break;
			}
		}
		return foundStr;
	}
	

	public static ArrayList<String> getAllMatchedGroups(String regex,
			String searchStr) {
		ArrayList<String> foundGroupsList = new ArrayList<String>();
		String matchedGroup = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(searchStr);
		while (matcher.find()) {
			matchedGroup = matcher.group();
			foundGroupsList.add(matchedGroup);
		}
		return foundGroupsList;
	}

}
