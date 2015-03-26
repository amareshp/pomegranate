package com.qatrend.pomegranate.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import com.qatrend.pomegranate.reflection.ReflectionUtil;
import com.qatrend.pomegranate.regex.RegexUtil;

public class XmlUtil {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());

    public static Document stringToDoc(String srcXmlString) {
        Document doc = null;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false); // never forget this!
            DocumentBuilder builder = null;
            builder = domFactory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(srcXmlString.getBytes()));
        } catch (Exception ex) {
            logger.error(ex);
        }
        return doc;
    }

    public static Object execXpathGetNodeList(String srcXmlString, String xPath) {
        Object result = null;
        try {
            Document doc = stringToDoc(srcXmlString);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xPath);
            result = expr.evaluate(doc, XPathConstants.NODESET);
        } catch (Exception ex) {
            logger.error(ex);
        }
        return result;
    }

    public static Object execXpathGetNode(String srcXmlString, String xPath) {
        Object result = null;
        try {
            Document doc = stringToDoc(srcXmlString);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xPath);
            result = expr.evaluate(doc, XPathConstants.NODE);
        } catch (Exception ex) {
            logger.error(ex);
        }
        return result;
    }

    public static String getValueXPath(String srcXmlString, String xPath) {
        String value = null;
        try {
            Object result = execXpathGetNode(srcXmlString, xPath);
            Node node = (Node) result;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                value = node.getTextContent();
            } else {
                value = node.getNodeValue();
            }
            logger.debug(xPath + " = " + value);
        } catch (Exception ex) {
            logger.error(ex.getMessage() + " Could not extract any value using xpath: " + xPath);
        }
        return value;
    }

    public String retrieveAttributeValue(Document document, String xpath, String attribute) {
        String attributeValue = null;
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression xPathExpression = xPath.compile(xpath + "/" + attribute);
            attributeValue = "" + xPathExpression.evaluate(document, XPathConstants.STRING);
        } catch (Exception ex) {
            logger.error(ex);
        }
        return attributeValue;
    }

    public static ArrayList<Node> getNodesXPath(String srcXmlString, String xPath) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false); // never forget this!
        Document doc = null;
        DocumentBuilder builder = null;
        ArrayList<Node> nodesList = new ArrayList<Node>();
        try {
            builder = domFactory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(srcXmlString.getBytes()));
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xPath);

            Object result = expr.evaluate(doc, XPathConstants.NODESET);

            NodeList xPathNodes = (NodeList) result;
            logger.debug("xpath result count: " + xPathNodes.getLength());
            // iterate through all the nodes
            for (int i = 0; i < xPathNodes.getLength(); i++) {
                nodesList.add(xPathNodes.item(i));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return nodesList;
    }

    public static String setValueXPath(String srcXmlString, String xPath, String newVal) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false); // never forget this!
        int i, j;
        Document doc = null;
        DocumentBuilder builder = null;
        try {
            builder = domFactory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(srcXmlString.getBytes()));
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xPath);

            Object result = expr.evaluate(doc, XPathConstants.NODESET);

            NodeList xPathNodes = (NodeList) result;
            logger.debug("xpath result count: " + xPathNodes.getLength());
            logger.debug(xPathNodes.item(0).getNodeName() + " = " + xPathNodes.item(0).getTextContent());

            // get list of all nodes in doc
            NodeList nodes = doc.getElementsByTagName("*");
            // iterate through all the nodes
            for (i = 0; i < xPathNodes.getLength(); i++) {
                // for each node in xpath result - traverse through all nodes in
                // doc to find match
                for (j = 0; j < nodes.getLength(); j++) {
                    if (nodes.item(j).isSameNode(xPathNodes.item(i))) {
                        logger.debug("Old value " + i + ": " + xPathNodes.item(i).getNodeName() + " = " + xPathNodes.item(i).getTextContent());
                        nodes.item(j).setTextContent(newVal);
                        logger.debug("New value " + i + ": " + xPathNodes.item(i).getNodeName() + " = " + xPathNodes.item(i).getTextContent());
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            // ex.printStackTrace();
        }
        return getW3CXmlFromDoc(doc);
    }

    public static String getAttribute(String xmlStr, String tagName, String attrName) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false); // never forget this!
        Document doc = null;
        DocumentBuilder builder = null;
        String value = null;

        try {
            builder = domFactory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
            // Get the root element
            Node rootNode = doc.getFirstChild();
            NodeList nodeList = doc.getElementsByTagName(tagName);
            if ((nodeList.getLength() == 0) || (nodeList.item(0).getAttributes().getNamedItem(attrName) == null)) {
                logger.error("Either node " + tagName + " or attribute " + attrName + " not found.");
            } else {
                value = nodeList.item(0).getAttributes().getNamedItem(attrName).getNodeValue();
                logger.debug("value of " + tagName + " attribute: " + attrName + " = " + value);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return value;
    }

    public static String setAttribute(String xmlStr, String tagName, String attrName, String newValue) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false); // never forget this!
        int i, j;
        Document doc = null;
        DocumentBuilder builder = null;

        StringWriter stringOut = new StringWriter();
        try {
            builder = domFactory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
            // Get the root element
            Node rootNode = doc.getFirstChild();
            NodeList nodeList = doc.getElementsByTagName(tagName);
            if ((nodeList.getLength() == 0) || (nodeList.item(0).getAttributes().getNamedItem(attrName) == null)) {
                logger.error("Either node " + tagName + " or attribute " + attrName + " not found.");
            } else {
                logger.debug("value of " + tagName + " attribute: " + attrName + " = "
                        + nodeList.item(0).getAttributes().getNamedItem(attrName).getNodeValue());
                nodeList.item(0).getAttributes().getNamedItem(attrName).setNodeValue(newValue);

                // write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(stringOut);
                // StreamResult result = new StreamResult(new File(filepath));
                transformer.transform(source, result);
                logger.debug("Updated XML: \n" + stringOut.toString());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return stringOut.toString();
    }

    private static String getW3CXmlFromDoc(Document doc) {
        String xmlString = null;
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            // transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // yes,
            // no
            transformer.setOutputProperty(OutputKeys.INDENT, "no");

            // initialize StreamResult with File object to save to file
            StreamResult xmlStream = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, xmlStream);
            xmlString = xmlStream.getWriter().toString();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return xmlString;
    }

    public static String replaceTokens(String xmlStr) {
        StringBuffer sb = new StringBuffer();
        String[] tokens = xmlStr.split("\\s");
        String tokenToModify = null, token = null;

        for (int i = 0; i < tokens.length; i++) {
            token = tokens[i];
            tokenToModify = null;
            // tokenToModify is set to null or the matched fullyqualified java
            // path - package.class.method
            tokenToModify = RegexUtil.getMatch(token, "java:([\\w.]+)");
            if (tokenToModify != null) {
                String className = tokenToModify.substring(0, tokenToModify.lastIndexOf("."));
                String methodName = tokenToModify.substring(tokenToModify.lastIndexOf(".") + 1);
                tokenToModify = (String) ReflectionUtil.callMethod(className, methodName, null);
                token = token.replaceFirst("(\\$\\{java:([\\w.]+)\\})", tokenToModify);
            }
            sb.append(token);
            sb.append(" ");
        }
        logger.info(sb.toString());
        return sb.toString();
    }

    public static void printNodesAndAttributes(String xmlStr) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            logger.info("Xml processing:");
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream inStream = new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8));
            // or InputSource inputSource = new InputSource( new StringReader(
            // xmlStr ) );
            Document doc = db.parse(inStream);
            DocumentTraversal dt = (DocumentTraversal) doc;
            NodeIterator i = dt.createNodeIterator(doc, NodeFilter.SHOW_ELEMENT, null, false);
            Node node = i.nextNode();
            while (node != null) {
                logger.info("Node type: " + node.getNodeType() + " Node name: " + node.getNodeName());
                logger.info("    Attributes: " + attributesStr(node));
                node = i.nextNode();
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static Node getNode(String xmlStr, String nodeName, Map<String, String> attributesMap) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Node returnNode = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream inStream = new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8));
            // or InputSource inputSource = new InputSource( new StringReader(
            // xmlStr ) );
            Document doc = db.parse(inStream);
            DocumentTraversal dt = (DocumentTraversal) doc;
            NodeIterator i = dt.createNodeIterator(doc, NodeFilter.SHOW_ELEMENT, null, false);
            Node node = i.nextNode();
            while (node != null) {
                if (node.getNodeName().equals(nodeName)) {
                    if (attributesExist(node, attributesMap)) {
                        returnNode = node;
                        break;
                    }
                }
                node = i.nextNode();
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return returnNode;
    }

    public static Node getNode(Node parentNode, String nodeName, Map<String, String> attributesMap) {
        Node returnNode = null;
        try {
            DocumentTraversal dt = (DocumentTraversal) parentNode.getOwnerDocument();
            NodeIterator i = dt.createNodeIterator(parentNode, NodeFilter.SHOW_ELEMENT, null, false);
            Node node = i.nextNode();
            while (node != null) {
                if (node.getNodeName().equals(nodeName)) {
                    if (attributesExist(node, attributesMap)) {
                        returnNode = node;
                        break;
                    }
                }
                node = i.nextNode();
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return returnNode;
    }

    public static ArrayList<Node> getNodeList(String xmlStr, String nodeName, Map<String, String> attributesMap) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ArrayList<Node> returnNodeList = new ArrayList<Node>();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream inStream = new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8));
            // or InputSource inputSource = new InputSource( new StringReader(
            // xmlStr ) );
            Document doc = db.parse(inStream);
            DocumentTraversal dt = (DocumentTraversal) doc;
            NodeIterator i = dt.createNodeIterator(doc, NodeFilter.SHOW_ELEMENT, null, false);
            Node node = i.nextNode();
            while (node != null) {
                if (node.getNodeName().equals(nodeName)) {
                    if (attributesExist(node, attributesMap)) {
                        returnNodeList.add(node);
                    }
                }
                node = i.nextNode();
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return returnNodeList;
    }

    public static ArrayList<Node> getNodeList(Node parentNode, String nodeName, Map<String, String> attributesMap) {
        ArrayList<Node> returnNodeList = new ArrayList<Node>();
        try {
            DocumentTraversal dt = (DocumentTraversal) parentNode.getOwnerDocument();
            NodeIterator i = dt.createNodeIterator(parentNode, NodeFilter.SHOW_ELEMENT, null, false);
            Node node = i.nextNode();
            while (node != null) {
                if (node.getNodeName().equals(nodeName)) {
                    if (attributesExist(node, attributesMap)) {
                        returnNodeList.add(node);
                    }
                }
                node = i.nextNode();
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return returnNodeList;
    }

    public static boolean attributesExist(Node node, Map<String, String> attributesMap) {
        boolean exists = true;
        Map<String, String> nodeAttributes = new HashMap<String, String>();
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            nodeAttributes.put(node.getAttributes().item(i).getNodeName(), node.getAttributes().item(i).getNodeValue());
        }

        if (attributesMap == null) {
            return exists;
        }

        for (String attr : attributesMap.keySet()) {
            if (nodeAttributes.get(attr) == null) {
                exists = false;
                break;
            }
            if (!nodeAttributes.get(attr).equals(attributesMap.get(attr))) {
                exists = false;
                break;
            }
        }

        return exists;
    }

    public static String attributesStr(Node node) {
        if (node == null) {
            return null;
        }
        StringBuffer attributes = new StringBuffer();
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            attributes.append(node.getAttributes().item(i).getNodeName() + "=" + node.getAttributes().item(i).getNodeValue() + ", ");
        }
        if (attributes.length() > 1) {
            attributes.delete(attributes.length() - 2, attributes.length());
        } else {
            attributes.append(node.getNodeName() + " has NO attributes.");
        }
        return attributes.toString();
    }

    public static void printNodeList(NodeList nodeList) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            buffer.append(node.getNodeName() + " = " + node.getNodeValue() + " Attributes: " + attributesStr(node) + "\n");
        }
        logger.info(buffer);
    }

    public static void printNodeList(ArrayList<Node> nodeList) {
        StringBuffer buffer = new StringBuffer();
        for (Node node : nodeList) {
            buffer.append(node.getNodeName() + " = " + node.getNodeValue() + " Attributes: " + attributesStr(node) + "\n");
        }
        logger.info(buffer);
    }

    public static String assignDynamicValues(String inputXML, Map<String, String> dynamicValues) {
        for (String key : dynamicValues.keySet()) {
            if (inputXML.contains("{" + key + "}")) {
                inputXML = inputXML.replaceAll("\\{" + key + "\\}", dynamicValues.get(key));
            }
        }
        return inputXML;
    }

    public static String assignDynamicValue(String input, String dynamicKey, String dynamicValue) {
        if (input.contains("{" + dynamicKey + "}")) {
            input = input.replaceAll("\\{" + dynamicKey + "\\}", dynamicValue);
        }
        return input;
    }

}
