package com.qatrend.pomegranate.soap;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.qatrend.pomegranate.logging.PLogger;


public class SOAPUtil {
	public static String EWS_URL = "https://yourMailSerter.com/EWS/Exchange.asmx";

	public static SOAPResponse sendSOAPMsg(String urlStr, String reqMsg) {
		SOAPResponse soapResponse = new SOAPResponse();
		String result = "";
		final String username_1 = "";
		final String password_1 = "";
		String username = "";
		String password = "";
		
		try
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{
			    new X509TrustManager() {
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
			
			HostnameVerifier hv = new HostnameVerifier()
			{
				public boolean verify(String urlHostName, SSLSession session)
				{
					PLogger.getLogger().debug( "Warning: URL Host: " + urlHostName + " vs. "
							+ session.getPeerHost());
					return true;
				}
			};			

			// Install the all-trusting trust manager
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    HttpsURLConnection.setDefaultHostnameVerifier(hv);
		    
		    URL url = new URL( urlStr );
		    PLogger.getLogger().debug( "URL: " + url.toURI());
            Authenticator myAuth = new Authenticator() 
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(username_1, password_1.toCharArray());
                }
            };

            Authenticator.setDefault(myAuth);
		    
		    //HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		    HttpURLConnection con = (HttpURLConnection) url.openConnection();
		    String authentication = (new sun.misc.BASE64Encoder()).encode( ( username + ":"+ password).getBytes() );
		    con.setRequestProperty("Authorization", authentication);
		    con.setRequestMethod("POST");
		    //con.setSSLSocketFactory(sslsocketfactory);
		    
		    //set properties
		    System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		    System.setProperty("javax.net.ssl.trustStoreType", "jks");
		    System.setProperty("javax.net.ssl.keyStore", "clientcertificate.p12");
		    System.setProperty("javax.net.ssl.trustStore", "gridserver.keystore");
		    System.setProperty("javax.net.debug", "ssl # very verbose debug");
		    System.setProperty("javax.net.ssl.keyStorePassword", "$PASS");
		    System.setProperty("javax.net.ssl.trustStorePassword", "$PASS");
		    
		    // specify that we will send output and accept input
		    con.setDoInput(true);
		    con.setDoOutput(true);

		    con.setConnectTimeout( 20000 );  // long timeout, but not infinite
		    con.setReadTimeout( 20000 );

		    con.setUseCaches (false);
		    con.setDefaultUseCaches (false);

		    // tell the web server what we are sending
		    con.setRequestProperty ( "Content-Type", "text/xml" );
		    OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
		    //PLogger.getLogger().debug( reqMsg);
		    //JawsLogger.getJawsLogger().log(Level.FINE, reqMsg);
		    PLogger.getLogger().debug( reqMsg);
		    //reqMsg = URLEncoder.encode(reqMsg, "UTF-8");
		    writer.write( reqMsg );
		    writer.flush();
		    //writer.close();

		    // reading the response
		    Thread.sleep(2000);
		    InputStreamReader reader = new InputStreamReader( con.getInputStream() );

		    StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;

		    while ( -1 != (num=reader.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }

		    result = buf.toString();
		    //PLogger.getLogger().debug( "Response code: " + con.getResponseCode() + " Response message: " + con.getResponseMessage());
		    //PLogger.getLogger().debug(  "\nResponse from server after POST:\n" + result );
		    soapResponse.setResponseCode( con.getResponseCode() );
		    soapResponse.setResponseMessage( con.getResponseMessage() );
		    soapResponse.setResponseText(result);
		}
		catch( Throwable t )
		{
		    t.printStackTrace( System.out );
		}
		return soapResponse;
		
	}
	
	
	public static SOAPResponse sendSOAPMsgWithBasicAuth(String urlStr, String reqMsg, String username, String password) {
		SOAPResponse soapResponse = new SOAPResponse();
		String result = "";
		final String username_1 = username;
		final String password_1 = password;
		
		try
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{
			    new X509TrustManager() {
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
			
			HostnameVerifier hv = new HostnameVerifier()
			{
				public boolean verify(String urlHostName, SSLSession session)
				{
					PLogger.getLogger().debug( "Warning: URL Host: " + urlHostName + " vs. "
							+ session.getPeerHost());
					return true;
				}
			};			

			// Install the all-trusting trust manager
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    HttpsURLConnection.setDefaultHostnameVerifier(hv);
		    
		    URL url = new URL( urlStr );
		    PLogger.getLogger().debug( "URL: " + url.toURI());
            Authenticator myAuth = new Authenticator() 
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(username_1, password_1.toCharArray());
                }
            };

            Authenticator.setDefault(myAuth);
		    
		    //HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		    HttpURLConnection con = (HttpURLConnection) url.openConnection();
		    String authentication = (new sun.misc.BASE64Encoder()).encode( ( username + ":"+ password).getBytes() );
		    con.setRequestProperty("Authorization", authentication);
		    con.setRequestMethod("POST");
		    //con.setSSLSocketFactory(sslsocketfactory);
		    
		    //set properties
		    System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		    System.setProperty("javax.net.ssl.trustStoreType", "jks");
		    System.setProperty("javax.net.ssl.keyStore", "clientcertificate.p12");
		    System.setProperty("javax.net.ssl.trustStore", "gridserver.keystore");
		    System.setProperty("javax.net.debug", "ssl # very verbose debug");
		    System.setProperty("javax.net.ssl.keyStorePassword", "$PASS");
		    System.setProperty("javax.net.ssl.trustStorePassword", "$PASS");
		    
		    // specify that we will send output and accept input
		    con.setDoInput(true);
		    con.setDoOutput(true);

		    con.setConnectTimeout( 20000 );  // long timeout, but not infinite
		    con.setReadTimeout( 20000 );

		    con.setUseCaches (false);
		    con.setDefaultUseCaches (false);

		    // tell the web server what we are sending
		    con.setRequestProperty ( "Content-Type", "text/xml" );
		    OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
		    //PLogger.getLogger().debug( reqMsg);
		    //JawsLogger.getJawsLogger().log(Level.FINE, reqMsg);
		    PLogger.getLogger().debug( reqMsg);
		    //reqMsg = URLEncoder.encode(reqMsg, "UTF-8");
		    writer.write( reqMsg );
		    writer.flush();
		    //writer.close();

		    // reading the response
		    Thread.sleep(2000);
		    InputStreamReader reader = new InputStreamReader( con.getInputStream() );

		    StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;

		    while ( -1 != (num=reader.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }

		    result = buf.toString();
		    //PLogger.getLogger().debug( "Response code: " + con.getResponseCode() + " Response message: " + con.getResponseMessage());
		    //PLogger.getLogger().debug(  "\nResponse from server after POST:\n" + result );
		    soapResponse.setResponseCode( con.getResponseCode() );
		    soapResponse.setResponseMessage( con.getResponseMessage() );
		    soapResponse.setResponseText(result);
		}
		catch( Throwable t )
		{
		    t.printStackTrace( System.out );
		}
		return soapResponse;
		
	}
	
	
	private boolean handleMessage(SOAPMessageContext soapMessageContext) {
	     try {
	 
	          boolean outMessageIndicator = (Boolean) soapMessageContext
	                                        .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	          if (outMessageIndicator) {
	 
	              SOAPEnvelope envelope = soapMessageContext.getMessage().getSOAPPart().getEnvelope();
	 
	              SOAPHeader header = envelope.addHeader();
	 
	              SOAPElement security = header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
	 
	              SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
	              usernameToken.addAttribute(new QName("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
	 
	              SOAPElement username = usernameToken.addChildElement("Username", "wsse");
	              username.addTextNode("TestUser");
	 
	              SOAPElement password = usernameToken.addChildElement("Password", "wsse");
	              password.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
	              password.addTextNode("TestPassword");
	 
	         }
	 
	   } catch (Exception ex) {
	     throw new WebServiceException(ex);
	   }
	   return true;
	 }	

}
