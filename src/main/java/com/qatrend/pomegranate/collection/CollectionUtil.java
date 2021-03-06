package com.qatrend.pomegranate.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.qatrend.pomegranate.logging.PLogger;

/**
 * This is a utility class that has various methods to do frequently used operations on Collections. 
 * 
 * @author <a href="http://visitamaresh.com" target=_blank>Amaresh Pattanaik (amaresh@visitamaresh.com)</a>
 * 
 */
public class CollectionUtil {
	/**
	 * Method for getting a String from an ArrayList
	 * 
	 * @param	list	an ArrayList of Object
	 * @return			a String which is a concatenation of elements (without any separator) in the list
	 */
	public String getStringFromList(ArrayList<Object> list) {
		String str = "";
		Iterator<Object> it = list.iterator();
		while(it.hasNext()) {
			str += it.next().toString();
		}
		return str;
	}

	/**
	 * Method for getting a String from an List of Objects.
	 * 
	 * @param	list	an ArrayList of Object
	 * @param  separator separator String
	 * @return	str		a String which is a concatenation of elements separated by the separator parameter
	 */
	public String getStringFromList(List<Object> list, String separator) {
		String str = "";
		Iterator<Object> it = list.iterator();
		while(it.hasNext()) {
			str += it.next().toString() + separator;
		}
		str = str.substring( 0, ( str.length()-separator.length() ) );
		return str;
	}
	
	/**
	 * Method for getting a String from a list of String
	 * 
	 * @param	list	a List of String
	 * @return			a String which is concatenation of the elements in the list
	 */
	public String getStringFromList(List<String> list) {
		String str = "";
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			str += it.next().toString();
		}
		return str;
	}

	/**
	 * Print a List - one element per line
	 * 
	 * @param	list	A list Object
	 * 
	 */
	public static void printList(List<String> list) {
		for(String str : list) {
			PLogger.getLogger().info( str);
		}
	}
	
	/**
	 * Get an ArrayList from a String of comma separated values 
	 * 
	 * @param  csvStr  comma separated values
	 * @return ArrayList ArrayList of the values
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
	 * 
	 * @param  list  	ArrayList of values
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
	
    public static void printMap(Map<String, Object> map) {
        for(String key : map.keySet()) {
            PLogger.getLogger().info(key + " : " + map.get(key));
        }
    }

    public static void printMapStr(Map<String, String> map) {
        for(String key : map.keySet()) {
            PLogger.getLogger().info(key + " : " + map.get(key));
        }
    }
	
    public static void printListOfObjects(List<Object> list) {
        for(Object element : list) {
            PLogger.getLogger().info(element);
        }
    }
	
}
