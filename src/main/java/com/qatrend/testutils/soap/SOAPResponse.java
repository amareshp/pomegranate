package com.qatrend.testutils.soap;

public class SOAPResponse {
	String responseMessage, responseText;
	int responseCode;

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	
	public void printASFResponse() {
		System.out.println("Response code: " + this.getResponseCode() + " Response message: " + this.getResponseMessage());
		System.out.println("Response text: \n" + this.getResponseText());
	}

}
