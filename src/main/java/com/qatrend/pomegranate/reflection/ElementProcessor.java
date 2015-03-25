package com.qatrend.pomegranate.reflection;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.qatrend.pomegranate.logging.PLogger;



/**
 * The purpose of ElementProcessor is to get its value and call its child element 
 * @author ralu
 *
 */
public class ElementProcessor {
    private static final Logger logger = PLogger.getLogger();
    private static final HashSet<Class<?>> PRIMITIVE_TYPES = getPrimitiveTypes();
    private static final HashSet<String> METHORDS_IGNORED = getMethodsIgnored();
    private static final HashSet<String> OBJECTS_IGNORED = getObjectsIgnored();
    private static final String VALUE_NULL = "null";
    private static final String VALUE_CIRCULAR = "circular";
    private static final String VALUE_SKIPPED = "skipped";
    private static final String VALUE_EMPTY = "";
    private static ObjectFunctionParametersMap FUNCTION_WITH_PARAMETERS_MAP = null;
    private final Stack<Object> objStack = new Stack<Object>();
    
    public void emptyObjStack() { 
    	objStack.removeAllElements();
    }
 
    public static void initObjectFunctionPrametersMap() { 
    	FUNCTION_WITH_PARAMETERS_MAP = getObjectFunctionParametersMap();
    }
    
