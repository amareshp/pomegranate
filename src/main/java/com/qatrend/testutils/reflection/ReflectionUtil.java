package com.qatrend.testutils.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectionUtil {

	public static void callMethod(String className, String methodName, Object arglist[]) {
		try {
			Class cls = Class.forName( className );
			Class partypes[] = new Class[1];
			//partypes[0] = Integer.TYPE;
			partypes[0] = ArrayList.class;
			//partypes[1] = Integer.TYPE;
			Method meth = cls.getMethod( methodName, partypes );
			com.qatrend.testutils.reflection.TestClass methobj = new com.qatrend.testutils.reflection.TestClass();
			//use reflection to instantiate the className
			//Object arglist[] = new Object[2];
			//arglist[0] = new Integer(37);
			//arglist[1] = new Integer(47);
			Object retobj = meth.invoke( methobj, arglist);
			Integer retval = (Integer) retobj;
			System.out.println(retval.intValue());
		} 
//		catch (Throwable e) {
//			System.err.println(e);
//		}
		catch (Exception e) {
			//System.err.println(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(4);
		//list.add(10);
		Object arglist[] = new Object[1];
		//arglist[0] = new Integer(37);
		arglist[0] = list;
		//arglist[1] = new Integer(47);
		callMethod( "com.paypal.test.platypus.reflection.TestClass", "add", arglist);
	}

}
