package com.qatrend.pomegranate.mail;

import java.util.Date;

public class Email {
	public String subject;
	public Date receivedTime;
	public Email(String mailSubject, Date received_time){
		subject = mailSubject;
		receivedTime = received_time;
	}

}
