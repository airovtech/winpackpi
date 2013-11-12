package test;
/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2012. 9. 10.
 * =========================================================
 * Copyright (c) 2012 ManinSoft, Inc. All rights reserved.
 */

public class getCodeNums {

	public String getCodeNum(String spliter, String arg1, String arg2, String arg3, String arg4) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		sb.append(arg1).append(spliter);
		sb.append(arg2).append(spliter);
		sb.append(arg3).append(spliter);
		sb.append(arg4).append(spliter);
		
		return sb.toString();
		
	}
}
