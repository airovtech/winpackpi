
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
import util.ResultsetConverter;

import model.Data;
import net.sf.json.JSONObject;

/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2013. 10. 29.
 * =========================================================
 * Copyright (c) 2013 ManinSoft, Inc. All rights reserved.
 */

public class GetKpiWS {

	public final String dbUrl = "jdbc:oracle:thin:@193.169.13.41:1523:grbf";
	public final String userId = "swuser";
	public final String pass = "smartworks";
	public final String driverClassName = "oracle.jdbc.driver.OracleDriver";
	public final String returnType = "json";//"data"
	
	//당월생산매출실적
	public String getDailyShippingNSales(String month) throws Exception {
		String sql = makeQueryString("getDailyShippingNSales", month);
		return executeQuery("getDailyShippingNSales", sql, month);
	}
	//일별생산실적
	public String getDailyShipping(String month) throws Exception {
		String sql = makeQueryString("getDailyShipping", month);
		return executeQuery("getDailyShipping", sql, month);
	}
	//일별매출실적
	public String getDailySales(String month) throws Exception {
		String sql = makeQueryString("getDailySales", month);
		return executeQuery("getDailySales", sql, month);
	}
	//일별가동률현황
	public String getDailyOperationRatio(String month) throws Exception {
		String sql = makeQueryString("getDailyOperationRatio", month);
		return executeQuery("getDailyOperationRatio", sql, month);
	}
	//일별TAT현황
	public String getDailyTat(String month) throws Exception {
		String sql = makeQueryString("getDailyTat", month);
		return executeQuery("getDailyTat", sql, month);
	}
	//1년간월별생산실적TREND
	public String getMonthlyShippingForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlyShippingForYear", month);
		return executeQuery("getMonthlyShippingForYear", sql, month);
	}
	//1년간월별매출실적TREND
	public String getMonthlySalesForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlySalesForYear", month);
		return executeQuery("getMonthlySalesForYear", sql, month);
	}
	//1년간월별Capacity대비실적
	public String getMonthlyCapacityPkgForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlyCapacityPkgForYear", month);
		return executeQuery("getMonthlyCapacityPkgForYear", sql, month);
	}
	//월별생산실적현황
	public String getMonthlyShipping(String month) throws Exception {
		String sql = makeQueryString("getMonthlyShipping", month);
		return executeQuery("getMonthlyShipping", sql, month);
	}
	//월별매출실적현황
	public String getMonthlySales(String month) throws Exception {
		String sql = makeQueryString("getMonthlySales", month);
		return executeQuery("getMonthlySales", sql, month);
	}
	public String getTestChartData(String date) throws Exception {
		Data data = ResultsetConverter.convertToData(null, null, null);
		return data.toString();
	}
	
	public String makeQueryString(String method, String month) throws Exception {
		
		if (month == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String yearMonth = sdf.format(Calendar.getInstance().getTime());
			month = yearMonth + "01";
		}
		
		int toYear = Calendar.getInstance().get(Calendar.YEAR);
		int toMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

		String selYear = month.substring(0,4);
		String selMonth = month.substring(4,6);
		
		int daycountOfMonth = DateUtil.GetDaysOfMonth(Integer.parseInt(selYear), Integer.parseInt(selMonth));
		int toDay = daycountOfMonth;
		
		if (Integer.parseInt(selYear) == toYear && Integer.parseInt(selMonth) == toMonth) {
			toDay = Calendar.getInstance().get(Calendar.DATE) -1;
		}
			
		int fromDate = Integer.parseInt(month);
		//int toDate = fromDate + toDay -1;
		int toDate = fromDate + daycountOfMonth -1;

		StringBuffer sqlBuff = new StringBuffer();
		
		if (method.equalsIgnoreCase("getDailyShippingNSales")) {
			sqlBuff.append(" select tbl.* ");
			sqlBuff.append(" ,round(((tbl.sumOfShipping / ((tbl.shippingExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) || '%' as perShipping ");
			sqlBuff.append(" ,round(((tbl.sumOfSales / ((tbl.salesExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) || '%' as perSales ");
			sqlBuff.append(" from ");
			sqlBuff.append(" ( ");
			sqlBuff.append("     select baseTbl.*, dailySS.boh, dailySS.sumOfReceiving, dailySS.sumOfShipping, dailySS.wip, dailySS.sumOfSales ");
			sqlBuff.append("     from ");
			sqlBuff.append("     ( ");
			sqlBuff.append("         select pln.division, pln.deviceGroup, pln.shippingPlan, pln.salesPlan , foc.shippingFocPlan, foc.salesFocPlan, exe.shippingExePlan, exe.salesExePlan ");
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
			sqlBuff.append("         select division, deviceGroup, sum(receiving) as sumOfReceiving, sum(shipping) as sumOfShipping, sum(salesOfDay) as sumOfSales ");
			
			sqlBuff.append("			,(");
			sqlBuff.append("				select boh ");
			sqlBuff.append("				from ( ");
			sqlBuff.append("					select devicegroup, sum(boh) as boh from dailyShippingNSales  "); 
			sqlBuff.append("					where collectingDate = '").append(toDate-1).append("'  ");
			sqlBuff.append("					group by devicegroup  ");
			sqlBuff.append("				) bohTbl  ");
			sqlBuff.append("				where bohTbl.devicegroup = daily.deviceGroup ");
			sqlBuff.append("			) as boh");
			
			sqlBuff.append("			,(");
			sqlBuff.append("				select wip ");
			sqlBuff.append("				from ( ");
			sqlBuff.append("					select devicegroup, sum(wip) as wip from dailyShippingNSales  "); 
			sqlBuff.append("					where collectingDate = '").append(toDate-1).append("'  ");
			sqlBuff.append("					group by devicegroup  ");
			sqlBuff.append("				) wipTbl  ");
			sqlBuff.append("				where wipTbl.devicegroup = daily.deviceGroup ");
			sqlBuff.append("			) as wip");
			
			sqlBuff.append("         from ");
			sqlBuff.append("             dailyShippingNSales daily");
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
			
		} else if (method.equalsIgnoreCase("getDailyShipping")) {
			
			sqlBuff.append("    select exeplan.division, exeplan.devicegroup, exeplan.planOfShipping, exeplan.avgPlanOfDay ");
			sqlBuff.append("        ,dailyShip.totalSum ");
			sqlBuff.append("        ,dailyShip.avgOfDay ");
			sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfShipping / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) || '%' as perShipping ");
			sqlBuff.append("        ,'WIP' as WIP");
			
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
			
		} else if (method.equalsIgnoreCase("getDailySales")) {
			
			sqlBuff.append("    select exeplan.division, exeplan.devicegroup, exeplan.planOfSales, exeplan.avgPlanOfDay ");
			sqlBuff.append("        ,dailySales.totalSum ");
			sqlBuff.append("        ,dailySales.avgOfDay ");
			sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfSales / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) || '%' as perSales ");
			sqlBuff.append("        ,'WIP' as WIP");
			
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
		
		} else if (method.equalsIgnoreCase("getDailyOperationRatio")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			cal.add(Calendar.MONTH, -1);
			String fromLastMonth = sdf.format(cal.getTime());
			
			String year = fromLastMonth.substring(0,4);
			String lastMonth = fromLastMonth.substring(4,6);
			int daycountOfLastMonth = DateUtil.GetDaysOfMonth(Integer.parseInt(year), Integer.parseInt(lastMonth));
			
			String fromLastMonthStr = fromLastMonth + "01";
			String toLastMonthStr = (Integer.parseInt(fromLastMonthStr) + (daycountOfLastMonth -1)) + "";
			
			sqlBuff.append("SELECT tbl.* ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("        ,dailyTbl.C").append((i+"").substring(6, 8)).append(" ");
			}
			sqlBuff.append("FROM   (SELECT target.* ");
			sqlBuff.append("               ,lastMonthAvgTbl.lastmonthavg ");
			sqlBuff.append("               ,monthAvgTbl.monthavg ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,gubun ");
			sqlBuff.append("                       ,targetoperationratio AS targetOr ");
			sqlBuff.append("                FROM   sw_monthlytargetoperationratio ");
			sqlBuff.append("                WHERE  collectingmonth = '").append(month).append("') target ");
			sqlBuff.append("               left outer join (SELECT division ");
			sqlBuff.append("                                       ,gubun ");
			sqlBuff.append("                                       ,Round(Avg(operationratio), 2) lastMonthAvg ");
			sqlBuff.append("                                FROM   dailyoperationratio ");
			sqlBuff.append("                                WHERE  collectingdate >= '").append(fromLastMonthStr).append("' ");
			sqlBuff.append("                                       AND collectingdate <= '").append(toLastMonthStr).append("' ");
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,gubun) lastMonthAvgTbl ");
			sqlBuff.append("                            ON target.gubun = lastMonthAvgTbl.gubun ");
			sqlBuff.append("               left outer join (SELECT division ");
			sqlBuff.append("                                       ,gubun ");
			sqlBuff.append("                                       ,Round(Avg(operationratio), 2) monthAvg ");
			sqlBuff.append("                                FROM   dailyoperationratio ");
			sqlBuff.append("                                WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("                                       AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,gubun) monthAvgTbl ");
			sqlBuff.append("                            ON target.gubun = monthAvgTbl.gubun) tbl ");
			sqlBuff.append("       left outer join (SELECT division ,gubun ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("            ,Max(Decode(collectingdate, '").append(i).append("', operationratio)) as \"C").append((i+"").substring(6, 8)).append("\" ");
			}
			sqlBuff.append("                        FROM   dailyoperationratio ");
			sqlBuff.append("                        WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("                               AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("                        GROUP  BY division ");
			sqlBuff.append("                                  ,gubun) dailyTbl ");
			sqlBuff.append("                    ON tbl.gubun = dailyTbl.gubun ");
			sqlBuff.append("		ORDER BY tbl.division, tbl.gubun ");	
			
		} else if (method.equalsIgnoreCase("getDailyTat")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			cal.add(Calendar.MONTH, -1);
			String fromLastMonth = sdf.format(cal.getTime());
			
			String year = fromLastMonth.substring(0,4);
			String lastMonth = fromLastMonth.substring(4,6);
			int daycountOfLastMonth = DateUtil.GetDaysOfMonth(Integer.parseInt(year), Integer.parseInt(lastMonth));
			
			String fromLastMonthStr = fromLastMonth + "01";
			String toLastMonthStr = (Integer.parseInt(fromLastMonthStr) + (daycountOfLastMonth -1)) + "";
			
			sqlBuff.append("SELECT tbl.* ");
			sqlBuff.append("       ,lastMonthAvgTatTbl.lastmonthavgtat ");
			sqlBuff.append("       ,monthAvgTatTbl.monthavgtat ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("        ,dailyTbl.C").append((i+"").substring(6, 8)).append(" ");
			}
			sqlBuff.append("FROM   (SELECT division ");
			sqlBuff.append("               ,devicegroup ");
			sqlBuff.append("               ,Avg(customertat) AS customerTat ");
			sqlBuff.append("               ,Avg(targettat)   AS targetTat ");
			sqlBuff.append("        FROM   sw_targettatofmonth ");
			sqlBuff.append("        WHERE  collectingmonth = '").append(month).append("' ");
			sqlBuff.append("        GROUP  BY division ");
			sqlBuff.append("                  ,devicegroup) tbl ");
			sqlBuff.append("       left outer join (SELECT division ");
			sqlBuff.append("                               ,devicegroup ");
			sqlBuff.append("                               ,Round(Avg(tatofday), 2) AS lastMonthAvgTat ");
			sqlBuff.append("                        FROM   dailyshippingnsales ");
			sqlBuff.append("                        WHERE  collectingdate >= '").append(fromLastMonthStr).append("' ");
			sqlBuff.append("                               AND collectingdate <= '").append(toLastMonthStr).append("' ");
			sqlBuff.append("                        GROUP  BY division ");
			sqlBuff.append("                                  ,devicegroup) lastMonthAvgTatTbl ");
			sqlBuff.append("                    ON tbl.devicegroup = lastMonthAvgTatTbl.devicegroup ");
			sqlBuff.append("       left outer join (SELECT division ");
			sqlBuff.append("                               ,devicegroup ");
			sqlBuff.append("                               ,Round(Avg(tatofday), 2) AS monthAvgTat ");
			sqlBuff.append("                        FROM   dailyshippingnsales ");
			sqlBuff.append("                        WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("                               AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("                        GROUP  BY division ");
			sqlBuff.append("                                  ,devicegroup) monthAvgTatTbl ");
			sqlBuff.append("                    ON tbl.devicegroup = monthAvgTatTbl.devicegroup ");
			sqlBuff.append("       left outer join (SELECT division ");
			sqlBuff.append("                               ,devicegroup ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("            ,Max(Decode(collectingdate, '").append(i).append("', tatofday)) as \"C").append((i+"").substring(6, 8)).append("\" ");
			}
			sqlBuff.append("                        FROM   (SELECT division ");
			sqlBuff.append("                                       ,devicegroup ");
			sqlBuff.append("                                       ,collectingdate ");
			sqlBuff.append("                                       ,round(Avg(tatofday),2) AS tatOfDay ");
			sqlBuff.append("                                FROM   dailyshippingnsales ");
			sqlBuff.append("                                WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("                                       AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,devicegroup ");
			sqlBuff.append("                                          ,collectingdate ");
			sqlBuff.append("                                ORDER  BY collectingdate) ");
			sqlBuff.append("                        GROUP  BY division ");
			sqlBuff.append("                                  ,devicegroup) dailyTbl ");
			sqlBuff.append("                    ON tbl.devicegroup = dailyTbl.devicegroup ");
			sqlBuff.append("                    ORDER BY tbl.division, tbl.devicegroup ");
			
		} else if (method.equalsIgnoreCase("getMonthlyShippingForYear")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String toDateStr = sdf.format(cal.getTime());
			cal.add(Calendar.MONTH, -12);
			String fromDateStr = sdf.format(cal.getTime());
			
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
			
		} else if (method.equalsIgnoreCase("getMonthlySalesForYear")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String toDateStr = sdf.format(cal.getTime());
			cal.add(Calendar.MONTH, -12);
			String fromDateStr = sdf.format(cal.getTime());
			
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
			
		} else if (method.equalsIgnoreCase("getMonthlyCapacityPkgForYear")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String toDateStr = sdf.format(cal.getTime());
			cal.add(Calendar.MONTH, -12);
			String fromDateStr = sdf.format(cal.getTime());
			
			sqlBuff.append(" select ");
			sqlBuff.append(" division  ");
			
			Calendar fromDateCal = Calendar.getInstance();
			fromDateCal.set(Calendar.YEAR, Integer.parseInt(fromDateStr.substring(0, 4)));
			fromDateCal.set(Calendar.MONTH, Integer.parseInt(fromDateStr.substring(4, 6)) -1);
			fromDateCal.set(Calendar.DATE, Integer.parseInt(fromDateStr.substring(6,8)));
			Calendar fromDateCal2 = Calendar.getInstance();
			fromDateCal2.set(Calendar.YEAR, Integer.parseInt(fromDateStr.substring(0, 4)));
			fromDateCal2.set(Calendar.MONTH, Integer.parseInt(fromDateStr.substring(4, 6)) -1);
			fromDateCal2.set(Calendar.DATE, Integer.parseInt(fromDateStr.substring(6,8)));
			
			sqlBuff.append("               ,'Capacity'                                                    AS gubun ");
			
			for (int i = 0; i < 12; i++) {
				fromDateCal.add(Calendar.MONTH, 1);
				String dateStr = sdf.format(fromDateCal.getTime());
				sqlBuff.append(" ,max(decode(collectingMonth, ").append(dateStr).append(", monthlycapashipping)) as \"C").append(dateStr).append("\" ");
			}
			
			sqlBuff.append("        ,SUM(monthlyCapaShipping) as TOTAL ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,collectingmonth ");
			sqlBuff.append("                       ,SUM(planofshipping) monthlyCapaShipping ");
			sqlBuff.append("                FROM   planofmonthlycapacity ");
			sqlBuff.append("                WHERE  division = 'pkg' ");
			sqlBuff.append("     			AND collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("' ");
			sqlBuff.append("                GROUP  BY division, ");
			sqlBuff.append("                          collectingmonth) ");
			sqlBuff.append("        GROUP  BY division ");
			sqlBuff.append("        UNION ALL ");
			sqlBuff.append("        SELECT division ");
			sqlBuff.append("               ,'생산실적'                                                  AS gubun ");
			
			for (int i = 0; i < 12; i++) {
				fromDateCal2.add(Calendar.MONTH, 1);
				String dateStr = sdf.format(fromDateCal2.getTime());
				sqlBuff.append(" 				,max(decode(collectingMonth, ").append(dateStr).append(", sumofshipping)) as \"C").append(dateStr).append("\" ");
			}
			sqlBuff.append("        ,SUM(sumofshipping) as TOTAL  ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,collectingmonth ");
			sqlBuff.append("                       ,SUM(sumofshipping) sumOfShipping ");
			sqlBuff.append("                FROM   monthlyshippingnsales ");
			sqlBuff.append("                WHERE  division = 'pkg' ");
			sqlBuff.append("     			AND collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("' ");
			sqlBuff.append("                GROUP  BY division, ");
			sqlBuff.append("                          collectingmonth) ");
			sqlBuff.append("        GROUP  BY division ");
			
		} else if (method.equalsIgnoreCase("getMonthlyShipping")) {
			
			int fromDateStr = Integer.parseInt(month.substring(0, 4) + "0101");
			int toDateStr = Integer.parseInt(month.substring(0, 4) + "1201");
			
			sqlBuff.append("SELECT division ");
			
			for (int i = fromDateStr; i <= toDateStr; i = i + 100) {
				sqlBuff.append("       ,NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.collectingmonth)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.planshipping)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.focplanshipping)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.sumofshipping)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithplan)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithfocplan)), 0) AS C").append((i+"").substring(4, 6)).append(" ");
			}
			
			sqlBuff.append("FROM   (SELECT pln.* ");
			sqlBuff.append("               ,foc.focshipping                                               AS focPlanShipping ");
			sqlBuff.append("               ,ship.sumofshipping ");
			sqlBuff.append("               ,Round(( ( ship.sumofshipping / pln.planshipping ) * 100 ), 2) perWithPlan ");
			sqlBuff.append("               ,Round(( ( ship.sumofshipping / foc.focshipping ) * 100 ), 2)  perWithFocPlan ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,collectingmonth ");
			sqlBuff.append("                       ,SUM(shippingplanofmonth) planShipping ");
			sqlBuff.append("                FROM   sw_planofmanagement ");
			sqlBuff.append("                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                GROUP  BY division ");
			sqlBuff.append("                          ,collectingmonth) pln ");
			sqlBuff.append("               left outer join (SELECT division ");
			sqlBuff.append("                                       ,collectingmonth ");
			sqlBuff.append("                                       ,SUM(shippingplanofmonth) focShipping ");
			sqlBuff.append("                                FROM   sw_planofforecast ");

			sqlBuff.append("                                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,collectingmonth) foc ");
			sqlBuff.append("                            ON pln.division = foc.division ");
			sqlBuff.append("                               AND pln.collectingmonth = foc.collectingmonth ");
			sqlBuff.append("               left outer join (SELECT division ");
			sqlBuff.append("                                       ,collectingmonth ");
			sqlBuff.append("                                       ,SUM(shipping) AS sumOfShipping ");
			sqlBuff.append("                                FROM   (SELECT division ");
			sqlBuff.append("                                               ,Substr(collectingdate, 0, 6) ");
			sqlBuff.append("                                                || '01' collectingMonth ");
			sqlBuff.append("                                               ,shipping ");
			sqlBuff.append("                                        FROM   dailyshippingnsales ");
			sqlBuff.append("                                        ) ");
		
			sqlBuff.append("                                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,collectingmonth) ship ");
			sqlBuff.append("                            ON pln.division = ship.division ");
			sqlBuff.append("                               AND pln.collectingmonth = ship.collectingmonth ");
			sqlBuff.append("        ORDER  BY pln.collectingmonth) tbl ");
			sqlBuff.append("GROUP  BY division ");
			
		} else if (method.equalsIgnoreCase("getMonthlySales")) {
			
			int fromDateStr = Integer.parseInt(month.substring(0, 4) + "0101");
			int toDateStr = Integer.parseInt(month.substring(0, 4) + "1201");
			
			sqlBuff.append("SELECT division ");
			
			for (int i = fromDateStr; i <= toDateStr; i = i + 100) {
				sqlBuff.append("       ,NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.collectingmonth)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.plansales)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.focplansales)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.sumofsales)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithplan)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithfocplan)), 0) AS C").append((i+"").substring(4, 6)).append(" ");
			}
			
			sqlBuff.append("FROM   (SELECT pln.* ");
			sqlBuff.append("               ,foc.focsales                                               AS focPlansales ");
			sqlBuff.append("               ,ship.sumofsales ");
			sqlBuff.append("               ,Round(( ( ship.sumofsales / pln.plansales ) * 100 ), 2) perWithPlan ");
			sqlBuff.append("               ,Round(( ( ship.sumofsales / foc.focsales ) * 100 ), 2)  perWithFocPlan ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,collectingmonth ");
			sqlBuff.append("                       ,SUM(salesplanofmonth) plansales ");
			sqlBuff.append("                FROM   sw_planofmanagement ");
			sqlBuff.append("                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                GROUP  BY division ");
			sqlBuff.append("                          ,collectingmonth) pln ");
			sqlBuff.append("               left outer join (SELECT division ");
			sqlBuff.append("                                       ,collectingmonth ");
			sqlBuff.append("                                       ,SUM(salesplanofmonth) focsales ");
			sqlBuff.append("                                FROM   sw_planofforecast ");

			sqlBuff.append("                                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,collectingmonth) foc ");
			sqlBuff.append("                            ON pln.division = foc.division ");
			sqlBuff.append("                               AND pln.collectingmonth = foc.collectingmonth ");
			sqlBuff.append("               left outer join (SELECT division ");
			sqlBuff.append("                                       ,collectingmonth ");
			sqlBuff.append("                                       ,SUM(salesOfDay) AS sumOfsales ");
			sqlBuff.append("                                FROM   (SELECT division ");
			sqlBuff.append("                                               ,Substr(collectingdate, 0, 6) ");
			sqlBuff.append("                                                || '01' collectingMonth ");
			sqlBuff.append("                                               ,salesOfDay ");
			sqlBuff.append("                                        FROM   dailyshippingnsales ");
			sqlBuff.append("                                        ) ");
		
			sqlBuff.append("                                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,collectingmonth) ship ");
			sqlBuff.append("                            ON pln.division = ship.division ");
			sqlBuff.append("                               AND pln.collectingmonth = ship.collectingmonth ");
			sqlBuff.append("        ORDER  BY pln.collectingmonth) tbl ");
			sqlBuff.append("GROUP  BY division ");
			
		}
		return sqlBuff.toString();
	}
	
	private String executeQuery(String operation, String sql, String month) throws Exception {
		
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		PreparedStatement pstmt = null;
		Connection con = null;
		ResultSet rs = null;

		Object result = null;
		try {
			con = DriverManager.getConnection(dbUrl, userId, pass);
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if (returnType.equalsIgnoreCase("json")) {
				result = ResultsetConverter.convertToJson(operation, rs, month);
			} else if(returnType.equalsIgnoreCase("data")) {
				result = ResultsetConverter.convertToData(operation, rs, month);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (SQLException e) {e.printStackTrace();}
			try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
			try {con.close();} catch (SQLException e) {e.printStackTrace();}
		}
		return result.toString();
	}
}
