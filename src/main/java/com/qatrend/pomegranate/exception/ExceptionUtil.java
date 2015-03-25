package com.qatrend.pomegranate.exception;

public class ExceptionUtil {
	public static String getBriefExceptionMsg(Exception ex){
		String className = ex.getStackTrace()[0].getClassName();
		String methodName = ex.getStackTrace()[0].getMethodName();
		int lineNo = ex.getStackTrace()[0].getLineNumber();
		String exMsg = "Exception in Class: " + className + " Method: " + methodName + " Line: " + lineNo;
		exMsg = exMsg + "\n" + "Exception message: " + ex.getMessage();
		return exMsg;
	}

}
