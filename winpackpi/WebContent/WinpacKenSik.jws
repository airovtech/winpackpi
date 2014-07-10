import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.JsonUtil;

/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2014. 6. 17.
 * =========================================================
 * Copyright (c) 2014 ManinSoft, Inc. All rights reserved.
 */

public class WinpacKenSik {

	public static final String dbUrl = "jdbc:jtds:sqlserver://193.169.13.35:1433/winpackensik";
	public static final String userId = "altit";
	public static final String pass = "altadmin";
	public static final String driverClassName = "net.sourceforge.jtds.jdbc.Driver";
	
	public static final String TARGETTABLE1 = "altit.ALT_KenDayMst";
	public static final String TARGETTABLE2 = "altit.ALT_SchHuumu";
	
	//스마트웍스닷넷 그리드폼 JSON데이터중 사번에 해당하는 필드 아이디
	public static final String WITHUSERIDENTNOFIELDID = "11";
	
	public static final String USERSELECT_SINGLEUSER = "작성자 본인 휴가";
	public static final String USERSELECT_MULTIUSER = "아니요(다수신청)";
	
	public static final String countTarget1Sql = " select count(*) as cnt from " + TARGETTABLE1 + " where DayDt = ? and IdentNo= ? ";
	public static final String insertTarget1Sql = " insert into " + TARGETTABLE1 + "(DayDt, IdentNo, kenCd) values (?, ?, ?) ";
	public static final String updateTarget1Sql = " update " + TARGETTABLE1 + " set kenCd = ? where DayDt = ? and IdentNo = ? and (fixfx is null or fixfx != 1)";

	public static final String countTarget2Sql = " select count(*) as cnt from " + TARGETTABLE2 + " where DayDt =? and IdentNo=? ";
	public static final String insertTarget2Sql = " insert into " + TARGETTABLE2 + "(DayDt, IdentNo, kenCd) values (?, ?, ?) ";
	public static final String updateTarget2Sql = " update " + TARGETTABLE2 + " set kenCd = ? where DayDt = ? and IdentNo = ?";
	
	public void sendToKenSik(String kentaeCode, String userSelectOption, String userStr, String withUsersStr, String fromDateStr, String toDateStr, String kentaeIlsu) {
		
		System.out.println("START! args : " + kentaeCode + "," + userStr + ","+ withUsersStr + "," + fromDateStr + "," + toDateStr);
		//START! args : 연차,aaaaaa,{"gridDatas":[{"6":"a","7":"p111","8":"b","9":"bi"},{"6":"b","7":"m123","8":"b2","9":"bi2"}]},2014-06-10 19:10:00.000,2014-06-25 23:30:00.000 
		
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection con = null;
		
		
		PreparedStatement countPstmt1 = null;
		PreparedStatement insertPstmt1 = null;
		PreparedStatement updatePstmt1 = null;
		
		PreparedStatement countPstmt2 = null;
		PreparedStatement insertPstmt2 = null;
		PreparedStatement updatePstmt2 = null;
		
		ResultSet count1Rs = null;
		ResultSet count2Rs = null;

		try {
			con = DriverManager.getConnection(dbUrl, userId, pass);
			
			countPstmt1 = con.prepareStatement(countTarget1Sql);
			insertPstmt1 = con.prepareStatement(insertTarget1Sql);
			updatePstmt1 = con.prepareStatement(updateTarget1Sql);

			countPstmt2 = con.prepareStatement(countTarget2Sql);
			insertPstmt2 = con.prepareStatement(insertTarget2Sql);
			updatePstmt2 = con.prepareStatement(updateTarget2Sql);
			
			String[] users = parseUserStr(userSelectOption, userStr, withUsersStr);
			if (users == null) {
				System.out.println("WARN! Not Exist User !!!!!!!!!!!! Break Interfaceing!!!!");
				return;
			}	
			
			String kenCd = parseKenCd(kentaeCode);
			if (kenCd == null) {
				System.out.println("WARN! Not Exist KenTae Code!! : " + kentaeCode + " !!!!!!!!!! Break Interfaceing!!!!");
				return;
			}	
			String startDateStr = parseFromDateStr(fromDateStr);
			String endDateStr = parseToDateStr(toDateStr);
			
			Date startDate = null;
			Date endDate = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
				startDate = sdf.parse(startDateStr);
				endDate = sdf.parse(endDateStr);
			} catch (Exception e) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				startDate = sdf.parse(startDateStr);
				endDate = sdf.parse(endDateStr);
			}
			
			//Date startDate = sdf.parse(startDateStr);
			//Date endDate = sdf.parse(endDateStr);
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(startDate);
			Calendar endCal = Calendar.getInstance();
			
