package test;
/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2012. 6. 19.
 * =========================================================
 * Copyright (c) 2012 ManinSoft, Inc. All rights reserved.
 */

public class GetString {
	
	public String getSimpleString() throws Exception {
		
		return "getSimpleString";
		
	}
	
	public String[] getSimpleArray() throws Exception {
		
		
		String[] result = new String[3];
		
		result[0] = "result1";
		result[1] = "result2";
		result[2] = "result3";
		
		return result;
		
	}
	
	public String[] getSimpleArray(String arg) throws Exception {
		
		String[] result = new String[3];
		
		result[0] = "result1_" + arg;
		result[1] = "result2_" + arg;
		result[2] = "result3_" + arg;
		
		return result;
		
	}
	
	public String simplePrint(String arg) throws Exception  {
		
		System.out.println(arg);
		
		return arg + " is Good";
		
		
	}
}
