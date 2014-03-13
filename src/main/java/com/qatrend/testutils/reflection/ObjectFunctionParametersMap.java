package com.qatrend.testutils.reflection;

import java.util.Map;

public class ObjectFunctionParametersMap {
	private Map<String, FunctionList> functionParametersMap;
	
	public Map<String, FunctionList> getFunctionParametersMap() {
		return functionParametersMap;
	}

	public void setFunctionParametersMap(
			Map<String, FunctionList> functionParametersMap) {
		this.functionParametersMap = functionParametersMap;
	}
}

