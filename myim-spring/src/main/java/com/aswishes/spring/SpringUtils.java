package com.aswishes.spring;

public class SpringUtils {

	public static boolean contain(String[] arr, String v) {
    	if (arr == null || arr.length < 1 || v == null) {
    		throw new IllegalStateException("The source array or value is null");
    	}
    	for (String s : arr) {
    		if (v.equals(s)) {
    			return true;
    		}
    	}
    	return false;
    }
}
