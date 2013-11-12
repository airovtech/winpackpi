/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2013. 3. 13.
 * =========================================================
 * Copyright (c) 2012 ManinSoft, Inc. All rights reserved.
 */

package schedule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import util.DateUtil;

public class dailyCollectingMonthDataJob  extends QuartzJobBean   {

	public final String dbUrl = "jdbc:oracle:thin:@193.169.13.41:1523:grbf";
	public final String userId = "swuser";
	public final String pass = "smartworks";
	public final String driverClassName = "oracle.jdbc.driver.OracleDriver";
	
	public final StringBuffer selectBuff = new StringBuffer()
		.append(" select division, sum(shipping) as sumOfShipping, sum(salesOfDay) as sumOfSales ")
		.append(" from dailyshippingnsales ")
		.append(" where collectingdate >= ? and collectingdate <= ? ")
		.append(" group by division ");
	
	public final StringBuffer deleteBuff = new StringBuffer()
		.append(" delete from monthlyshippingnsales where collectingmonth=? and division=? ");
	
	public final StringBuffer insertBuff = new StringBuffer()
		.append(" insert into monthlyshippingnsales(division, collectingmonth, sumofshipping, sumofsales, updatedate) values(?,?,?,?,?) ");
	
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		try {
			try {
				Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String month = sdf.format(new Date()) + "01";
			
			String selYear = month.substring(0,4);
			String selMonth = month.substring(4,6);
			
			int daycountOfMonth = DateUtil.GetDaysOfMonth(Integer.parseInt(selYear), Integer.parseInt(selMonth));
				
			int fromDate = Integer.parseInt(month);
			int toDate = Integer.parseInt(month) + daycountOfMonth -1;
			
			PreparedStatement pstmt = null;
			PreparedStatement deletePstmt = null;
			PreparedStatement insertPstmt = null;
			Connection con = null;
			ResultSet rs = null;
			
			try {
				con = DriverManager.getConnection(dbUrl, userId, pass);
				pstmt = con.prepareStatement(selectBuff.toString());
				deletePstmt = con.prepareStatement(deleteBuff.toString());
				insertPstmt = con.prepareStatement(insertBuff.toString());
				pstmt.setString(1, fromDate+"");
				pstmt.setString(2, toDate+"");
				rs = pstmt.executeQuery();
				while (rs.next()) {
					String division = rs.getString("division");
					String sumOfShipping = rs.getString("sumOfShipping");
					String sumOfSales = rs.getString("sumOfSales");
					
					deletePstmt.setString(1, month);
					deletePstmt.setString(2, division);
					
					deletePstmt.addBatch();
					deletePstmt.clearParameters();
					
					insertPstmt.setString(1, division);
					insertPstmt.setString(2, month);
					insertPstmt.setString(3, sumOfShipping);
					insertPstmt.setString(4, sumOfSales);
					insertPstmt.setString(5, Calendar.getInstance().get(Calendar.MINUTE) + "");
					
					insertPstmt.addBatch();
					insertPstmt.clearParameters();
					
				}
				
				System.out.println("delete Pstmt : " + deletePstmt.executeBatch().length);
				System.out.println("insert Pstmt : " + insertPstmt.executeBatch().length);
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					pstmt.close();
					deletePstmt.close();
					insertPstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
