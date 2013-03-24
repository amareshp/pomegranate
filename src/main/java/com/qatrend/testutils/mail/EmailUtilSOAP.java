package com.qatrend.testutils.mail;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.qatrend.testutils.soap.SOAPResponse;
import com.qatrend.testutils.soap.SOAPUtil;
import com.qatrend.testutils.system.SystemUtil;
import com.qatrend.testutils.xml.XmlUtil;


public class EmailUtilSOAP {
	public static String getMails(int n){
		String resMsg = null, reqMsg = null;
		String username = null, password = null;
		String msgCount = "" + n;
		try{
			username = SystemUtil.getProperty("user.name");
			password = SystemUtil.getProperty("CORP_PWD");
			if(password == null) {
				String userHome = SystemUtil.getProperty("user.home");
				String propsFilePath = userHome + "/passwordless.properties";
				password = SystemUtil.getPropertyFromFile(propsFilePath, "CORP_PWD");
			}
			reqMsg = FileUtils.readFileToString(new File("src/test/resources/soap/getMailsReq.xml") );
			reqMsg = XmlUtil.setAttribute(reqMsg, "m:IndexedPageItemView", "MaxEntriesReturned", msgCount);
			SOAPResponse response = SOAPUtil.sendSOAPMsgWithBasicAuth(SOAPUtil.EWS_URL, reqMsg, username, password);
			resMsg = response.getResponseText();
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getStackTrace()[0].getMethodName() + ": " + ex.getMessage());
		}
		return resMsg;
	}

	public static String getRMMails(int n){
		String resMsg = null, reqMsg = null;
		String username = null, password = null;
		String msgCount = "" + n;
		try{
			username = SystemUtil.getProperty("user.name");
			password = SystemUtil.getProperty("CORP_PWD");
			if(password == null) {
				String userHome = SystemUtil.getProperty("user.home");
				String propsFilePath = userHome + "/passwordless.properties";
				password = SystemUtil.getPropertyFromFile(propsFilePath, "CORP_PWD");
			}
			reqMsg = FileUtils.readFileToString(new File("src/test/resources/soap/getRMMailsReq.xml") );
			reqMsg = XmlUtil.setAttribute(reqMsg, "m:IndexedPageItemView", "MaxEntriesReturned", msgCount);
			SOAPResponse response = SOAPUtil.sendSOAPMsgWithBasicAuth(SOAPUtil.EWS_URL, reqMsg, username, password);
			resMsg = response.getResponseText();
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getStackTrace()[0].getMethodName() + ": " + ex.getMessage());
		}
		return resMsg;
	}

	public static String getRMMails(File reqFile, int fetchCount){
		String resMsg = null, reqMsg = null;
		String username = null, password = null;
		String msgCount = "" + fetchCount;
		try{
			username = SystemUtil.getProperty("user.name");
			password = SystemUtil.getProperty("CORP_PWD");
			if(password == null) {
				String userHome = SystemUtil.getProperty("user.home");
				String propsFilePath = userHome + "/passwordless.properties";
				password = SystemUtil.getPropertyFromFile(propsFilePath, "CORP_PWD");
			}
			reqMsg = FileUtils.readFileToString( reqFile );
			reqMsg = XmlUtil.setAttribute(reqMsg, "m:IndexedPageItemView", "MaxEntriesReturned", msgCount);
			SOAPResponse response = SOAPUtil.sendSOAPMsgWithBasicAuth(SOAPUtil.EWS_URL, reqMsg, username, password);
			resMsg = response.getResponseText();
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getStackTrace()[0].getMethodName() + ": " + ex.getMessage());
		}
		return resMsg;
	}
	
	
}
