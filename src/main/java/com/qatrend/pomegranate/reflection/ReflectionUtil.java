package com.qatrend.pomegranate.reflection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.qatrend.pomegranate.logging.PLogger;

public class ReflectionUtil {

	private static Logger logger = PLogger.getLogger();
	private static final String OBJ_DUMP = "obj_dump";
	
	public static void appendToFile(String filePath, String content){
		try{
			FileOutputStream fos = new FileOutputStream(filePath, true);
		    fos.write(content.getBytes());
		    fos.flush();
		    fos.close();		
		    } catch(Exception ex) {
				logger.error(ex);
		}
	}
	
	public static void printObj(Object obj){
		try{
			StringBuffer sb = new StringBuffer();
			ElementProcessor.initObjectFunctionPrametersMap();
			ElementProcessor processor = new ElementProcessor();
			String objName = obj.getClass().getSimpleName();
			Element ele = processor.processObject(objName, objName, obj, null);
			ele.dump(sb);
			String filePath = "logs/obj-test-output.txt";
			ReflectionUtil.appendToFile(filePath, sb.toString());
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	public static void truncateFile(String filePath){
		File file = new File(filePath);
		try{
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			fw.write("");
			fw.flush();
			fw.close();
		} catch(Exception ex) {
			logger.error(ex);
		}
	}
	
	public static String getObjOutputFilePath(){
		String filePath = null;
		try{
			filePath = "logs/rbo-test-output.txt";
		} catch(Exception ex) {
			logger.error(ex);
		}
		return filePath;
	}
	
	public static void dumpObject(Object object){
		StringBuffer sb = new StringBuffer();
		ElementProcessor.initObjectFunctionPrametersMap();
		ElementProcessor processor = new ElementProcessor();
		if(object != null){
			try {
				processor.emptyObjStack();
				Element ele = processor.processObject("method: ", object.getClass().getSimpleName(), object, null);
				ele.dump(sb);
			}
			catch(Exception ex) {
				logger.info("Exception in dumpObject:  " + ex.getMessage());
			}
		} else {
			processor.emptyObjStack();
			Element ele = processor.processObject("method: ", "", null, null);
			ele.dump(sb);
		}
		
		
		String strFilename = getDumpFileName(OBJ_DUMP);
		try {
			FileUtils.writeStringToFile(new File(strFilename), sb.toString());
			//FileOutputStream fos = new FileOutputStream(strFilename);
			//OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			//out.write(sb.toString());
			//out.close();
		} catch (Exception ex) { 
		    logger.info("Exception in dumpObject:  " + ex.getMessage());
		}
   	
	}
	
    private static String getDumpFileName(String str) {
    	StringBuffer bufFileName = new StringBuffer();
    	bufFileName.append(getFilePathString());
    	bufFileName.append("/");
		bufFileName.append(str);
    	
    	if ( DumpObjectConfig.isOverwriteDumpFile() ) {
    		if ( DumpObjectConfig.isDumpXMLForamt() )
    			bufFileName.append(".xml");
    		else
    			bufFileName.append(".txt");
    	}
    	else {
    		bufFileName.append("_");
    		bufFileName.append(getTimeStampString());
    		
    		if ( DumpObjectConfig.isDumpXMLForamt() ) {
    			bufFileName.append(".xml");
    		} else {
    			bufFileName.append(".txt");
    		}
    		
    	}
    	return bufFileName.toString();
    }

    private static String getTimeStampString() {
		Calendar cal = new GregorianCalendar();
		
		String strTime = String.format("%02d%02d_%02d%02d_%02d", 
				cal.get(Calendar.MONTH) + 1, 
				cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		
		return strTime;
    }
    private static String getFilePathString() {
		String filePath = "./logs";
 		
 		return filePath;
     }
    

	
    public static Object callMethod(String className, String methodName, Object arglist[]) {
        Object retObj = null;
        try {
            Class cls = Class.forName(className);
            Class partypes[] = new Class[0];
            // partypes[0] = Integer.TYPE;
            // partypes[0] = ArrayList.class;
            // partypes[1] = Integer.TYPE;
            Method meth = cls.getMethod(methodName, partypes);
            // use reflection to instantiate the className
            // Object arglist[] = new Object[2];
            // arglist[0] = new Integer(37);
            // arglist[1] = new Integer(47);
            retObj = meth.invoke(meth, arglist);
            logger.debug("Evaluated value: " + retObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retObj;
    }
	
	public static void main(String args[]) {
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(4);
		//list.add(10);
		Object arglist[] = new Object[1];
		//arglist[0] = new Integer(37);
		arglist[0] = list;
		//arglist[1] = new Integer(47);
		callMethod( "com.paypal.test.platypus.reflection.TestClass", "add", arglist);
	}

}
