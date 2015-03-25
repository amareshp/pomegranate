package com.qatrend.testutils.reflection;

import org.testng.annotations.Test;

import com.qatrend.pomegranate.reflection.ReflectionUtil;


@Test
public class DumpTest {
	@Test
	public static void testDump(){
		try{
			PersonBean bean = new PersonBean();
			bean.setAddress("123 Main St");
			bean.setAge(21);
			bean.setCity("Los Angeles");
			bean.setFirstName("Jen");
			bean.setLastName("Smith");
			
			ReflectionUtil.dumpObject(bean);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
