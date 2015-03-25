package com.qatrend.pomegranate.db;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.zip.InflaterInputStream;

import com.qatrend.pomegranate.logging.PLogger;

/**
 * Utility to read Blob column from a Oracle database table.
 * 
 * @author <a href="http://visitamaresh.com" target=_blank>Amaresh Pattanaik (amaresh@visitamaresh.com)</a>
 *
 */
public class BlobUtil {
	/** jdbc url of the oracle database 
	 * e.g. jdbc:oracle:thin:server1.host.com:2231:servicename
	 * */
	private static String sJdbcUrl = "";
	private static String sDbUser = "";
	private static String sDbPass = "";
	private static String sBlobTable = "";
	private static String sBlobColumn = "";
	private static List<String> sVarList = null;
	private static String sJdbcDriver = "oracle.jdbc.driver.OracleDriver";
	public static String sValSeparator = "~";
	private static String sOutFile = "";
	
	public static void main(String args[]){
		try{
			String folderPath = getCurrentFolderPath() + "/";  // "D:\\MyData\\DailyStatus\\2012\\Jul\\blob_tool\\";
			Properties dbProp = new Properties();
			InputStream is = new FileInputStream(folderPath + "database.properties");
			dbProp.load(is);
			sJdbcDriver = dbProp.getProperty("jdbc.driver");
			sJdbcUrl = dbProp.getProperty("jdbc.url");
			sOutFile = folderPath + dbProp.getProperty("out.file");
			String sVarListStr =  dbProp.getProperty("var.list");
			String[] sVarArr = sVarListStr.split(",");
			sVarList = Arrays.asList(sVarArr) ;
			HashMap<String, ArrayList<String>> varMap = getVariableValuesFromBlob(sJdbcUrl, sBlobTable, sBlobColumn, sVarList);
			FileWriter fWriter = new FileWriter( sOutFile );
			BufferedWriter out = new BufferedWriter( fWriter );
			
			for(String key : varMap.keySet()){
				//System.out.println(key + " = " + listToString(varMap.get(key)));
				out.write( key + " = " + listToString(varMap.get(key)) + "\n" );
			}
			out.close();

		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param jdbcUrl		the jdbc url e.g. jdbc:oracle:thin:server1.host.com:2231:servicename
	 * @param dbUser		database username
	 * @param dbPassword	database password
	 * @param sqlQuery		the sql query to return only one row of the BLOB column. 
	 * 						e.g. if the primary key is ID, table name is TABLE_X, Blob column is BLOB_X, 
	 * 						query might be <i>SELECT BLOB_X FROM TABLE_X WHERE ID=1</i>
	 * @return				string representation of the blob value.
	 */
	public static String getBlobAsString(String jdbcUrl, String dbUser, String dbPassword, String sqlQuery){
		sJdbcUrl = jdbcUrl;
		sDbUser = dbUser;
		sDbPass = dbPassword;
		String blobStr = null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection(sJdbcUrl, sDbUser, sDbPass);
			PreparedStatement stmnt = conn.prepareStatement(sqlQuery);
			ResultSet rs = stmnt.executeQuery();
			Blob aBlob = rs.getBlob(1);
			byte[] allBytesInBlob = aBlob.getBytes(1, (int) aBlob.length());
			blobStr = convertCompressedBinaryToString(allBytesInBlob);
			conn.close();
			stmnt.close();
		}
		catch(Exception ex){
			PLogger.getLogger().error(ex);
		}
		return blobStr;
	}
	
	/**
	 * Get a comma separated string of values from an ArrayList
	 * 
	 * @param		list
	 * @return		comma separated String of values.
	 */
	public static String listToString(ArrayList<String> list){
		String retStr = "";
		for(String str : list){
			retStr = retStr + str + ", ";
		}
		retStr = retStr.substring(0, retStr.length()-2);
		return retStr;
		
	}


	/**
	 * This method assumes that a particular column contains a BLOB of a String of values separated by the global variable sValSeparator
	 * You can set the sValSeparator to whatever is the separator in your BLOB field.
	 * The list of variable names whose values are stored in the BLOB is given as an input.
	 * For each variable name, the values from each row are printed
	 * 
	 * @param jdbcUrl		jdbc url of the database
	 * @param tableName		name of the table
	 * @param columnName	name of the column that has blob data
	 * @param varList		list of names of variables
	 * @return				nothing
	 */
	public static void printBlobAsString(String jdbcUrl, String tableName, int columnName, List<String> varList) {
		sJdbcUrl = jdbcUrl;
		//get the list of variables
		//get the list of variables for the model. 
		
		String value = "";
		int counter = 0;
		byte[] allBytesInBlob = null;
		try { // Prepare a Statement:
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection(jdbcUrl, sDbUser, sDbPass);
			String queryStr = "SELECT " + columnName + " FROM " + tableName ;
			PreparedStatement stmnt = conn.prepareStatement(queryStr);
			ResultSet rs = stmnt.executeQuery();
			// if (rs.next()) {
			while (rs.next()) {
				// Get as a BLOB
				counter++;
				Blob aBlob = rs.getBlob(1);
				allBytesInBlob = aBlob.getBytes(1, (int) aBlob.length());
				int offset = -1;
				int chunkSize = 1024;
				long blobLength = aBlob.length();
				if (chunkSize > blobLength) {
					chunkSize = (int) blobLength;
				}
				char buffer[] = new char[chunkSize];
				StringBuilder stringBuffer = new StringBuilder();
				Reader reader = new InputStreamReader(aBlob.getBinaryStream());

				while ((offset = reader.read(buffer)) != -1) {
					stringBuffer.append(buffer, 0, offset);
				}
				value = stringBuffer.toString();

				// The driver could not handle this as a BLOB...
				// Fallback to default (and slower) byte[] handling
				byte[] bytes = rs.getBytes(1);
				value = convertCompressedBinaryToString(allBytesInBlob);
				System.out.println(counter + "    " + value);
				//get the value of each variable.
				//if the value begins with a ~, then remove it, otherwise the first value will be null.
				if(value.startsWith("~")) {
					value = value.substring(1);
				}
				String [] varValArray = value.split( sValSeparator );
				int numOfVars = varList.size();
				int numOfValuesInModelRes = varValArray.length;
				for(int i=0; i<varValArray.length; i++){
					varValArray[i] = varValArray[i].trim();
					System.out.println(varList.get(i) + " = " + varValArray[i]);
				}
			}

			// Close resources
			rs.close();
			stmnt.close();

		} catch (Exception ex) {
			System.out.println("Error when trying to read BLOB: "
					+ ex.getMessage());
			ex.printStackTrace();
		}
		// value = decodeCharByteArray(allBytesInBlob, "US-ASCII"); //US-ASCII ,
		// UTF-8
	}

	/**
	 * This method assumes that a particular column contains a BLOB of a String of values separated by the global variable sValSeparator
	 * You can set the sValSeparator to whatever is the separator in your BLOB field.
	 * The list of variable names whose values are stored in the BLOB is given as an input.
	 * For each variable name, the variable name and list of values are stored in a map
	 * 
	 * @param jdbcUrl		jdbc url for connecting to the database
	 * @param tableName		name of the database table
	 * @param columnName	name of the database column
	 * @param varList		list of names of the variables
	 * @return				A HashMap that has key = name of variable and value = list of values for that variable. 
	 */
	public static HashMap<String, ArrayList<String>> getVariableValuesFromBlob(String jdbcUrl, String tableName, String columnName, List<String> varList) {
		HashMap<String, ArrayList<String>> retMap = new HashMap<String, ArrayList<String>>();
		ArrayList<String> varValList = new ArrayList<String>();
		sJdbcUrl = jdbcUrl;
		//get the list of variables
		String value = "";
		int counter = 0;
		byte[] allBytesInBlob = null;
		try { // Prepare a Statement:
			Class.forName(sJdbcDriver);
			Connection conn = DriverManager.getConnection(jdbcUrl, sDbUser, sDbPass);
			String queryStr = "SELECT " + columnName + " FROM " + tableName ;
			PreparedStatement stmnt = conn.prepareStatement(queryStr);
			ResultSet rs = stmnt.executeQuery();
			//For each blob
			while (rs.next()) {
				// Get as a BLOB
				counter++;
				Blob aBlob = rs.getBlob(1);
				allBytesInBlob = aBlob.getBytes(1, (int) aBlob.length());
				int offset = -1;
				int chunkSize = 1024;
				long blobLength = aBlob.length();
				if (chunkSize > blobLength) {
					chunkSize = (int) blobLength;
				}
				char buffer[] = new char[chunkSize];
				StringBuilder stringBuffer = new StringBuilder();
				Reader reader = new InputStreamReader(aBlob.getBinaryStream());

				while ((offset = reader.read(buffer)) != -1) {
					stringBuffer.append(buffer, 0, offset);
				}
				//Here is the text value of the Blob
				value = stringBuffer.toString();

				// The driver could not handle this as a BLOB...
				// Fallback to default (and slower) byte[] handling
				byte[] bytes = rs.getBytes(1);
				value = convertCompressedBinaryToString(allBytesInBlob);
				//System.out.println(counter + "    " + value);
				//get the value of each variable.
				//if the value begins with a ~, then remove it, otherwise the first value will be null.
				if(value.startsWith( sValSeparator )) {
					value = value.substring(1);
				}
				String [] varValArray = value.split( sValSeparator );
				int numOfVars = varList.size();
				int numOfValuesInModelRes = varValArray.length;
				String varName = null;
				String varVal = null;
				ArrayList<String> listVar = new ArrayList<String>();
				for(int i=0; i<varValArray.length; i++){
					varName = varList.get(i);
					varVal = varValArray[i];
					varVal = varVal.trim();
					if(retMap.containsKey(varName)){
						listVar = retMap.get(varName);
						listVar.add(varVal);
						retMap.put(varName, listVar);
					}
					else{
						listVar = new ArrayList<String>();
						listVar.add(varVal);
						retMap.put(varName, listVar);
					}
					
				}
				
			}

			// Close resources
			rs.close();
			stmnt.close();

		} catch (Exception ex) {
			System.out.println("Error when trying to read BLOB: "
					+ ex.getMessage());
			ex.printStackTrace();
		}
		// value = decodeCharByteArray(allBytesInBlob, "US-ASCII"); //US-ASCII ,
		// UTF-8
		return retMap;
	}
	
	
	// Now, depending on your character encoding parse the bytes to human
	// readable form as follows

	private static String decodeCharByteArray(byte[] inputArray, String charSet) { // Ex
		// charSet="US-ASCII"
		Charset theCharset = Charset.forName(charSet);
		CharsetDecoder decoder = theCharset.newDecoder();
		ByteBuffer theBytes = ByteBuffer.wrap(inputArray);
		CharBuffer inputArrayChars = null;
		try {
			inputArrayChars = decoder.decode(theBytes);
		} catch (CharacterCodingException e) {
			System.err.println("Error decoding");
			e.printStackTrace();
		}
		return inputArrayChars.toString();
	}

	private static String convertCompressedBinaryToString(
			byte[] serializedExtendedOutput) throws Exception {
		byte[] seo = serializedExtendedOutput;
		if ((null == seo) || (seo.length == 0)) {
			return null;
		}
		ByteArrayInputStream bin = new ByteArrayInputStream(
				serializedExtendedOutput);
		InflaterInputStream in = new InflaterInputStream(bin);
		ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
		try {
			int b;
			while ((b = in.read()) != -1) {
				bout.write(b);
			}
			in.close();
			bout.close();
		} catch (IOException e) {
			throw new Exception("IOException reading SerializedExtendedOutput",
					e);
		}
		return bout.toString();
	}

	private static String getCurrentFolderPath() {
		File file = new File(".");
		String path = file.getAbsolutePath();
		return path;
	}



	
}