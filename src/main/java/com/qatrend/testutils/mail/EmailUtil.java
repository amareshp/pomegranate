package com.qatrend.testutils.mail;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.jsoup.Jsoup;

import com.qatrend.testutils.logging.PLogger;
import com.qatrend.testutils.regex.RegexUtil;
import com.sun.mail.imap.IMAPFolder;

public class EmailUtil {
	private static EmailUtil instance = null;
	private static Store store = null;
	private IMAPFolder folder;
	private static String CONNECTION_TIMEOUT = "20000";
	private static String IMAPServer = "electron.corp.ebay.com";  //"electron.corp.ebay.com";
	private String username = "username";
	private String password = "password";
	private static int IMAPPort = 143;  //IMAP4/SSL = 993, IMAP4 with or without TLS = 143, POP3/SSL = 995, POP3 with or without TLS = 110
	//private static int IMAPPort = 110;
	private static String Protocol = "imap";  //pop3
	//private static String Protocol = "pop3";  //pop3
	private static HashMap<String, String> fusionProps = null;
	private static HashMap<String, String> desktopProps = null;

	public static Store getMailStoreInstance(String username, String password) {
		if (store == null) {
			instance = new EmailUtil(username, password);
		}
		return store;
	}

	public static EmailUtil getMailUtilInstance(String username, String password) {
		if (instance == null) {
			instance = new EmailUtil(username, password);
		}
		return instance;
	}

	protected EmailUtil(String username, String password) {
		this.username = username;
		this.password = password;
		setLocalProperties();
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", Protocol);
		props.setProperty("mail.debug", "false");
		
		props.setProperty("mail.imap.proxyauth.user", username);
		props.setProperty("mail.imap.user", username);
		props.setProperty("mail.imap.host", IMAPServer);
		props.setProperty("mail.imap.port", IMAPPort + "");
		props.setProperty("mail.imap.connectionpooltimeout", CONNECTION_TIMEOUT);
		props.setProperty("mail.imap.connectiontimeout", CONNECTION_TIMEOUT);
		props.setProperty("mail.imap.timeout", CONNECTION_TIMEOUT);

		if (mustUseSSL()) {
			props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
			props.setProperty("mail.imap.socketFactory.fallback", "false");
			props.setProperty("mail.imap.socketFactory.port", "993");
			java.security.Security.setProperty("ssl.SocketFactory.provider",
					SSL_FACTORY);
		}
		try {
			//Session s = Session.getDefaultInstance(props, null);
			Session s = Session.getInstance(props, null);
			store = s.getStore(Protocol);
			store.connect(IMAPServer, IMAPPort, username, password);
		} catch (Exception ex) {
			PLogger.getLogger().debug( "Exception: " + ex.getClass().getName()
					+ " Message: " + ex.getMessage());
		}
	}

	public int getMessageCount(String imapFolderName) {
		int numMessages = 0;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			PLogger.getLogger().debug( "Messages waiting: "
					+ this.folder.getMessageCount());
			numMessages = this.folder.getMessageCount();
			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return numMessages;
	}

	public int getNewMessageCount(String imapFolderName) {
		int numMessages = 0;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			PLogger.getLogger().debug( "Messages waiting: "
					+ this.folder.getMessageCount());
			numMessages = this.folder.getNewMessageCount();
			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return numMessages;
	}

	public int getUnreadMessageCount(String imapFolderName) {
		int numMessages = 0;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			PLogger.getLogger().debug( "Messages waiting: "
					+ this.folder.getMessageCount());
			numMessages = this.folder.getUnreadMessageCount();
			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return numMessages;
	}

