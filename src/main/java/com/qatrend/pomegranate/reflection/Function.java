package com.qatrend.pomegranate.reflection;

import java.util.List;


public class Function {
	private String functionName;
	private List<Parameter> parameters;
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
}

