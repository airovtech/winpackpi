/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2013. 10. 29.
 * =========================================================
 * Copyright (c) 2013 ManinSoft, Inc. All rights reserved.
 */

package util;

public class DateUtil {
	public static boolean IsLeapYear(int a) {
		// 윤년 체크
		if (a % 4 == 0 && a % 100 != 4 || a % 400 == 0)
			return true;
		else
			return false;
	}
	public static int GetDaysOfMonth(int year, int month) {
		// 날짜수 체크
		int day = 0;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12)
			day = 31;
		if (month == 2) {
			if (!IsLeapYear(year))
				day = 28;
			else
				day = 29;
		}
		if (month == 4 || month == 6 || month == 9 || month == 11)
			day = 30;
		return day;
	}
}
