package com.qatrend.testutils.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.qatrend.testutils.logging.PLogger;


public class CollectionUtil {
	public String getStringFromList(ArrayList<Object> list) {
		String str = "";
		Iterator<Object> it = list.iterator();
		while(it.hasNext()) {
			str += it.next().toString();
		}
		return str;
	}

	public String getStringFromList(List<String> list) {
		String str = "";
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			str += it.next().toString();
		}
		return str;
	}

	/**
	 * Print a List 
	 * <p>
	 *  
	 * Print the List line by line.
	 * 
	 * @param  list  A list Object
	 * @return       Nothing
	 */
	public static void printList(List<String> list) {
		for(String str : list) {
			PLogger.getLogger().debug( str);
		}
	}
	
	/**
	 * Get an ArrayList from a String of comma separated values 
	 * <p>
	 * User gets an instance of FusionUtil object 
	 * Call the API using the name of the fusion job.
	 * 
	 * @param  csvStr  String of comma separated values
	 * @return         ArrayList of the values
	 */
	public static ArrayList<String> getListFromCsvString(String csvStr) {
		ArrayList<String> arrList = new ArrayList<String>();
		for(String x : csvStr.split(",")) {
			x = x.trim();
			arrList.add(x);
		}
		return arrList;
	}

	/**
	 * Get a String of comma separated values from an ArrayList 
	 * <p>
	 * User gets an instance of FusionUtil object 
	 * Call the API using the name of the fusion job.
	 * 
	 * @param  list  ArrayList of values
	 * @return          String of comma separated values.
	 */
	public static String getCsvFromList(ArrayList<String> list) {
		String csvStr = "";
		for(String str : list) {
			csvStr = csvStr + str + ",";
		}
		csvStr = csvStr.substring(0, csvStr.length()-1);
		return csvStr;
	}
	
	
}
