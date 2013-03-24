package com.qatrend.testutils.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.qatrend.testutils.logging.PLogger;
import com.qatrend.testutils.regex.RegexUtil;
import com.sun.xml.bind.StringInputStream;


public class XmlUtil {
	public boolean printOnce = true;
	private static int depth = 0;
	private static int nodeDepth = 0;

	public static void printValueXPath(String xmlFilePath, String xpathStr)  
	throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse( xmlFilePath );

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr 
		= xpath.compile( xpathStr );

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			PLogger.getLogger().debug( nodes.item(i).getNodeValue()); 
		}

	}

	public static ArrayList<String> getValuesXPath(String xmlStr, String xpathStr)  
	throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException {
		ArrayList<String> xpathValues = new ArrayList<String>();
		String value = "";
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xmlStr));
		Document doc = builder.parse( is );

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr 
		= xpath.compile( xpathStr );

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			value = nodes.item(i).getNodeValue();
			//PLogger.getLogger().debug(  value );
			xpathValues.add(value);
		}
		return xpathValues;
	}

	public static String getValueXPathFromXmlStr(String xmlStr, String xpathStr)  
	throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException {
		String value = "";
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xmlStr));
		Document doc = builder.parse( is );

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr 
		= xpath.compile( xpathStr );

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		value = nodes.item(0).getNodeValue();
		return value;
	}
	

	public static String getValueXPath(String xmlFilePath, String xpathStr)  
	throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException {

		String xpathVal;
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse( xmlFilePath );

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr 
		= xpath.compile( xpathStr );

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		return nodes.item(0).getNodeValue();
	}

	public static ArrayList<String> execXPath(String xmlStr, String xpathStr)  
	throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException {

		ArrayList<String> xpathResult = new ArrayList<String>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		//Document doc = builder.parse( xmlFilePath );
		Document doc = builder.parse(xmlStr);

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr 
		= xpath.compile( xpathStr );

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for(int i=0; i<nodes.getLength(); i++) {
			xpathResult.add(nodes.item(i).getNodeValue());
		}
		return xpathResult;

	}
	
	
	public static void setNodeValue(String xmlFilePath, String nodeName, String attrName, String attrVal, String tmpFilePath)  
	throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException {

		String xpathVal;
		int i, j;
		RegexUtil reUtil = new RegexUtil();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse( xmlFilePath );
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile( "/" );
		

		Object result = expr.evaluate(doc, XPathConstants.STRING);
		

		NodeList nodes = doc.getElementsByTagName("*");
		//iterate through all the nodes
		for(i=0; i<nodes.getLength(); i++) {
			//PLogger.getLogger().debug( "Node name - " + nodes.item(i).getNodeName() + " = " + nodes.item(i).getTextContent() );
			NamedNodeMap nodeMap = nodes.item(i).getAttributes();
			for(j=0; j<nodeMap.getLength(); j++) {
				if( reUtil.checkPatternExists("splitMOD", nodeMap.item(j).getNodeValue()  )   ) {      // (nodeMap.item(j).getNodeName() == "name") && (nodeMap.item(j).getNodeValue() != "xxx")
					PLogger.getLogger().debug( "Attributes - " + nodeMap.item(j).getNodeName() + " = " + nodeMap.item(j).getNodeValue() + " = " + nodes.item(i).getTextContent());
					nodes.item(i).setTextContent("4");
					
				}
				//PLogger.getLogger().debug( "Attributes - " + nodeMap.item(j).getNodeName() + " = " + nodeMap.item(j).getNodeValue());
			}
		}
		
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");

			//initialize StreamResult with File object to save to file
			StreamResult xmlStream = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, xmlStream);
			String xmlString = xmlStream.getWriter().toString();
			PLogger.getLogger().debug( xmlString);		
			
			File file = new File( tmpFilePath );
			FileUtils.writeStringToFile(file, xmlString );
			
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	public static void printW3CXML(org.w3c.dom.Document doc, String tmpFilePath) {
		//
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // yes, no
			transformer.setOutputProperty(OutputKeys.INDENT, "no");

			//initialize StreamResult with File object to save to file
			StreamResult xmlStream = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, xmlStream);
			String xmlString = xmlStream.getWriter().toString();
			//PLogger.getLogger().debug( xmlString);		
			
			File file = new File( tmpFilePath );
			FileUtils.writeStringToFile(file, xmlString );
		}
		catch (Exception ex) {
			PLogger.getLogger().debug( ex.getMessage());
		}
	}

	public static String getW3CXmlFromDoc(org.w3c.dom.Document doc) {
		String xmlString = null;
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // yes, no
			transformer.setOutputProperty(OutputKeys.INDENT, "no");

			//initialize StreamResult with File object to save to file
			StreamResult xmlStream = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, xmlStream);
			xmlString = xmlStream.getWriter().toString();
			//PLogger.getLogger().debug( xmlString);		
		}
		catch (Exception ex) {
			PLogger.getLogger().debug( ex.getMessage());
		}
		
		return xmlString;
	}
	
	public static void printW3CXMLWithIndent(org.w3c.dom.Document doc, String tmpFilePath) {
		//
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // yes, no
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file
			StreamResult xmlStream = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, xmlStream);
			String xmlString = xmlStream.getWriter().toString();
			//PLogger.getLogger().debug( xmlString);		
			
			File file = new File( tmpFilePath );
			FileUtils.writeStringToFile(file, xmlString );
		}
		catch (Exception ex) {
			PLogger.getLogger().debug( ex.getMessage());
		}
	}
	
	
	public static void printW3CXML(org.w3c.dom.Document doc) {
		//
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file
			StreamResult xmlStream = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, xmlStream);
			String xmlString = xmlStream.getWriter().toString();
			PLogger.getLogger().debug( xmlString);		
		}
		catch (Exception ex) {
			PLogger.getLogger().debug( ex.getMessage());
		}
	}
	
	
	public static void setValueXPath(String srcFile, String xPath, String newVal, String tgtFile) {
		//
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(false); // never forget this!
		int i, j;
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse( srcFile );
			//Document doc = builder.parse( "tmp/test.xml" );
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile( xPath );

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			
			NodeList xPathNodes = (NodeList) result;
			PLogger.getLogger().debug( "xpath result count: " + xPathNodes.getLength());
			PLogger.getLogger().debug( xPathNodes.item(0).getNodeName() + " = " + xPathNodes.item(0).getTextContent()  );
			
			//get list of all nodes in doc
			NodeList nodes = doc.getElementsByTagName("*");
			//iterate through all the nodes
			for(i=0; i<xPathNodes.getLength(); i++) {
				//for each node in xpath result - traverse through all nodes in doc to find match
				for(j=0; j<nodes.getLength(); j++) {
					if(  nodes.item(j).isSameNode(xPathNodes.item(i))  ) {   
						PLogger.getLogger().debug( "Old value " + i + ": " + xPathNodes.item(i).getNodeName() + " = " + xPathNodes.item(i).getTextContent()  );
						nodes.item(j).setTextContent(newVal);
						PLogger.getLogger().debug( "New value " + i + ": " + xPathNodes.item(i).getNodeName() + " = " + xPathNodes.item(i).getTextContent()  );
						break;
					}
					
				}
			}
			
			printW3CXML(doc, tgtFile);
			//PLogger.getLogger().debug( "updated xml has been written to: " + tgtFile);
			
		}
		catch (Exception ex) {
			PLogger.getLogger().debug( ex.getMessage());
			//ex.printStackTrace();
		}
		
	}

	public static void xmlInsertNode(String xmlSrcFile, String insertBeforeTagName, String nodeStr, String xmlTgtFile) {
		PLogger.getLogger().debug( "//////////////////////////////////////////////////////////////////////");
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		PLogger.getLogger().debug( "Starting " + methodName + "...");
		PLogger.getLogger().debug( "//////////////////////////////////////////////////////////////////////");
		
		try{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			//domFactory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse( xmlSrcFile );
			XmlUtil xmlUtil = new XmlUtil();
			xmlUtil.printW3CXML(doc, "src/test/resources/tmp/temp.xml");
			Element ele1 = builder.parse( new StringInputStream( nodeStr ) ).getDocumentElement();
			NodeList list1 = doc.getElementsByTagName( insertBeforeTagName );
			doc.setStrictErrorChecking(false);
			doc.insertBefore(ele1, list1.item(0));
			xmlUtil.printW3CXML(doc, "src/test/resources/tmp/temp1.xml");
		}
		catch(Exception ex){
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
		
		
	}
	
	public static String setValueXPath(String inputXmlStr, String xPath, String newVal) {
		String updatedXmlStr = null;
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(false); // never forget this!
		int i, j;
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse( new InputSource(new StringReader(inputXmlStr)) );  
			//Document doc = builder.parse( "tmp/test.xml" );
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile( xPath );

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			
			NodeList xPathNodes = (NodeList) result;
			PLogger.getLogger().debug( "xpath result count: " + xPathNodes.getLength());
			PLogger.getLogger().debug( xPathNodes.item(0).getNodeName() + " = " + xPathNodes.item(0).getTextContent()  );
			
			//get list of all nodes in doc
			NodeList nodes = doc.getElementsByTagName("*");
			//iterate through all the nodes
			for(i=0; i<xPathNodes.getLength(); i++) {
				//for each node in xpath result - traverse through all nodes in doc to find match
				for(j=0; j<nodes.getLength(); j++) {
					if(  nodes.item(j).isSameNode(xPathNodes.item(i))  ) {   
						PLogger.getLogger().debug( "Old value " + i + ": " + xPathNodes.item(i).getNodeName() + " = " + xPathNodes.item(i).getTextContent()  );
						nodes.item(j).setTextContent(newVal);
						PLogger.getLogger().debug( "New value " + i + ": " + xPathNodes.item(i).getNodeName() + " = " + xPathNodes.item(i).getTextContent()  );
						break;
					}
					
				}
			}
			
			updatedXmlStr = getW3CXmlFromDoc(doc);
			//PLogger.getLogger().debug( "updated xml: \n" + updatedXmlStr);
			
		}
		catch (Exception ex) {
			PLogger.getLogger().debug( ex.getMessage());
			//ex.printStackTrace();
		}
		return updatedXmlStr;
		
	}
	
	public static String setAttribute(String xmlStr, String tagName, String attrName, String newValue){
		StringWriter stringOut = new StringWriter ();
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
			Document doc = docBuilder.parse( is );
			// Get the root element
			Node rootNode = doc.getFirstChild();
			NodeList nodeList = doc.getElementsByTagName(tagName);
			if( (nodeList.getLength() == 0) || (nodeList.item(0).getAttributes().getNamedItem(attrName) == null) ) {
				System.out.println("Either node " + tagName + " or attribute " + attrName + " not found.");
			}
			else{
				System.out.println("value of " + tagName +  " attribute: " + attrName + " = " + nodeList.item(0).getAttributes().getNamedItem("MaxEntriesReturned").getNodeValue());
				nodeList.item(0).getAttributes().getNamedItem("MaxEntriesReturned").setNodeValue(newValue);
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(stringOut);
				//StreamResult result = new StreamResult(new File(filepath));
				transformer.transform(source, result);	
				System.out.println(stringOut.toString());
			}
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		
		return stringOut.toString();
	}
	
	public void printDepth(Node node){
		Node childNode;
		String nodeName;
		String depthStr;
		ArrayList<String> ignoreList = new ArrayList<String>();
		ignoreList.add("#text");
		ignoreList.add("#comment");
		nodeName = node.getNodeName();
		//String format = String.format("%%0%dd", 3);
		if(printOnce == true){
			depthStr = String.format("%03d", depth);
			System.out.println("[" + depthStr + "]" + getSpaces(depth) + nodeName);
			printOnce = false;
		}
		try{
			NodeList nodeList = node.getChildNodes();
			for(int i=0; i<nodeList.getLength(); i++){
				childNode = nodeList.item(i);
				nodeName = childNode.getNodeName();
				if( !ignoreList.contains(nodeName)){
					depth++;
					depthStr = String.format("%03d", depth);
					System.out.println("[" + depthStr + "]" + getSpaces(depth) + nodeName);
					printDepth(childNode);
					depth--;
				}
			}
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	public int getNodeDepth(Node node, String nodeNameToFind){
		Node childNode;
		String nodeName;
		String depthStr;
		ArrayList<String> ignoreList = new ArrayList<String>();
		ignoreList.add("#text");
		ignoreList.add("#comment");
		nodeName = node.getNodeName();
		//String format = String.format("%%0%dd", 3);
		if(printOnce == true){
			depthStr = String.format("%03d", depth);
			System.out.println("[" + depthStr + "]" + getSpaces(depth) + nodeName);
			if(nodeName.equals(nodeNameToFind)) {
				nodeDepth = depth;
				return nodeDepth;
			}
			printOnce = false;
		}
		try{
			NodeList nodeList = node.getChildNodes();
			for(int i=0; i<nodeList.getLength(); i++){
				childNode = nodeList.item(i);
				nodeName = childNode.getNodeName();
				if( !ignoreList.contains(nodeName)){
					depth++;
					depthStr = String.format("%03d", depth);
					System.out.println("[" + depthStr + "]" + getSpaces(depth) + nodeName);
					if(nodeName.equals(nodeNameToFind)) {
						nodeDepth = depth;
						return nodeDepth;
					}
					getNodeDepth(childNode, nodeNameToFind);
					depth--;
				}
			}
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return nodeDepth;
	}
	
	
	private String getSpaces(int n){
		String space = "";
		for(int i=0; i<n; i++){
			space += "  ";
		}
		return space;
	}
	
} 