	private void printMessages(String imapFolderName) {
		int numMessages;
		String body;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			numMessages = this.folder.getMessageCount();
			for (int i = numMessages; i >= 1; i--) {
				PLogger.getLogger().debug( this.folder.getMessage(i).getSentDate() + " Subject: " + this.folder.getMessage(i).getSubject());
				body = getBody(this.folder.getMessage(i));
				PLogger.getLogger().debug( body);
			}
			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printUnreadMessages(String imapFolderName) {
		int numMessages;
		String body;
		Message[] messages;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);  //new emails
			messages = this.folder.search(ft);
			numMessages = messages.length - 1;
			for (int i = numMessages; i >= 0; i--) {
				PLogger.getLogger().debug(  messages[i].getSentDate() + " Subject: " + messages[i].getSubject());
				body = getBody(messages[i]);
				//PLogger.getLogger().debug( body);
			}

			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	//prints emails received in past 24 hours.
	private void printMessages(String imapFolderName, int daysOld) {
		int numMessages;
		String body;
		Message[] messages;
		Calendar calendar = Calendar.getInstance();
		daysOld = -1 * daysOld;
		calendar.add(Calendar.DATE, daysOld);
		Date fromDt = calendar.getTime();
		Date receivedDt;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			messages = this.folder.getMessages();
			numMessages = messages.length - 1;
			//print most recent emails first
			for (int i = numMessages; i >= 0; i--) {
				receivedDt = messages[i].getSentDate();
				if(receivedDt.before(fromDt)) {
					break;
				}
				PLogger.getLogger().debug(  messages[i].getSentDate() + " Subject: " + messages[i].getSubject());
				body = getBody(messages[i]);
				//PLogger.getLogger().debug( body);
			}

			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	//prints emails received in past N minutes.
	private void printMessagesRecivedLastNMin(String imapFolderName, int minutes) {
		int numMessages;
		String body;
		Message[] messages;
		Calendar calendar = Calendar.getInstance();
		//int hours = minutes/60;
		//minutes = minutes % 60;
		//hours = -1 * hours;
		minutes = -1 * minutes;
		//calendar.add(Calendar.HOUR, hours);
		calendar.add(Calendar.MINUTE, minutes);
		Date fromDt = calendar.getTime();
		Date receivedDt;
		int badTimeStampCount = 0, maxBadTimeStampCount = 15;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			messages = this.folder.getMessages();
			numMessages = messages.length - 1;
			//print most recent emails first
			for (int i = numMessages; i >= 0; i--) {
				receivedDt = messages[i].getReceivedDate();
				if(receivedDt.before(fromDt)) {
					badTimeStampCount++;
					PLogger.getLogger().debug(  receivedDt + " Subject: " + messages[i].getSubject());
					if(badTimeStampCount > maxBadTimeStampCount) {
						break;
					}
					continue;	
				}
				else {
					badTimeStampCount = 0;
				}
				
				PLogger.getLogger().debug(  receivedDt + " Subject: " + messages[i].getSubject());
				//body = getBody(messages[i]);
				//PLogger.getLogger().debug( body);
			}

			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	
	private void printNewMessages(String imapFolderName) {
		Message[] messages;
		// Message message;
		int numMessages;
		Multipart multipart;
		BodyPart bodyPart;
		String content;
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			numMessages = this.folder.getNewMessageCount();
			for (int i = numMessages; i >= 1; i--) {
				PLogger.getLogger().debug( this.folder.getMessage(i).getSubject());
				//PLogger.getLogger().debug( this.folder.getMessage(i).getContentType());
				//PLogger.getLogger().debug( this.folder.getMessage(i).getDisposition());
				//PLogger.getLogger().debug( this.folder.getMessage(i).getContent());
				//PLogger.getLogger().debug( 	((Message)this.folder.getMessage(i))  );
				// this.folder.getMessage(i).writeTo(System.out);
				multipart = (Multipart) this.folder.getMessage(i).getContent();
				bodyPart = multipart.getBodyPart(0);
				content = (bodyPart.getContent().toString());
				content = html2text(content);
				PLogger.getLogger().debug( content);
			}

			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	private String searchFirstOccurrenceOfTextInUnreadMessages(String imapFolderName, String regex) {
		Message[] messages, sortedMessages;
		// Message message;
		int numMessages;
		Multipart multipart = null;
		BodyPart bodyPart;
		String content = null;
		String foundText = "", subject, body;
		MessageSentDateComparator comparator = new MessageSentDateComparator();
		Date fromDt = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		fromDt = calendar.getTime();
		try {
			this.folder = (IMAPFolder) store.getFolder(imapFolderName); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			messages = this.folder.getMessages();
			
			//Arrays.sort(messages, comparator);
			//numMessages = this.folder.getUnreadMessageCount();
			numMessages = this.folder.getMessageCount();
			//for (int i = numMessages; i >= 1; i--) {
			for (int i = numMessages; i >= 1; i--) {
				//PLogger.getLogger().debug( "Date received: " + this.folder.getMessage(i).getSentDate() );
				if(this.folder.getMessage(i).getSentDate().before(fromDt)){
					break;
				}
				//PLogger.getLogger().debug( this.folder.getMessage(i).getSubject());
				subject = this.folder.getMessage(i).getSubject();
				
				//PLogger.getLogger().debug( this.folder.getMessage(i).getContentType());
				//PLogger.getLogger().debug( this.folder.getMessage(i).getDisposition());
				//PLogger.getLogger().debug( this.folder.getMessage(i).getContent());
				//PLogger.getLogger().debug( 	((Message)this.folder.getMessage(i))  );
				// this.folder.getMessage(i).writeTo(System.out);
//				multipart = (Multipart) this.folder.getMessage(i).getContent();
//				bodyPart = multipart.getBodyPart(0);
//				content = (bodyPart.getContent().toString());
//				content = html2text(content);
//				body = content;
				//PLogger.getLogger().debug( content);
				body = getBody(this.folder.getMessage(i));
				if(RegexUtil.checkPatternExists(regex, subject)) {
					foundText = RegexUtil.getMatchedStr(regex, subject);
					PLogger.getLogger().debug( "subject: " + subject);
					PLogger.getLogger().debug( "body: " + body);
					//break;
				}
				if(RegexUtil.checkPatternExists(regex, body)) {
					foundText = RegexUtil.getMatchedStr(regex, body);
					PLogger.getLogger().debug( "subject: " + subject);
					PLogger.getLogger().debug( "body: " + body);
					//break;
				}
			}

			this.folder.close(true);
		} catch (Exception e) {
			PLogger.getLogger().debug( "Exception: " + e.getClass().getName() + " Message: " + e.getMessage());
			e.printStackTrace();
		}
		return foundText;
	}
	
	public String searchInboxLastNDays(String searchForRegex, int days) {
		int numMessages;
		String body, subject, foundStr = "";
		Message[] messages;
		Calendar calendar = Calendar.getInstance();
		days = -1 * days;
		calendar.add(Calendar.DATE, days);
		Date fromDt = calendar.getTime();
		Date receivedDt;
		int badTimeStampCount = 0, maxBadTimeStampCount = 15;
		try {
			this.folder = (IMAPFolder) store.getFolder("Inbox"); // Inbox
			// inbox.open(Folder.READ_WRITE);
			this.folder.open(Folder.READ_ONLY);
			messages = this.folder.getMessages();
			numMessages = messages.length - 1;
			//print most recent emails first
			for (int i = numMessages; i >= 0; i--) {
				receivedDt = messages[i].getReceivedDate();
				if(receivedDt.before(fromDt)) {
					//badTimeStampCount++;
					//PLogger.getLogger().debug(  receivedDt + " Subject: " + messages[i].getSubject());
					//if(badTimeStampCount > maxBadTimeStampCount) {
						break;
					//}
					//continue;	
				}
				//else {
					//badTimeStampCount = 0;
				//}
				subject = messages[i].getSubject();
				body = getBody(messages[i]);
				//PLogger.getLogger().debug(  receivedDt + " Subject: " + subject);
				//PLogger.getLogger().debug( body);
				if(RegexUtil.checkPatternExists(searchForRegex, subject)){
					foundStr = RegexUtil.getMatchedStr(searchForRegex, subject);
					PLogger.getLogger().debug( "Found text pattern: " + searchForRegex + " in Subject of the email. Text found: " + foundStr);
					break;
				}
				if(RegexUtil.checkPatternExists(searchForRegex, body)){
					foundStr = RegexUtil.getMatchedStr(searchForRegex, body);
					PLogger.getLogger().debug( "Found text pattern: " + searchForRegex + " in Body of the email. Text found: " + foundStr);
					break;
				}
			}

			this.folder.close(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return foundStr;
	}

	public String searchInboxLastNMins(String searchForRegex, int minutes) {
		int numMessages;
		String body, subject = "", foundStr = "";
		Message[] messages;
		Calendar calendar = Calendar.getInstance();
		minutes = -1 * minutes;
		calendar.add(Calendar.MINUTE, minutes);
		Date fromDt = calendar.getTime();
		Date receivedDt = null;
		int badTimeStampCount = 0, maxBadTimeStampCount = 15;
		try {
			this.folder = (IMAPFolder) store.getFolder("Inbox"); // Inbox
			this.folder.open(Folder.READ_ONLY);
			messages = this.folder.getMessages();
			numMessages = messages.length - 1;
			//print most recent emails first
			for (int i = numMessages; i >= 0; i--) {
				try {
					receivedDt = messages[i].getReceivedDate();
					if(receivedDt.before(fromDt)) {
						badTimeStampCount++;
						//PLogger.getLogger().debug(  receivedDt + " Subject: " + messages[i].getSubject());
						if(badTimeStampCount > maxBadTimeStampCount) {
							break;
						}
						continue;	
					}
					else {
						badTimeStampCount = 0;
					}
					subject = messages[i].getSubject();
					body = getBody(messages[i]);
					//PLogger.getLogger().debug(  receivedDt + " Subject: " + subject);
					//PLogger.getLogger().debug( body);
					if(RegexUtil.checkPatternExists(searchForRegex, subject)){
						foundStr = RegexUtil.getMatchedStr(searchForRegex, subject);
						PLogger.getLogger().debug( "Subject: " + subject);
						PLogger.getLogger().debug( "Found text pattern: " + searchForRegex + " in Subject of the email. Text found: " + foundStr);
						break;
					}
					if(RegexUtil.checkPatternExists(searchForRegex, body)){
						foundStr = RegexUtil.getMatchedStr(searchForRegex, body);
						PLogger.getLogger().debug( "Body: \n" + body);
						PLogger.getLogger().debug( "Found text pattern: " + searchForRegex + " in Body of the email. Text found: " + foundStr);
						break;
					}
				}
				catch (MessageRemovedException mre) {
					PLogger.getLogger().debug( "Exception: " + mre.getClass().getName() + " Message: " + mre.getMessage());
				}
			
			}

			this.folder.close(true);
		}
		catch(MessagingException me) {
			PLogger.getLogger().debug(  receivedDt + " Subject: " + subject);
			PLogger.getLogger().debug( "Exception: " + me.getClass().getName() + " Message: " + me.getMessage());
			me.printStackTrace();
		}
		catch (Exception e) {
			PLogger.getLogger().debug( "Exception: " + e.getClass().getName() + " Message: " + e.getMessage());
			e.printStackTrace();
		}
		return foundStr;
	}
	
	
	private static boolean mustUseSSL() {
		return false;
		//return true;
	}
	
	private static String html2text(String html) {
//		String[] lines = html.split("\n");
		String text = "";
//		for(String line : lines) {
//			line = Jsoup.parse(line).text();
//			text += line + "\n";
//		}
		text = Jsoup.parse(html.replaceAll("(?i)<br[^>]*>", "br2n1818")).text();
		text = text.replaceAll("br2n1818", "\n");
	    //return Jsoup.parse(html).text();
		return text;
	}	

	private static String getBody(Message message) {
		String body = "";
		Multipart multipart = null;
		BodyPart bodyPart;
		String content = null;
		try {
			if(message.getContentType().contains("text/html")) {
				try {
					multipart = (Multipart) message.getContent();
					bodyPart = multipart.getBodyPart(0);
					content = (bodyPart.getContent().toString());
					content = html2text(content);
					body = content;
				}
				catch(ClassCastException ccex) {
					//PLogger.getLogger().debug( "Exception: " + ccex.getClass().getName() + " Message: " + ccex.getMessage());
					content = html2text(message.getContent().toString());
					body = content;
				}
				
			}
			if(message.getContentType().contains("text/multipart")) {
				multipart = (Multipart) message.getContent();
				bodyPart = multipart.getBodyPart(0);
				content = (bodyPart.getContent().toString());
				content = html2text(content);
				body = content;
			}
			if(message.getContentType().contains("text/plain")) {
				body = message.getContent().toString();
			}
		}
		catch(Exception ex) {
			PLogger.getLogger().debug( "Exception: " + ex.getClass() + " Message: " + ex.getMessage());
			ex.printStackTrace();
		}
		return body;
	}

	private void setLocalProperties(){
		String systemUserName = System.getProperty("user.name");
		System.setProperty("user.name", this.username);
		//fusionProps = (HashMap<String, String>)System.getenv();
		//desktopProps = new HashMap<String, String>();
		
		//desktopProps.put("USERDNSDOMAIN", "CORP.EBAY.COM");
		//desktopProps.put("USERDOMAIN", "CORP");
		//desktopProps.put("USERNAME", this.username);
		//fusionProps.putAll(desktopProps);
		
	}

}
