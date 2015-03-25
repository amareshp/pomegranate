package com.qatrend.pomegranate.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.qatrend.pomegranate.logging.PLogger;


public class JsonUtil {
	private static String indent = "";
	public static void printJsonObjects(String jsonStr) {
		JSONObject jo1 = null;
		JSONObject jo2 = null;
		try{
			jo1 = new JSONObject( jsonStr );
			//for every json name parameter
			for(String name : JSONObject.getNames(jo1) ) {
				//assume that the value is another json object
				try {
					//print the name value pair
					PLogger.getLogger().debug( indent + name + " : " + jo1.getString(name));
					//check if the value is a json object
					jo2 = new JSONObject(jo1.getString(name));
					//if the value is another Json object increase indent to print it.
					indent = indent + "    ";
					printJsonObjects(jo1.getString(name));
					indent = "";
				}
				//print it if it's not a json object (which means it's a string value).
				catch (JSONException jex) {
					//reset the indent after printing it. 
				}
			}
		}
		catch(Exception e) {
			PLogger.getLogger().debug( "Exception: " + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
}
