package test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*	
 * $Id$
 * created by    : SHIN HYUN SEONG
 * creation-date : 2011. 8. 17.
 * =========================================================
 * Copyright (c) 2011 ManinSoft, Inc. All rights reserved.
 */

public class InsertDataWS {

	// MSSQL
	public static final String SQL_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String URL="jdbc:sqlserver://localhost:1433;SelectMethod=cursor;DatabaseName=Semiteq_20111222;";
	public static final String USER = "sa";
	public static final String PASS = "maninsoft";

	public InsertDataWS() {
		try {
			Class.forName(SQL_DRIVER);
			System.out.println("**mssql driver loading...**");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String insertToEduTable(String eduNo, String employeeNo, String startDate, String endDate) throws SQLException {
		String sql = "insert into IF_TABLE_EDU (eduNo, employeeNo, startDate, endDate, status) values (?, ?, ?, ?, 'N')";
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USER, PASS);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, eduNo);
			pstmt.setString(2, employeeNo);
			pstmt.setString(3, startDate);
			pstmt.setString(4, endDate);
			int result = pstmt.executeUpdate();
			if(result < 1)
			  System.out.println("데이터 입력 실패");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pstmt.close();
			conn.close();
		}
		return "success";
	}

	public String insertToTnaTable(String tnaNo, String employeeNo, String startDate, String endDate) throws SQLException {
		String sql = "insert into IF_TABLE_TNA (tnaNo, employeeNo, startDate, endDate, status) values (?, ?, ?, ?, 'N')";
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USER, PASS);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tnaNo);
			pstmt.setString(2, employeeNo);
			pstmt.setString(3, startDate);
			pstmt.setString(4, endDate);
			int result = pstmt.executeUpdate();
			if(result < 1)
			  System.out.println("데이터 입력 실패");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pstmt.close();
			conn.close();
		}
		return "success";
	}

}