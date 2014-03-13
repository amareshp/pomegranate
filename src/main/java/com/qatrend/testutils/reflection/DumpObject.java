package com.qatrend.testutils.reflection;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qatrend.testutils.logging.PLogger;

public class DumpObject {
    private static final Logger logger = PLogger.getLogger();
    
    private static final String OBJ_DUMP = "obj_dump";
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    


    public static void dumpObjects(Map<String, Object> objMap) {
		StringBuffer sb = new StringBuffer();
		
		if (DumpObjectConfig.isDumpXMLForamt()) {
			sb.append(XML_HEADER);
			sb.append("<");
			sb.append(OBJ_DUMP);
			sb.append(">\n");
		}
		ElementProcessor.initObjectFunctionPrametersMap();
		ElementProcessor processor = new ElementProcessor();
		if (objMap != null  ) {
			for ( String key : objMap.keySet()) {
				try {
					if (null != objMap.get(key)) {
						processor.emptyObjStack();
						Element ele = processor.processObject(key, objMap.get(key).getClass().getSimpleName(), objMap.get(key), null);
						ele.dump(sb);
					} else {
						processor.emptyObjStack();
						Element ele = processor.processObject(key, "", null, null);
						ele.dump(sb);
					}
				}
				catch(Exception ex) {
					logger.info("Exception in dumpRBOs:  " + ex.getMessage());
				}
			}
		}
		
		if (DumpObjectConfig.isDumpXMLForamt()) {
			sb.append("</");
			sb.append(OBJ_DUMP);
			sb.append(">\n");
		}
		
		String strFilename = getDumpFileName(OBJ_DUMP);
		try {
			FileOutputStream fos = new FileOutputStream(strFilename);
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			out.write(sb.toString());
			out.close();
		} catch (Exception ex) { 
		    logger.info("Exception in dumpRBOs:  " + ex.getMessage());
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
		String filePath = "logs";
 		
 		return filePath;
     }
}

