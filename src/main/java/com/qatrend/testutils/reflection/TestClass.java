package com.qatrend.testutils.reflection;

import java.util.ArrayList;

public class TestClass {
	public int add(int a, int b) {
		return a+b;
	}

//	public int add(int a) {
//		return a;
//	}

	//	public int add(ArrayList<Integer> list) {
//		int sum = 0;
//		for(Integer i : list) {
//			sum += i.intValue();
//		}
//		return sum;
//	}

	public int add(ArrayList<Object> list) {
		int sum = 0;
		for(Object i : list) {
			sum += ((Integer)i).intValue();
		}
		return sum;
	}
	
}
