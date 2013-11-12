
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

import util.DateUtil;

import model.Data;
import net.sf.json.JSONObject;

/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2013. 10. 29.
 * =========================================================
 * Copyright (c) 2013 ManinSoft, Inc. All rights reserved.
 */

public class GetKpiWSForJson {

	public final String dbUrl = "jdbc:oracle:thin:@193.169.13.41:1523:grbf";
	public final String userId = "swuser";
	public final String pass = "smartworks";
	public final String driverClassName = "oracle.jdbc.driver.OracleDriver";

	
	public String getDailyShippingNSales(String month) throws Exception {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		int toYear = Calendar.getInstance().get(Calendar.YEAR);
		int toMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

		String selYear = month.substring(0,4);
		String selMonth = month.substring(4,6);
		
		int daycountOfMonth = DateUtil.GetDaysOfMonth(Integer.parseInt(selYear), Integer.parseInt(selMonth));
		int toDay = daycountOfMonth;
		
		if (Integer.parseInt(selYear) == toYear && Integer.parseInt(selMonth) == toMonth) {
			toDay = Calendar.getInstance().get(Calendar.DATE);
		}
			
		int fromDate = Integer.parseInt(month);
		int toDate = fromDate + toDay -1;

		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append(" select tbl.* ");
		sqlBuff.append(" ,round(((tbl.sumOfShipping / ((tbl.shippingExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) || '%' as perShipping ");
		sqlBuff.append(" ,round(((tbl.sumOfSales / ((tbl.salesExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) || '%' as perSales ");
		sqlBuff.append(" from ");
		sqlBuff.append(" ( ");
		sqlBuff.append("     select baseTbl.*, dailySS.boh, dailySS.sumOfReceiving, dailySS.sumOfShipping, dailySS.wip, dailySS.sumOfSales ");
		sqlBuff.append("     from ");
		sqlBuff.append("     ( ");
		sqlBuff.append("         select pln.*,foc.shippingFocPlan, foc.salesFocPlan, exe.shippingExePlan, exe.salesExePlan ");
		sqlBuff.append("         from ");
		sqlBuff.append("         ( ");
		sqlBuff.append("             select ");
		sqlBuff.append("                 collectingMonth, division, deviceGroup, sum(shippingPlanOfMonth) as shippingPlan, sum(salesPlanOfMonth) as salesPlan ");
		sqlBuff.append("             from  ");
		sqlBuff.append("                 sw_planofmanagement ");
		sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
		sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
		sqlBuff.append("         ) pln, ");
		sqlBuff.append("         ( ");
		sqlBuff.append("             select ");
		sqlBuff.append("                 collectingMonth, division, deviceGroup, sum(shippingPlanOfMonth) as shippingFocPlan, sum(salesPlanOfMonth) as salesFocPlan ");
		sqlBuff.append("             from  ");
		sqlBuff.append("                 sw_planOfForecast ");
		sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
		sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
		sqlBuff.append("         ) foc, ");
		sqlBuff.append("         ( ");
		sqlBuff.append("             select ");
		sqlBuff.append("                 collectingMonth, division, deviceGroup, sum(shippingPlanOfMonth) as shippingExePlan, sum(salesPlanOfMonth) as salesExePlan ");
		sqlBuff.append("             from  ");
		sqlBuff.append("                 PlanOFExecuting ");
		sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
		sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
		sqlBuff.append("         ) exe ");
		sqlBuff.append("         where pln.division = foc.division ");
		sqlBuff.append("         and pln.deviceGroup = foc.deviceGroup ");
		sqlBuff.append("         and foc.division = exe.division ");
		sqlBuff.append("         and foc.deviceGroup = exe.deviceGroup ");
		sqlBuff.append("     ) baseTbl ");
		sqlBuff.append("     left outer join ");
		sqlBuff.append("     ( ");
		sqlBuff.append("         select division, deviceGroup, 'boh' as boh, sum(receiving) as sumOfReceiving, sum(shipping) as sumOfShipping, 'wip' as wip, sum(salesOfDay) as sumOfSales ");
		sqlBuff.append("         from ");
		sqlBuff.append("             dailyShippingNSales ");
		sqlBuff.append("         where  ");
		sqlBuff.append("             collectingDate >= '").append(fromDate).append("' ");
		sqlBuff.append(" 			 and collectingDate <= '").append(toDate).append("' ");
		sqlBuff.append("         group by division, deviceGroup ");
		sqlBuff.append("     ) dailySS ");
		sqlBuff.append("     on  ");
		sqlBuff.append("    baseTbl.division = dailySS.division ");
		sqlBuff.append("     and baseTbl.deviceGroup = dailySS.deviceGroup ");
		sqlBuff.append(" ) tbl ");
		sqlBuff.append(" order by division desc, deviceGroup asc ");
		

		PreparedStatement pstmt = null;
		List<Map> resultListMap = new ArrayList<Map>();
		List columnNameList = new ArrayList();
		Connection con = null;
		ResultSet rs = null;

		JSONObject json = new JSONObject();
		try {
			con = DriverManager.getConnection(dbUrl, userId, pass);
			pstmt = con.prepareStatement(sqlBuff.toString());
			rs = pstmt.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNameList.add(rs.getMetaData().getColumnName(i));
			}

			while (rs.next()) {
				//ResultClass rc = new ResultClass();
				Map rowMap = new HashMap();
				for (int i = 0; i < columnNameList.size(); i++) {
					String columnName = (String) columnNameList.get(i);
					String value = rs.getString(columnName);
					//rc.resultClassValue(i, value);
					rowMap.put(columnName, value);
				}
				//resultList.add(rc);
				resultListMap.add(rowMap);
			}

			//json.put("rows", resultList);
			json.put("rows", resultListMap);

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
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	public String getDailyShipping(String month) throws Exception {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		int toYear = Calendar.getInstance().get(Calendar.YEAR);
		int toMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

		String selYear = month.substring(0,4);
		String selMonth = month.substring(4,6);
		
		int daycountOfMonth = DateUtil.GetDaysOfMonth(Integer.parseInt(selYear), Integer.parseInt(selMonth));
		int toDay = daycountOfMonth;
		
		if (Integer.parseInt(selYear) == toYear && Integer.parseInt(selMonth) == toMonth) {
			toDay = Calendar.getInstance().get(Calendar.DATE);
		}
			
		int fromDate = Integer.parseInt(month);
		int toDate = fromDate + toDay -1;

		StringBuffer sqlBuff = new StringBuffer();


		sqlBuff.append("    select exeplan.division, exeplan.devicegroup, exeplan.planOfShipping, exeplan.avgPlanOfDay ");
		sqlBuff.append("        ,dailyShip.totalSum ");
		sqlBuff.append("        ,dailyShip.avgOfDay ");
		sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfShipping / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) || '%' as perShipping ");
		
		for (int i = fromDate; i <= toDate; i++) {
			sqlBuff.append("        ,dailyShip.C").append((i+"").substring(6, 8)).append(" ");
		}
		
		sqlBuff.append("    from ");
		sqlBuff.append("    ( ");
		sqlBuff.append("        select collectingmonth, division, devicegroup  ");
		sqlBuff.append("            , sum(shippingplanofmonth) as planOfShipping ");
		sqlBuff.append("            , round((sum(shippingplanofmonth)/ ").append(daycountOfMonth).append(" ),2) as avgPlanOfDay   ");
		sqlBuff.append("        from PlanOFExecuting ");
		sqlBuff.append("        where collectingmonth ='").append(month).append("' ");
		sqlBuff.append("        group by collectingmonth, division, devicegroup ");
		sqlBuff.append("        order by collectingmonth asc, division desc ");
		sqlBuff.append("    ) exeplan ");
		sqlBuff.append("    left outer join ");
		sqlBuff.append("    ( ");
		sqlBuff.append("        select  ");
		sqlBuff.append("            division as dailyShipDivision ");
		sqlBuff.append("            ,devicegroup as dailyShipDeviceGroup ");
		sqlBuff.append("            ,sum(shipping) as totalSum ");
		sqlBuff.append("            ,round((sum(shipping)/ ").append(toDay).append(" ),2) as avgOfDay ");
		
		for (int i = fromDate; i <= toDate; i++) {
			sqlBuff.append("            ,max(decode(collectingdate, '").append(i).append("', shipping)) as \"C").append((i+"").substring(6, 8)).append("\" ");
		}
		
		sqlBuff.append("        from ");
		sqlBuff.append("        ( ");
		sqlBuff.append("               select division, devicegroup, collectingdate, sum(shipping) as shipping ");
		sqlBuff.append("               from ");
		sqlBuff.append("               dailyshippingnsales ");
		sqlBuff.append("               where collectingdate >= '").append(fromDate).append("' and collectingdate <= '").append(toDate).append("' ");
		sqlBuff.append("               group by division, devicegroup, collectingdate ");
		sqlBuff.append("         ) ");
		sqlBuff.append("         group by division, devicegroup ");
		sqlBuff.append("     ) dailyShip ");
		sqlBuff.append("     on exeplan.devicegroup = dailyShip.dailyShipDevicegroup ");
		sqlBuff.append("	 order by exeplan.division desc, exeplan.devicegroup asc");
		
		
		PreparedStatement pstmt = null;
		List<Map> resultListMap = new ArrayList<Map>();
		List columnNameList = new ArrayList();
		Connection con = null;
		ResultSet rs = null;

		JSONObject json = new JSONObject();
		try {
			con = DriverManager.getConnection(dbUrl, userId, pass);
			pstmt = con.prepareStatement(sqlBuff.toString());
			rs = pstmt.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNameList.add(rs.getMetaData().getColumnName(i));
			}

			while (rs.next()) {
				//ResultClass rc = new ResultClass();
				Map rowMap = new HashMap();
				for (int i = 0; i < columnNameList.size(); i++) {
					String columnName = (String) columnNameList.get(i);
					String value = rs.getString(columnName);
					//rc.resultClassValue(i, value);
					rowMap.put(columnName, value);
				}
				//resultList.add(rc);
				resultListMap.add(rowMap);
			}

			json.put("rows", resultListMap);

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
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	public String getDailySales(String month) throws Exception {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		int toYear = Calendar.getInstance().get(Calendar.YEAR);
		int toMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

		String selYear = month.substring(0,4);
		String selMonth = month.substring(4,6);
		
		int daycountOfMonth = DateUtil.GetDaysOfMonth(Integer.parseInt(selYear), Integer.parseInt(selMonth));
		int toDay = daycountOfMonth;
		
		if (Integer.parseInt(selYear) == toYear && Integer.parseInt(selMonth) == toMonth) {
			toDay = Calendar.getInstance().get(Calendar.DATE);
		}
			
		int fromDate = Integer.parseInt(month);
		int toDate = fromDate + toDay -1;

		StringBuffer sqlBuff = new StringBuffer();


		sqlBuff.append("    select exeplan.division, exeplan.devicegroup, exeplan.planOfSales, exeplan.avgPlanOfDay ");
		sqlBuff.append("        ,dailySales.totalSum ");
		sqlBuff.append("        ,dailySales.avgOfDay ");
		sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfSales / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) || '%' as perSales ");
		
		for (int i = fromDate; i <= toDate; i++) {
			sqlBuff.append("        ,dailySales.C").append((i+"").substring(6, 8)).append(" ");
		}
		
		sqlBuff.append("    from ");
		sqlBuff.append("    ( ");
		sqlBuff.append("        select collectingmonth, division, devicegroup  ");
		sqlBuff.append("            , sum(salesplanofmonth) as planOfSales ");
		sqlBuff.append("            , round((sum(salesplanofmonth)/ ").append(daycountOfMonth).append(" ),2) as avgPlanOfDay   ");
		sqlBuff.append("        from PlanOFExecuting ");
		sqlBuff.append("        where collectingmonth ='").append(month).append("' ");
		sqlBuff.append("        group by collectingmonth, division, devicegroup ");
		sqlBuff.append("        order by collectingmonth asc, division desc ");
		sqlBuff.append("    ) exeplan ");
		sqlBuff.append("    left outer join ");
		sqlBuff.append("    ( ");
		sqlBuff.append("        select  ");
		sqlBuff.append("            division as dailySalesDivision ");
		sqlBuff.append("            ,devicegroup as dailySalesDeviceGroup ");
		sqlBuff.append("            ,sum(salesOfDay) as totalSum ");
		sqlBuff.append("            ,round((sum(salesOfDay)/ ").append(toDay).append(" ),2) as avgOfDay ");
		
		for (int i = fromDate; i <= toDate; i++) {
			sqlBuff.append("            ,max(decode(collectingdate, '").append(i).append("', salesOfDay)) as \"C").append((i+"").substring(6, 8)).append("\" ");
		}
		
		sqlBuff.append("        from ");
		sqlBuff.append("        ( ");
		sqlBuff.append("               select division, devicegroup, collectingdate, sum(salesOfDay) as salesOfDay ");
		sqlBuff.append("               from ");
		sqlBuff.append("               dailyshippingnsales ");
		sqlBuff.append("               where collectingdate >= '").append(fromDate).append("' and collectingdate <= '").append(toDate).append("' ");
		sqlBuff.append("               group by division, devicegroup, collectingdate ");
		sqlBuff.append("         ) ");
		sqlBuff.append("         group by division, devicegroup ");
		sqlBuff.append("     ) dailySales ");
		sqlBuff.append("     on exeplan.devicegroup = dailySales.dailySalesDevicegroup ");
		sqlBuff.append("	 order by exeplan.division desc, exeplan.devicegroup asc");
		
		
		PreparedStatement pstmt = null;
		List<Map> resultListMap = new ArrayList<Map>();
		List columnNameList = new ArrayList();
		Connection con = null;
		ResultSet rs = null;

		JSONObject json = new JSONObject();
		try {
			con = DriverManager.getConnection(dbUrl, userId, pass);
			pstmt = con.prepareStatement(sqlBuff.toString());
			rs = pstmt.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNameList.add(rs.getMetaData().getColumnName(i));
			}

			while (rs.next()) {
				//ResultClass rc = new ResultClass();
				Map rowMap = new HashMap();
				for (int i = 0; i < columnNameList.size(); i++) {
					String columnName = (String) columnNameList.get(i);
					String value = rs.getString(columnName);
					//rc.resultClassValue(i, value);
					rowMap.put(columnName, value);
				}
				//resultList.add(rc);
				resultListMap.add(rowMap);
			}

			json.put("rows", resultListMap);

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
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	public String getMonthlyShippingForYear(String month) throws Exception {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
		cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String toDateStr = sdf.format(cal.getTime());
		cal.add(Calendar.MONTH, -12);
		String fromDateStr = sdf.format(cal.getTime());
		
		StringBuffer sqlBuff = new StringBuffer();

		sqlBuff.append(" select ");
		sqlBuff.append(" division  ");
		
		
		Calendar fromDateCal = Calendar.getInstance();
		fromDateCal.set(Calendar.YEAR, Integer.parseInt(fromDateStr.substring(0, 4)));
		fromDateCal.set(Calendar.MONTH, Integer.parseInt(fromDateStr.substring(4, 6)) -1);
		fromDateCal.set(Calendar.DATE, Integer.parseInt(fromDateStr.substring(6,8)));
		
		for (int i = 0; i < 12; i++) {
			fromDateCal.add(Calendar.MONTH, 1);
			String dateStr = sdf.format(fromDateCal.getTime());
			sqlBuff.append(" ,max(decode(collectingMonth, ").append(dateStr).append(", sumofShipping)) as \"C").append(dateStr).append("\" ");
		}
		
		sqlBuff.append(" ,sum(sumofShipping) as \"TOTAL\" ");
		sqlBuff.append(" from  ");
		sqlBuff.append(" ( ");
		sqlBuff.append("     select division, collectingMonth, sum(sumofshipping) as sumOfShipping ");
		sqlBuff.append("     from ");
		sqlBuff.append("         monthlyShippingNsales ");
		sqlBuff.append("     where collectingMonth >= '").append(fromDateStr).append("' and collectingMonth <= '").append(toDateStr).append("' ");
		sqlBuff.append("     group by division, collectingMonth ");
		sqlBuff.append(" ) ");
		sqlBuff.append(" group by division ");
		
		
		PreparedStatement pstmt = null;
		List<Map> resultListMap = new ArrayList<Map>();
		List columnNameList = new ArrayList();
		Connection con = null;
		ResultSet rs = null;

		JSONObject json = new JSONObject();
		try {
			con = DriverManager.getConnection(dbUrl, userId, pass);
			pstmt = con.prepareStatement(sqlBuff.toString());
			rs = pstmt.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNameList.add(rs.getMetaData().getColumnName(i));
			}

			while (rs.next()) {
				Map rowMap = new HashMap();
				for (int i = 0; i < columnNameList.size(); i++) {
					String columnName = (String) columnNameList.get(i);
					String value = rs.getString(columnName);
					//rc.resultClassValue(i, value);
					rowMap.put(columnName, value);
				}
				resultListMap.add(rowMap);
			}

			json.put("rows", resultListMap);

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
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	public String getMonthlySalesForYear(String month) throws Exception {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
		cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String toDateStr = sdf.format(cal.getTime());
		cal.add(Calendar.MONTH, -12);
		String fromDateStr = sdf.format(cal.getTime());
		
		StringBuffer sqlBuff = new StringBuffer();

		sqlBuff.append(" select ");
		sqlBuff.append(" division  ");
		
		
		Calendar fromDateCal = Calendar.getInstance();
		fromDateCal.set(Calendar.YEAR, Integer.parseInt(fromDateStr.substring(0, 4)));
		fromDateCal.set(Calendar.MONTH, Integer.parseInt(fromDateStr.substring(4, 6)) -1);
		fromDateCal.set(Calendar.DATE, Integer.parseInt(fromDateStr.substring(6,8)));
		
		for (int i = 0; i < 12; i++) {
			fromDateCal.add(Calendar.MONTH, 1);
			String dateStr = sdf.format(fromDateCal.getTime());
			sqlBuff.append(" ,max(decode(collectingMonth, ").append(dateStr).append(", sumOfSales)) as \"C").append(dateStr).append("\" ");
		}
		
		sqlBuff.append(" ,sum(sumOfSales) as \"TOTAL\" ");
		sqlBuff.append(" from  ");
		sqlBuff.append(" ( ");
		sqlBuff.append("     select division, collectingMonth, sum(sumOfSales) as sumOfSales ");
		sqlBuff.append("     from ");
		sqlBuff.append("         monthlyShippingNsales ");
		sqlBuff.append("     where collectingMonth >= '").append(fromDateStr).append("' and collectingMonth <= '").append(toDateStr).append("' ");
		sqlBuff.append("     group by division, collectingMonth ");
		sqlBuff.append(" ) ");
		sqlBuff.append(" group by division ");
		
		
		PreparedStatement pstmt = null;
		List<Map> resultListMap = new ArrayList<Map>();
		List columnNameList = new ArrayList();
		Connection con = null;
		ResultSet rs = null;

		JSONObject json = new JSONObject();
		try {
			con = DriverManager.getConnection(dbUrl, userId, pass);
			pstmt = con.prepareStatement(sqlBuff.toString());
			rs = pstmt.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNameList.add(rs.getMetaData().getColumnName(i));
			}

			while (rs.next()) {
				Map rowMap = new HashMap();
				for (int i = 0; i < columnNameList.size(); i++) {
					String columnName = (String) columnNameList.get(i);
					String value = rs.getString(columnName);
					//rc.resultClassValue(i, value);
					rowMap.put(columnName, value);
				}
				resultListMap.add(rowMap);
			}

			json.put("rows", resultListMap);

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
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	
}