    private static HashSet<Class<?>> getPrimitiveTypes()
    {
        HashSet<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(BigInteger.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(Date.class);
        ret.add(String.class);
        ret.add(Currency.class);
        return ret;
    }
    private static HashSet<String> getMethodsIgnored()
    {
        HashSet<String> ret = new HashSet<String>();
        ret.add("isEmpty");
        ret.add("isListCapped");
        
        return ret;
    }
    private static HashSet<String> getObjectsIgnored()
    {
        HashSet<String> ret = new HashSet<String>();
        // AccountRBO uses AccountProxy which works with ClassLoader directly
        // and messes up reflection
        ret.add("AccountRBO");
        
        return ret;
    }
    public static boolean isObjectIgnored(String className)
    {
        return OBJECTS_IGNORED.contains(className);
    }
    public static boolean isMethodIgnored(String method)
    {
        return METHORDS_IGNORED.contains(method);
    }
    
    public static boolean isPrimitiveType(Class<?> clazz)
    {
        return PRIMITIVE_TYPES.contains(clazz);
    }
    private static FunctionList getFunctionList(String className)
    {
    	if ( FUNCTION_WITH_PARAMETERS_MAP != null)
    		return FUNCTION_WITH_PARAMETERS_MAP.getFunctionParametersMap().get(className);
    	else
    		return null;
    }
    private static Object getParameter(String type, String value) {
    	Object obj = null;
    	try 
    	{
    		if (  type.equals("string") || type.equals("String"))
    			obj = value;
    		else if ( type.equals("byte") || type.equals("Byte"))
    			obj = Byte.valueOf(value);
       		else if ( type.equals("short") || type.equals("Short"))
    			obj = Short.valueOf(value);
       		else if ( type.equals("int") || type.equals("Integer"))
    			obj = Integer.valueOf(value);
    		else if ( type.equals("long") || type.equals("Long"))
    			obj = Long.valueOf(value);
    		else if ( type.equals("BigInteger"))
    			obj = BigInteger.valueOf(Long.valueOf(value));
    		else if ( type.equals("float") || type.equals("Float"))
    			obj = Float.valueOf(value);
    		else if ( type.equals("double") || type.equals("Double"))
    			obj = Double.valueOf(value);
    		else if ( type.equals("boolean") || type.equals("Boolean"))
    			obj = Boolean.valueOf(value);
    		else if ( type.equals("char") || type.equals("Character"))
    			obj = Character.valueOf(value.charAt(0));
    		else if ( type.equals("String...")) {
    			obj = value.split(",");
    		}
    		else
    			logger.info("ElementProcessor.getParameter: type=" + type + ", not supported");
    		
    	}
    	catch(Exception ex)
    	{
			logger.info("Exception calling ElementProcessor.getParameter: type=" + type + ", value=" + value);
    	}
    	return obj;
    }
    
    // Use reflection to dump all the fields
	@SuppressWarnings("unchecked")
	 public Element processObject(String objName, String objType, Object obj, String parameters) {
		Element element = new Element();
		element.setName(objName);
		element.setType(objType);
		element.setParameters(parameters);
		
		if (obj == null ) {
			element.setValue(VALUE_NULL);
			return element;
		}
		
		if (objStack.search(obj) != -1 ) {
			element.setValue(VALUE_CIRCULAR);
			return element;
		}
			
		if ( isObjectIgnored(obj.getClass().getSimpleName())) {
			element.setValue(VALUE_SKIPPED);
			return element;
		}
		if (isPrimitiveType(obj.getClass())) {
			element.setValue(obj.toString());
			return element;
		}
		
		objStack.push(obj);
		
		if ( hasGetter(obj) == false )
		{
			if ( isCollection(obj)) {
				processCollection(obj, element);
			} else {
				element.setValue(obj.toString());
			}
			objStack.pop();
			return element;
		}
		
		// This is the function call list for this object 
		FunctionList functionWithParameters = getFunctionList(objType);
		
		// Ok, this is a class with public void getters so put them into a child
		Method[] methods = obj.getClass().getDeclaredMethods();
		for ( Method method : methods) {
			// Only interested in get and is menthods
			if ( (  method.getName().startsWith("get") == false && 
					method.getName().startsWith("is") == false && 
					method.getName().startsWith("exists") == false) ||
					isMethodIgnored(method.getName()))
				continue;
			
			if ( (method.getModifiers() & Modifier.PUBLIC) == 0 || 
					true == method.getReturnType().equals(Void.TYPE) )
				continue;
			
			// This is the function call list for this method 
			List<Function> listFunctionCalls = null;
			if (  method.getParameterTypes().length > 0 ) {
				if ( functionWithParameters != null )
					listFunctionCalls = functionWithParameters.find(method.getName(), method.getParameterTypes().length);
				
				if ( listFunctionCalls == null || listFunctionCalls.isEmpty())
					continue;
				else
					processFunctionWithParameters(obj, method, listFunctionCalls, element);
			}
			else
			{
				if (method.getReturnType().isArray())
				{
					Object[] retArray = null;
					try {
						if (method.invoke(obj) != null)
							retArray =  (Object[])method.invoke(obj); 
					}
					catch(Exception ex) {
						logger.info("Exception calling " + method.getName() + ": " + ex.getMessage());
					}
					if (retArray == null )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, null));
					else {
						if (retArray.length == 0 )
							element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, null));
						else {
							Element childEle = new Element();
							childEle.setName(method.getName());
							childEle.setType(method.getReturnType().getSimpleName());
							for (Object ret : retArray ) {
								childEle.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, null));
							}
							
							element.addChildElement(childEle);
						}
							
					}
				}
				else if ( List.class.isAssignableFrom(method.getReturnType())) {
					List<? extends Object> retList = null;
					try {
						if (method.invoke(obj) != null)
							retList =  (List<? extends Object>)method.invoke(obj); 
					}
					catch(Exception ex) {
						logger.info("Exception calling " + method.getName() + ": " + ex.getMessage());
					}
					if (retList == null )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, null));
					else {
						if (retList.isEmpty() )
							element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, null));
						else {
							Element childEle = new Element();
							childEle.setName(method.getName());
							childEle.setType(method.getReturnType().getSimpleName());
							for (Object ret : retList ) {
								childEle.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, null));
							}
							
							element.addChildElement(childEle);
						}
					}
				}
				else if ( Set.class.isAssignableFrom(method.getReturnType())) {
					Set<? extends Object> retSet = null;
					try {
						if (method.invoke(obj) != null)
							retSet =  (Set<? extends Object>)method.invoke(obj); 
					}
					catch(Exception ex) {
						logger.info( "Exception calling " + method.getName() + ": " + ex.getMessage());
					}
					if (retSet == null )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, null));
					else {
						if (retSet.isEmpty() )
							element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, null));
						else {
							Element childEle = new Element();
							childEle.setName(method.getName());
							childEle.setType(method.getReturnType().getSimpleName());
							for (Object ret : retSet ) {
								childEle.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, null));
							}
							
							element.addChildElement(childEle);
						}
					}
				}
				else if ( Map.class.isAssignableFrom(method.getReturnType())) {
					Map<?, ? extends Object> retMap = null;
					try {
						if (method.invoke(obj) != null)
							retMap =  (Map<?, ? extends Object>)method.invoke(obj); 
					}
					catch(Exception ex) {
						logger.info( "Exception calling " + method.getName() + ": " + ex.getMessage());
					}
					if (retMap == null )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, null));
					else {
						if (retMap.isEmpty() )
							element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, null));
						else {
							Element childEle = new Element();
							childEle.setName(method.getName());
							childEle.setType(method.getReturnType().getSimpleName());
	
							for (Object key : retMap.keySet() ) {
								if ( null != retMap.get(key))
									childEle.addChildElement(processObject(key.toString(), retMap.get(key).getClass().getSimpleName(), retMap.get(key), null));
							}
							
							element.addChildElement(childEle);
						}
							
					}
				}
				else 
				{
					 Object ret = null;
					 try {
						 ret = method.invoke(obj); 
						 element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), ret, null));
					 }
					 // If the invoked method throws exception, it will be wrapped into InvocationTargetException
					 catch (InvocationTargetException ex) { 
						Throwable cause = ex.getCause();
						if ( null != cause ) {
							logger.info( "Exception calling " + obj.getClass().getSimpleName() + "." + method.getName() + ": " + cause.getMessage());
						} else {
							logger.info( "Exception calling " + obj.getClass().getSimpleName() + "." + method.getName() + ": null cause" );
						}
						
					 }
					 catch (Exception ex) {
						logger.info( "Exception calling " + obj.getClass().getSimpleName() + "." + method.getName() + ": " + ex.getMessage());
					 }
					 
					
				}
			}
		}
		objStack.pop();	
		return element;
	}
	private void processFunctionWithParameters(Object obj, Method method, List<Function> listFunctionCalls, Element element)
	{
		for ( Function func : listFunctionCalls)
		{
			StringBuffer sb = new StringBuffer();
			Object[] args = new Object[func.getParameters().size()];
			int n = 0;
			for ( Parameter para : func.getParameters())
			{
				args[n] = getParameter(para.getType(), para.getValue());
				sb.append(para.getValue());
				if ( n < func.getParameters().size() -1 )
					sb.append(",");
				n++;
			}
			String strParameters = sb.toString();
			
			if (method.getReturnType().isArray())
			{
				Object[] retArray = null;
				try {
					if (method.invoke(obj, args) != null)
						retArray =  (Object[])method.invoke(obj, args); 
				}
				catch(Exception ex) {
					logger.info( "Exception calling " + method.getName() + ": " + ex.getMessage());
				}
				if (retArray == null )
					element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, strParameters));
				else {
					if (retArray.length == 0 )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, strParameters));
					else {
						Element childEle = new Element();
						childEle.setName(method.getName());
						childEle.setType(method.getReturnType().getSimpleName());
						for (Object ret : retArray ) {
							childEle.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, strParameters));
						}
						
						element.addChildElement(childEle);
					}
						
				}
			}
			else if ( List.class.isAssignableFrom(method.getReturnType())) {
				List<? extends Object> retList = null;
				try {
					if (method.invoke(obj, args) != null)
						retList =  (List<? extends Object>)method.invoke(obj, args); 
				}
				catch(Exception ex) {
					logger.info( "Exception calling " + method.getName() + ": " + ex.getMessage());
				}
				if (retList == null )
					element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, strParameters));
				else {
					if (retList.isEmpty() )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, strParameters));
					else {
						Element childEle = new Element();
						childEle.setName(method.getName());
						childEle.setType(method.getReturnType().getSimpleName());
						for (Object ret : retList ) {
							childEle.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, strParameters));
						}
						
						element.addChildElement(childEle);
					}
				}
			}
			else if ( Set.class.isAssignableFrom(method.getReturnType())) {
				Set<? extends Object> retSet = null;
				try {
					if (method.invoke(obj, args) != null)
						retSet =  (Set<? extends Object>)method.invoke(obj, args); 
				}
				catch(Exception ex) {
					logger.info( "Exception calling " + method.getName() + ": " + ex.getMessage());
				}
				if (retSet == null )
					element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, strParameters));
				else {
					if (retSet.isEmpty() )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, strParameters));
					else {
						Element childEle = new Element();
						childEle.setName(method.getName());
						childEle.setType(method.getReturnType().getSimpleName());
						for (Object ret : retSet ) {
							childEle.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, strParameters));
						}
						
						element.addChildElement(childEle);
					}
				}
			}
			else if ( Map.class.isAssignableFrom(method.getReturnType())) {
				Map<?, ? extends Object> retMap = null;
				try {
					if (method.invoke(obj, args) != null)
						retMap =  (Map<?, ? extends Object>)method.invoke(obj, args); 
				}
				catch(Exception ex) {
					logger.info( "Exception calling " + method.getName() + ": " + ex.getMessage());
				}
				if (retMap == null )
					element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), null, strParameters));
				else {
					if (retMap.isEmpty() )
						element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), VALUE_EMPTY, strParameters));
					else {
						Element childEle = new Element();
						childEle.setName(method.getName());
						childEle.setType(method.getReturnType().getSimpleName());
	
						for (Object key : retMap.keySet() ) {
							if ( null != retMap.get(key))
								childEle.addChildElement(processObject(key.toString(), retMap.get(key).getClass().getSimpleName(), retMap.get(key), strParameters));
						}
						
						element.addChildElement(childEle);
					}
						
				}
			}
			else 
			{
				 Object ret = null;
				 try {
					 ret = method.invoke(obj, args); 
					 element.addChildElement(processObject(method.getName(), method.getReturnType().getSimpleName(), ret, strParameters));
				 }
				 // If the invoked method throws exception, it will be wrapped into InvocationTargetException
				 catch (InvocationTargetException ex) { 
					Throwable cause = ex.getCause();
					if ( null != cause ) {
						logger.info( "Exception calling " + method.getName()  + ": " + cause.getMessage());
					} else {
						logger.info( "Exception calling " + method.getName() + ": null cause" );
					}
					
				 }
				 catch (Exception ex) {
					logger.info( "Exception calling " + method.getName() +  ": " + ex.getMessage());
				 }
			}
		}
	
	}

	private static boolean isCollection(Object obj) {
		return obj.getClass().isArray() ||
				List.class.isAssignableFrom(obj.getClass()) ||
				Set.class.isAssignableFrom(obj.getClass()) ||
				Map.class.isAssignableFrom(obj.getClass());
	}
	private void processCollection(Object obj, Element element) {
		
		if (obj.getClass().isArray())
		{
			Object[] retArray = (Object[])obj;
			if (retArray.length == 0 )
				element.setValue(VALUE_EMPTY);
			else {
				
				for (Object ret : retArray ) {
					element.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, null));
				}
			}
		}
		else if ( List.class.isAssignableFrom(obj.getClass())) {
			List<? extends Object> retList = (List<? extends Object>)obj;
			if (retList.isEmpty() )
				element.setValue(VALUE_EMPTY);
			else {
				for (Object ret : retList ) {
					element.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, null));
				}
			}
		}
		else if ( Set.class.isAssignableFrom(obj.getClass())) {
			Set<? extends Object> retSet = (Set<? extends Object>)obj;
			if (retSet.isEmpty() )
				element.setValue(VALUE_EMPTY);
			else {
				for (Object ret : retSet ) {
					element.addChildElement(processObject(changeTypeToName(ret.getClass().getSimpleName()), ret.getClass().getSimpleName(), ret, null));
				}
			}
		}
		else if ( Map.class.isAssignableFrom(obj.getClass())) {
			Map<?, ? extends Object> retMap = (Map<?, ? extends Object>)obj;
			if (retMap.isEmpty() )
				element.setValue(VALUE_EMPTY);
			else {
				for (Object key : retMap.keySet() ) {
					if ( null != retMap.get(key))
						element.addChildElement(processObject(key.toString(), retMap.get(key).getClass().getSimpleName(), retMap.get(key), null));
				}
		
			}
		}
	}
	
	
	private static boolean hasGetter(Object obj)  {
		Method[] methods = obj.getClass().getDeclaredMethods();
		for ( Method method : methods) {
			if ( method.getName().startsWith("get") || method.getName().startsWith("is") ) {
				if ( method.getParameterTypes().length == 0 && 
						(method.getModifiers() & Modifier.PUBLIC) != 0 && 
						false == method.getReturnType().equals(Void.TYPE) && false == isMethodIgnored(method.getName())) {
							return true;
				}
			}
		}
		
		return false;
	}
	private static String changeTypeToName(String type) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < type.length(); i++) {
			if (i > 0 && i < (type.length() -1) &&  
					(type.charAt(i) >= 'A' && type.charAt(i) <= 'Z') &&
					(type.charAt(i+1) < 'A' || type.charAt(i+1) > 'Z') )
				sb.append("_");
			
			sb.append(type.charAt(i));
		}
		
		return sb.toString().toLowerCase();
	}
	private static ObjectFunctionParametersMap getObjectFunctionParametersMap() {
		ObjectFunctionParametersMap objectFunctionParametersMap = null;

		String beanFile = "src/main/resources/FunctionParametersBean.xml";
 		
		// Check if the beanFile exists
		File f = new File(beanFile);
		if ( f.exists() ) {
	 		try {
	 			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(beanFile);
	 			
	 			objectFunctionParametersMap = (ObjectFunctionParametersMap)context.getBean("objectFunctionParameters");
	 		} 
	 		catch(BeansException ex) {
				logger.info( "Exception in  getObjectFunctionPrametersMap" + ": " + ex.getMessage());
	 		}
		}
 		
 		return objectFunctionParametersMap;
     }

}
