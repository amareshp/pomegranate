package com.qatrend.pomegranate.reflection;

import java.util.ArrayList;
import java.util.List;

public class FunctionList {
	private List<Function> functionList;
	
	public List<Function> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List<Function> functionList) {
		this.functionList = functionList;
	}
	
	public List<Function> find(String funcName, int nParams)  {
		 List<Function> smallList = new ArrayList<Function>();
		 
		 for(Function func : functionList) {
			 if ( func.getFunctionName().equals(funcName) && func.getParameters().size() == nParams )
				 smallList.add(func);
		 }
		 return smallList;
	}
}
