package com.qatrend.testutils.mail;

import java.util.Comparator;
import java.util.Date;

import javax.mail.Message;

public class MessageSentDateComparator implements Comparator {
	public int compare(Object msg1, Object msg2) {
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = ((Message)msg1).getSentDate();
			date2 = ((Message)msg2).getSentDate();
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
		if(date1.after(date2)) {
			return -1;
		}
		else {
			return 0;
		}
	}
}
