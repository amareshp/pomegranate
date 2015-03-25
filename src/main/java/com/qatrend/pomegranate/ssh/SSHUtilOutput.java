package com.qatrend.pomegranate.ssh;

public class SSHUtilOutput {
	private String outputTxt = "";
	private int returnCode = 0;
	
	public String getOutputTxt(){
		return outputTxt;
	}
	public void setOutputTxt(String txt){
		outputTxt = txt;
	}
	public int getReturnCode(){
		return returnCode;
	}
	public void setReturnCode(int code){
		returnCode = code;
	}

}