			//야간조 근태는 전날 밤부터 다음날 아침까지기 때문에 하루 근태도 시작날짜와 종료날짜가 다르다
			/*if (kentaeIlsu != null && kentaeIlsu.length() != 0 && Integer.parseInt(kentaeIlsu) <= 1 ) {
				endCal.setTime(startDate);
			} else {
				endCal.setTime(endDate);
			}*/
			try {
				if (kentaeIlsu == null || kentaeIlsu.length() == 0) {
					endCal.setTime(endDate);
				} else if (Integer.parseInt(kentaeIlsu) <= 1) {
					endCal.setTime(startDate);
				} else {
					endCal.setTime(startDate);
					endCal.add(Calendar.DATE, (Integer.parseInt(kentaeIlsu)-1));
				}
			} catch (Exception e) {
				endCal.setTime(endDate);
			}
			
			//해당 날짜가 타겟 데이터베이스 테이블에 있는지를 검사한다.(key : 날짜,사번)
			//날짜 만큼 반복한다.(fromDate ~ toDate)
			for (;startCal.getTimeInMillis() <= endCal.getTimeInMillis(); startCal.add(Calendar.DATE, 1)) {
				StringBuffer dateBuff = new StringBuffer();
				dateBuff.append(startCal.get(Calendar.YEAR)).append("-");
				if (startCal.get(Calendar.MONTH) + 1 < 10)
					dateBuff.append("0");
				dateBuff.append((startCal.get(Calendar.MONTH)+1)).append("-");
				if (startCal.get(Calendar.DATE) < 10)
					dateBuff.append("0");
				dateBuff.append(startCal.get(Calendar.DATE));
				
				//System.out.println(dateBuff.toString());
				
				//사용자 수만큼 반복한다.(users.length) 
				for (int i = 0; i < users.length; i++) {
					String userId = users[i];
					//table1
					//해당 날짜가 타겟 데이터베이스 테이블에 있는지를 검사한다.(key : 날짜,사번)
					countPstmt1.clearParameters();
					countPstmt1.setString(1, dateBuff.toString());
					countPstmt1.setString(2, userId);
					
					count1Rs = countPstmt1.executeQuery();
					
					int totalCount1 = 0;
					if (count1Rs.next()) {
						totalCount1 = count1Rs.getInt("cnt");
					}
					count1Rs.close();
					
					//근태시스템의 근태데이터에 승인이 떨어졌다면(fixfx = 1) 수정을 해서는 안된다.
					//updatePstmt1 에는 승인되지 않은 데이터만 업데이트하게끔 되어 있다
					//따라서 updatePstmt1 의 결과 값에 따라 이후 updatePstmt2도 수행여부가 결정된다
					int updateRowCount = 0;
					//데이터가 존재한다면 update, 아니라면 insert 
					System.out.println("TotalCount1 : " + totalCount1);
					if (totalCount1 > 0) {
						updatePstmt1.clearParameters();
						updatePstmt1.setString(1, kenCd);
						updatePstmt1.setString(2, dateBuff.toString());
						updatePstmt1.setString(3, userId);
						updateRowCount = updatePstmt1.executeUpdate();
						System.out.println("UPDATE DONE!!!!!!!!!!!!!!!!!! by updatePstmt1 UpdateCount : " + updateRowCount);
					} else {
						insertPstmt1.clearParameters();
						insertPstmt1.setString(1, dateBuff.toString());
						insertPstmt1.setString(2, userId);
						insertPstmt1.setString(3, kenCd);
						boolean isInsert = insertPstmt1.execute();
						System.out.println("INSERT DONE!!!!!!!!!!!!!!!!!! by insertPstmt1 " + isInsert);
					}
					
					//table2
					//해당 날짜가 타겟 데이터베이스 테이블에 있는지를 검사한다.(key : 날짜,사번)
					countPstmt2.clearParameters();
					countPstmt2.setString(1, dateBuff.toString());
					countPstmt2.setString(2, userId);
					
					count2Rs = countPstmt2.executeQuery();
					
					int totalCount2 = 0;
					if (count2Rs.next()) {
						totalCount2 = count2Rs.getInt("cnt");
					}
					count2Rs.close();
					
					//데이터가 존재한다면 update, 아니라면 insert 
					System.out.println("TotalCount2 : " + totalCount2);
					if (totalCount2 > 0) {
						if (updateRowCount != 0) {
							updatePstmt2.clearParameters();
							updatePstmt2.setString(1, kenCd);
							updatePstmt2.setString(2, dateBuff.toString());
							updatePstmt2.setString(3, userId);
							boolean isUpdate = updatePstmt2.execute();
							System.out.println("UPDATE DONE!!!!!!!!!!!!!!!!!! by updatePstmt2 " + isUpdate);
						}
					} else {
						insertPstmt2.clearParameters();
						insertPstmt2.setString(1, dateBuff.toString());
						insertPstmt2.setString(2, userId);
						insertPstmt2.setString(3, kenCd);
						boolean isInsert = insertPstmt2.execute();
						System.out.println("INSERT DONE!!!!!!!!!!!!!!!!!! by insertPstmt2 " + isInsert);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			try {count1Rs.close();} catch (SQLException e) {e.printStackTrace();}
			try {count2Rs.close();} catch (SQLException e) {e.printStackTrace();}
			try {countPstmt1.close();} catch (SQLException e) {e.printStackTrace();}
			try {updatePstmt1.close();} catch (SQLException e) {e.printStackTrace();}
			try {insertPstmt1.close();} catch (SQLException e) {e.printStackTrace();}
			try {countPstmt2.close();} catch (SQLException e) {e.printStackTrace();}
			try {updatePstmt2.close();} catch (SQLException e) {e.printStackTrace();}
			try {insertPstmt2.close();} catch (SQLException e) {e.printStackTrace();}
			try {con.close();} catch (SQLException e) {e.printStackTrace();}
		}
	}
	
	private void addWithUser(List userList, String withUserStr) throws Exception {
		
		if (withUserStr.indexOf("gridDatas") == -1)
			return;
		
		//JSON 스트링을 파싱하여 userList에 추가한다
		//EX : {"gridDatas":[{"6":"a","7":"p111","8":"b","9":"bi"},{"6":"b","7":"m123","8":"b2","9":"bi2"}]}
		
		Map withUsersMap = JsonUtil.getMapByJsonString(withUserStr);
		List valueList = (ArrayList)withUsersMap.get("gridDatas");
		for (int i = 0; i < valueList.size(); i++) {
			Map userMap = (Map)valueList.get(i); 
			Iterator itr = userMap.keySet().iterator();
			while(itr.hasNext()) {
				String key = (String)itr.next();
				if (key.equalsIgnoreCase(WITHUSERIDENTNOFIELDID)) {
					String userIdentId = (String)userMap.get(key);
					if (userIdentId != null && userIdentId.length() != 0) {
						userList.add(userMap.get(key));
					}
					break;
				}	
			}
		}
	}
	private String[] parseUserStr(String userSelectOption, String userStr, String withUsersStr) throws Exception {
		
		//userSelectOption : 작성자 휴가 여부( 작성자 본인 휴가, 아니요(다수신청) )
		// 작성자 본인 휴가 -> return userStr
		// 아니요(다수신청) -> return withUserStr
		// null -> return userStr + withUserStr
		
		//usersStr : 신청자
		//withUserStr : 신청자외 다수신청자
		System.out.println("userOption : " + userSelectOption);
		
		if (userSelectOption == null || userSelectOption.length() == 0 || userSelectOption.equalsIgnoreCase("NULL")) {
			List userList = new ArrayList();
			if (userStr != null && userStr.length() != 0) {
				userList.add(userStr);
			}
			addWithUser(userList, withUsersStr);
			String[] result = new String[userList.size()];
			userList.toArray(result);
			return result;
		} else if (userSelectOption.equalsIgnoreCase(USERSELECT_SINGLEUSER)) {
			return new String[]{userStr};
		} else if (userSelectOption.equalsIgnoreCase(USERSELECT_MULTIUSER)) {
			List userList = new ArrayList();
			
			addWithUser(userList, withUsersStr);
			String[] result = new String[userList.size()];
			userList.toArray(result);
			
			return result;
		}
		return null;
	}
	private String parseFromDateStr(String fromDate) throws Exception {
		return fromDate;
	}
	private String parseToDateStr(String toDate) throws Exception {
		return toDate;
	}
	private String parseKenCd(String kenCd) throws Exception {
		
		if (kenCd == null || kenCd.length() == 0)
			return null;
		
		if (kenCd.equalsIgnoreCase("연차")) {
			return "30";
		} else if (kenCd.equalsIgnoreCase("반차(오전)") || kenCd.equalsIgnoreCase("반차(오후)")) {
			return "31";
		} else if (kenCd.equalsIgnoreCase("하계휴가")) {
			return "52";
		} else if (kenCd.equalsIgnoreCase("공가")) {
			return "53";
		} else if (kenCd.equalsIgnoreCase("사외교육") || kenCd.equalsIgnoreCase("법정교육") || kenCd.equalsIgnoreCase("사내교육") || kenCd.equalsIgnoreCase("온라인교육")) {
			return "48";
		} else if (kenCd.equalsIgnoreCase("동원훈련") || kenCd.equalsIgnoreCase("동미참훈련") || kenCd.equalsIgnoreCase("향방훈련") || kenCd.equalsIgnoreCase("민방위훈련")) {
			return "49";
		} else if (kenCd.equalsIgnoreCase("국내출장")) {
			return "46";
		} else if (kenCd.equalsIgnoreCase("해외출장")) {
			return "47";
		} else if (kenCd.equalsIgnoreCase("결혼")) {
			return "35";
		} else if (kenCd.equalsIgnoreCase("출산") || kenCd.equalsIgnoreCase("돌")) {
			return "37";
		} else if (kenCd.equalsIgnoreCase("수연")) {
			return null;
		} else if (kenCd.equalsIgnoreCase("사망")) {
			return "45";
		}
		return null;
	}
	
}
