package com.qatrend.pomegranate.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.qatrend.pomegranate.exception.ExceptionUtil;
import com.qatrend.pomegranate.system.SystemUtil;

public class FileUtil {

	public static ArrayList<String> readFileAsList(String filePath){
		ArrayList<String> strList = new ArrayList<String>();
		String line;
		try{
			FileReader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			while((line = br.readLine()) != null) {
				if(line.trim().length() != 0) {
					strList.add(line);
				}
			}
			fr.close();
		}
		catch(Exception ex){
			System.out.println( ExceptionUtil.getBriefExceptionMsg(ex) );
		}
		return strList;
	}

	public static ArrayList<String> readFileAsList(InputStream fileIn){
		ArrayList<String> strList = new ArrayList<String>();
		StringBuffer strContent = new StringBuffer("");
		String strFull;
		String strArr[];
		int ch;
		try{
			//FileReader fr = new FileReader(new File(filePath));
			//FileReader fr = new FileReader( fileIn );
			//BufferedReader br = new BufferedReader( fileIn );
			while( (ch = fileIn.read()) != -1)
		        strContent.append((char)ch);
			strFull = strContent.toString();
			strList = stringToList(strFull);
//			while((line = br.readLine()) != null) {
//				if(line.trim().length() != 0) {
//					strList.add(line);
//				}
//			}
			
			fileIn.close();
		}
		catch(Exception ex){
			System.out.println( ExceptionUtil.getBriefExceptionMsg(ex) );
		}
		return strList;
	}
	
	public static ArrayList<String> stringToList(String str){
		ArrayList<String> strList = new ArrayList<String>();
		String strArr[];
		String lineSep = SystemUtil.getProperty("line.separator");
		strArr = str.split(lineSep);
		for(int i=0; i<strArr.length; i++){
			strList.add(strArr[i]);
		}
		return strList;
	}
	
	public static void writeListToFile(ArrayList<String> strList, String filePath){
		try{
			FileWriter fw = new FileWriter(new File(filePath));
			BufferedWriter bw = new BufferedWriter(fw);
			String lineSeparator = System.getProperty("line.separator");
			for(String line : strList){
				bw.write(line + lineSeparator);
			}
			bw.flush();
			bw.close();
			fw.close();
		}
		catch(Exception ex){
			System.out.println( ExceptionUtil.getBriefExceptionMsg(ex) );
		}
	}

	public static void writeListToFile(ArrayList<String> strList, OutputStream fileOut){
		try{
			//FileWriter fw = new FileWriter(new File(filePath));
			//BufferedWriter bw = new BufferedWriter(fw);
			String lineSeparator = System.getProperty("line.separator");
			for(String line : strList){
				//bw.write(line + lineSeparator);
				fileOut.write(line.getBytes());
				fileOut.write(lineSeparator.getBytes());
			}
			fileOut.flush();
			fileOut.close();
		}
		catch(Exception ex){
			System.out.println( ExceptionUtil.getBriefExceptionMsg(ex) );
		}
	}
	
	
	public static String readFileAsString(String filePath){
		File file = new File(filePath);
		byte[] b  = new byte[ (int) file.length()];
		int len = b.length;
		int total = 0;
		try{
			InputStream in = new FileInputStream( file );

			while (total < len) {
			  int result = in.read(b, total, len - total);
			  if (result == -1) {
			    break;
			  }
			  total += result;
			}
			in.close();
		}
		catch(Exception ex){
			System.out.println(ExceptionUtil.getBriefExceptionMsg(ex));
		}
		return new String( b );	
		}
}
