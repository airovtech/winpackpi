package test;
/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2012. 3. 16.
 * =========================================================
 * Copyright (c) 2012 ManinSoft, Inc. All rights reserved.
 */

public class GetSimpleArray {

	public String[] getStringArray(String arg, String arg2) throws Exception {
		
		System.out.println("WEB SERVICE ARGUMENT : " + arg + "," + arg2);
		String[] result = new String[5];
		result[0] = "������";
		result[1] = "������";
		result[2] = "������";
		result[3] = "������";
		result[4] = "�̼���";
		
		return result;
	}
	public String getString() throws Exception {
		
		return "getString result!";
	}
	public int getInteger() throws Exception {
		return 10000;
	}
	public String getFullString(String arg) throws Exception {
		return arg + " �Դϴ�.";
	}
}
