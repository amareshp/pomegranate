package com.qatrend.testutils.reflection;

import java.util.ArrayList;
import java.util.List;
/**
 * Data structure for an element
 * @author ralu
 *
 */
public class Element {
	private static int level = 0;
	private String name = null;
	private String type = null;
	private String value = null;
	private String parameters = null;
	private List<Element> childElements = new ArrayList<Element>();
	
	public static void setLevel(int val) {
		level = level;
	}
	public Element() {
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<Element> getChildElements() {
		return childElements;
	}
	public void setChildElements(List<Element> childElements) {
		this.childElements = childElements;
	}
	public void addChildElement(Element element) {
		childElements.add(element);
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public void dump(StringBuffer sb) {
		if ( DumpObjectConfig.isDumpXMLForamt() ) {
			addTabs(sb);
			sb.append("<");
			sb.append(name);
			if ( parameters != null ) {
				sb.append(" parameters=\"");
				sb.append(parameters);
				sb.append("\"");
			}
			sb.append(" type=\"");
			sb.append(type);
			sb.append("\"");
			sb.append(">");
			if (null != value)
				sb.append(value);
			if ( childElements.size() > 0 ) {
				++level;
				sb.append("\n");
				for (Element ele : childElements)
					ele.dump(sb);
				--level;
				addTabs(sb);
				sb.append("</");
				sb.append(name);
				sb.append(">\n");
			}
			else {
				sb.append("</");
				sb.append(name);
				sb.append(">\n");
			}
		}
		else {
			addTabs(sb);
			sb.append(name);
			sb.append(" ");
			if ( parameters != null ) {
				sb.append(parameters);
				sb.append(" ");
			}
			sb.append(type);
			sb.append(" ");
			if (null != value)
				sb.append(value);
			if ( childElements.size() > 0 ) {
				++level;
				sb.append("\n");
				for (Element ele : childElements)
					ele.dump(sb);
				--level;
			}
			else {
				sb.append("\n");
			}
		}
	}
	public void addTabs(StringBuffer sb) {
		for (int n=0; n< level; n++)
			sb.append("\t");
	}
}

