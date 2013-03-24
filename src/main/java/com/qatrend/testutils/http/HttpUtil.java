package com.qatrend.testutils.http;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.qatrend.testutils.logging.PLogger;




public class HttpUtil {
	
	public static String getHtmlTextRendered(String url) {
		String textDisplayed = "";
		try {
			final WebClient webClient = new WebClient();
			final HtmlPage page = (HtmlPage)webClient.getPage( url );
			textDisplayed = page.getBody().getTextContent();
		}
		catch(Exception ex) {
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
		return textDisplayed;
	}

	public static String getPageAsString(String url, String user, String pwd) {
		String pageAsText = "";
		String pageAsXml = "";
		HtmlElement element;
		try {
			DefaultCredentialsProvider credsProvider = new DefaultCredentialsProvider();
			credsProvider.addCredentials(user, pwd);
			
		    final WebClient webClient = new WebClient();
		    webClient.setCredentialsProvider(credsProvider);
		    webClient.setUseInsecureSSL(true);
		    webClient.setRedirectEnabled(true);
		    //webClient.setRefreshHandler(new WaitingRefreshHandler());
		    webClient.setRefreshHandler(new ThreadedRefreshHandler());


		    final HtmlPage page = (HtmlPage)webClient.getPage( url );
		    if(page.getElementByName("filterText") != null) {
		    	//PLogger.getLogger().debug( "Found filter text textbox");
		    }
		    if(page.getElementByName("creatorText") != null) {
		    	//PLogger.getLogger().debug( "Found creator text textbox");
		    }
		    
		    if( page.getByXPath("//input[@title='Fetch']").size() == 1 ) {
		    	//PLogger.getLogger().debug( "Found the filter button. Clicking on filter ...");
		    }
		    element = (HtmlElement)page.getByXPath("//input[@title='Fetch']").get(0);
		    HtmlPage statusPage = element.click();
		    Thread.sleep(3000);
		    //page.refresh();
		    //page.getB

		    pageAsXml = statusPage.asXml();
		    //PLogger.getLogger().debug( "Page title: " + statusPage.getTitleText());

		    pageAsText = statusPage.asText();
			FileUtils.writeStringToFile(new File("tmp/temp.html"), pageAsText);
		}
		catch(Exception ex) {
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
	    return pageAsText;
	    

		
	}

	public ArrayList<String> execXPath(String url, String xpath, String user, String pwd) {
		ArrayList<String> nodeList = new ArrayList<String>();
		try {
			DefaultCredentialsProvider credsProvider = new DefaultCredentialsProvider();
			credsProvider.addCredentials(user, pwd);
			
		    final WebClient webClient = new WebClient();
		    webClient.setCredentialsProvider(credsProvider);
		    webClient.setUseInsecureSSL(true);
		    webClient.setRedirectEnabled(true);
		    //webClient.setRefreshHandler(new WaitingRefreshHandler());
		    webClient.setRefreshHandler(new ThreadedRefreshHandler());


		    final HtmlPage page = (HtmlPage)webClient.getPage( url );
		    List<HtmlElement> nodes = (List<HtmlElement>)page.getByXPath( xpath );
		    for(int i=0; i<nodes.size(); i++) {
		    	nodeList.add(nodes.get(i).getTextContent() );
		    }
		}
		catch(Exception ex) {
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
	    return nodeList;
	    

		
	}

	public String mapToURL(String hostURL, LinkedHashMap<String, String> paramsMap){
		String url="";
		URI uri;
		URL url1 = null;
		for(String key : paramsMap.keySet() ) {
			url += key + "=" + paramsMap.get(key) + "&";
		}
		url = url.substring(0, url.length()-1);
		url = hostURL + "?" + url;
		
		try {
			uri = new URI(url);
			url1 = uri.toURL();
		}
		catch(Exception ex){
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
		
		return url1.toString();
	}

	public String mapToURL(String protocol, String host, String hostPath, LinkedHashMap<String, String> paramsMap){
		String query="";
		URI uri;
		URL url1 = null;
		for(String key : paramsMap.keySet() ) {
			query += key + "=" + paramsMap.get(key) + "&";
		}
		query = query.substring(0, query.length()-1);
		//url = hostURL + "?" + url;
		
		try {
			uri = new URI(protocol, host, hostPath, query, null);
			url1 = uri.toURL();
		}
		catch(Exception ex){
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
		
		return url1.toString();
	}

	public static String getPageText(String url, String user, String pwd) {
		String pageAsText = "";
		String pageAsXml = "";
		HtmlElement element;
		try {
			DefaultCredentialsProvider credsProvider = new DefaultCredentialsProvider();
			credsProvider.addCredentials(user, pwd);
			
		    final WebClient webClient = new WebClient();
		    webClient.setCredentialsProvider(credsProvider);
		    webClient.setUseInsecureSSL(true);
		    webClient.setRedirectEnabled(true);
		    //webClient.setRefreshHandler(new WaitingRefreshHandler());
		    webClient.setRefreshHandler(new ThreadedRefreshHandler());

		    final HtmlPage page = (HtmlPage)webClient.getPage( url );
		    pageAsXml = page.asXml();
		    pageAsText = page.asText();
			FileUtils.writeStringToFile(new File("tmp/temp.html"), pageAsText);
		}
		catch(Exception ex) {
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
	    return pageAsText;
	}

	public static String getPageText(String url) {
		String pageAsText = "";
		String pageAsXml = "";
		try {
		    final WebClient webClient = new WebClient();
		    final HtmlPage page = (HtmlPage)webClient.getPage( url );
		    pageAsXml = page.asXml();
		    pageAsText = page.asText();
			FileUtils.writeStringToFile(new File("tmp/temp.html"), pageAsText);
		}
		catch(Exception ex) {
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
	    return pageAsText;
	}
	
	public static String html2text(String html) {
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

	public static WebDriver getHtmlUnitDriver() {
		DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
		WebDriver driver = new HtmlUnitDriver(capabilities);
		return driver;
	}

	public static WebDriver getHtmlUnitDriverJSEnabled() {
		DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
		capabilities.setJavascriptEnabled(true);
		WebDriver driver = new HtmlUnitDriver(capabilities); 
		return driver;
	}
	
	
}
