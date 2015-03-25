package com.qatrend.pomegranate.collection;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.qatrend.pomegranate.collection.CollectionUtil;
import com.qatrend.pomegranate.logging.PLogger;

@Test
public class CollectionUtilTest {
	
	@Test(groups="pomegranate")
	public void test1(){
		CollectionUtil cUtil = new CollectionUtil();
		List<Object> objList = new ArrayList<Object>();
		objList.add("cat");
		objList.add("dog");
		objList.add("lion");
		objList.add("tiger");
		String objListStr = cUtil.getStringFromList(objList, "~~");
		PLogger.getLogger().info(objListStr);
		Assert.assertTrue(objListStr.endsWith("tiger"), "The string did not end with the last element in the list.");
	}

}
