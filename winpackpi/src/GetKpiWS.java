
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

	public final String dbUrl = "jdbc:oracle:thin:@193.169.13.45:1523:grbf";
	public final String userId = "swuser";
	public final String pass = "smartworks";
	public final String driverClassName = "oracle.jdbc.driver.OracleDriver";
	public final String returnType = "json";//"data"
	
	public String getDailyShippingNSales(String month) throws Exception {
		String sql = makeQueryString("getDailyShippingNSales", month);
		return executeQuery("getDailyShippingNSales", sql, month);
	}
	public String getDailyShipping(String month) throws Exception {
		String sql = makeQueryString("getDailyShipping", month);
		return executeQuery("getDailyShipping", sql, month);
	}
	public String getDailySales(String month) throws Exception {
		String sql = makeQueryString("getDailySales", month);
		return executeQuery("getDailySales", sql, month);
	}
	public String getDailyOperationRatio(String month) throws Exception {
		String sql = makeQueryString("getDailyOperationRatio", month);
		return executeQuery("getDailyOperationRatio", sql, month);
	}
	public String getDailyTat(String month) throws Exception {
		String sql = makeQueryString("getDailyTat", month);
		return executeQuery("getDailyTat", sql, month);
	}
	public String getMonthlyShippingForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlyShippingForYear", month);
		return executeQuery("getMonthlyShippingForYear", sql, month);
	}
	public String getMonthlySalesForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlySalesForYear", month);
		return executeQuery("getMonthlySalesForYear", sql, month);
	}
	public String getMonthlyCapacityPkgForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlyCapacityPkgForYear", month);
		return executeQuery("getMonthlyCapacityPkgForYear", sql, month);
	}
	public String getMonthlyCapacityPkgForYearByGroup(String month) throws Exception {
		String sql = makeQueryString("getMonthlyCapacityPkgForYearByGroup", month);
		return executeQuery("getMonthlyCapacityPkgForYearByGroup", sql, month);
	}
	public String getMonthlyShipping(String month) throws Exception {
		String sql = makeQueryString("getMonthlyShipping", month);
		return executeQuery("getMonthlyShipping", sql, month);
	}
	public String getMonthlySales(String month) throws Exception {
		String sql = makeQueryString("getMonthlySales", month);
		return executeQuery("getMonthlySales", sql, month);
	}
	public String getTestChartData(String date) throws Exception {
		Data data = ResultsetConverter.convertToData(null, null, null);
		return data.toString();
	}
	
	public String makeQueryString(String method, String month) throws Exception {
		
		String today_yyyyMMdd = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
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
			sqlBuff.append(" ,round(((tbl.sumOfShipping / ((tbl.shippingExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) as perShipping ");
			sqlBuff.append(" ,round(((tbl.sumOfSales / ((tbl.salesExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) as perSales ");
			sqlBuff.append(" from ");
			sqlBuff.append(" ( ");
			sqlBuff.append("     select case when baseTbl.division is null then dailySS.division else baseTbl.division end as division ");
			sqlBuff.append("     		,case when baseTbl.devicegroup is null then dailySS.devicegroup else baseTbl.devicegroup end as devicegroup ");
			sqlBuff.append("     		,baseTbl.shippingPlan, baseTbl.salesPlan, baseTbl.shippingFocPlan, baseTbl.salesFocPlan, baseTbl.shippingExePlan, baseTbl.salesExePlan ");
			sqlBuff.append("     		,round(dailySS.boh/1000,0) as boh, round(dailySS.sumOfReceiving/1000,0) as sumOfReceiving, round(dailySS.sumOfShipping/1000,0) as sumOfShipping, round(dailySS.wip/1000,0) as wip, round(dailySS.sumOfSales/1000000,0) as sumOfSales ");
			sqlBuff.append("     from ");
			sqlBuff.append("     ( ");
			sqlBuff.append("         select case when pln.division is null then (case when foc.division is null then exe.division else foc.division end) else pln.division end as division ");
			sqlBuff.append("         		,case when pln.devicegroup is null then (case when foc.devicegroup is null then exe.devicegroup else foc.devicegroup end) else pln.devicegroup end as devicegroup ");
			sqlBuff.append("         		,round(pln.shippingPlan/1000,0) as shippingPlan, round(pln.salesPlan/1000000,0) as salesPlan , round(foc.shippingFocPlan/1000,0) shippingFocPlan, round(foc.salesFocPlan/1000000,0) as salesFocPlan, round(exe.shippingExePlan/1000,0) as shippingExePlan, round(exe.salesExePlan/1000000,0) as salesExePlan ");
			sqlBuff.append("         from ");
			sqlBuff.append("         ( ");
			sqlBuff.append("             select ");
			sqlBuff.append("                 collectingMonth, division, deviceGroup, sum(shippingPlanOfMonth) as shippingPlan, sum(salesPlanOfMonth) as salesPlan ");

			sqlBuff.append("             from  ");
			sqlBuff.append("                 sw_planofmanagement ");
			sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
			sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
			sqlBuff.append("         ) pln ");
			
			sqlBuff.append("         full outer join ");
			
			sqlBuff.append("         ( ");
			sqlBuff.append("             select ");
			sqlBuff.append("                 collectingMonth, division, deviceGroup, sum(shippingPlanOfMonth) as shippingFocPlan, sum(salesPlanOfMonth) as salesFocPlan ");
			sqlBuff.append("             from  ");
			sqlBuff.append("                 sw_planOfForecast ");
			sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
			sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
			sqlBuff.append("         ) foc ");
			
			sqlBuff.append("         on pln.division = foc.division  AND pln.devicegroup = foc.devicegroup ");
			sqlBuff.append("         full outer join ");
			
			sqlBuff.append("         ( ");
			sqlBuff.append("             select ");
			sqlBuff.append("                 collectingMonth, division, deviceGroup, sum(shippingPlanOfMonth) as shippingExePlan, sum(salesPlanOfMonth) as salesExePlan ");
			sqlBuff.append("             from  ");
			sqlBuff.append("                 PlanOFExecuting ");
			sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
			sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
			sqlBuff.append("         ) exe ");
			
			sqlBuff.append("         on pln.division = exe.division AND pln.devicegroup = exe.devicegroup  ");
			
			sqlBuff.append("     ) baseTbl ");
			sqlBuff.append("     full outer join ");
			sqlBuff.append("     ( ");
			sqlBuff.append("         select division, deviceGroup, sum(receiving) as sumOfReceiving, sum(shipping) as sumOfShipping, sum(salesOfDay) as sumOfSales ");
			
			sqlBuff.append("			,(");
			sqlBuff.append("				select boh ");
			sqlBuff.append("				from ( ");
			sqlBuff.append("					select devicegroup, sum(boh) as boh from dailyShippingNSales  "); 
			//sqlBuff.append("					where collectingDate = '").append(toDate-1).append("'  ");
			//조회하는 달의 첫번째 날
			sqlBuff.append("					where collectingDate = '").append(fromDate).append("'  ");
			sqlBuff.append("					group by devicegroup  ");
			sqlBuff.append("				) bohTbl  ");
			sqlBuff.append("				where bohTbl.devicegroup = daily.deviceGroup ");
			sqlBuff.append("			) as boh");
			
			sqlBuff.append("			,(");
			sqlBuff.append("				select wip ");
			sqlBuff.append("				from ( ");
			sqlBuff.append("					select devicegroup, sum(wip) as wip from dailyShippingNSales  "); 
			//sqlBuff.append("					where collectingDate = '").append(toDate-1).append("'  ");
			
			if (toYear == Integer.parseInt(selYear) && toMonth == Integer.parseInt(selMonth)) {
				sqlBuff.append("					where collectingDate = '").append(today_yyyyMMdd).append("'  ");
			} else {
				sqlBuff.append("					where collectingDate = '").append(toDate).append("'  ");
			}
			
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
			
			sqlBuff.append("    select case when dailyShipDivision is null then exeplan.division else dailyShipDivision end as division, case when dailyShipDeviceGroup is null then exeplan.devicegroup else dailyShipDeviceGroup end as devicegroup ");
			sqlBuff.append("		,round(exeplan.planOfShipping/1000,0) as planOfShipping, round(exeplan.avgPlanOfDay/1000,0) as avgPlanOfDay ");
			sqlBuff.append("        ,round(dailyShip.totalSum/1000,0) as  totalSum");
			sqlBuff.append("        ,round(dailyShip.avgOfDay/1000,0) as avgOfDay");
			sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfShipping / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) as perShipping ");
			
			sqlBuff.append("		,(");
			sqlBuff.append("			select round(wip/1000,0) ");
			sqlBuff.append("			from ( ");
			sqlBuff.append("				select devicegroup, sum(wip) as wip from dailyShippingNSales  "); 
			//sqlBuff.append("				where collectingDate = '").append(toDate-1).append("'  ");\
			if (toYear == Integer.parseInt(selYear) && toMonth == Integer.parseInt(selMonth)) {
				sqlBuff.append("					where collectingDate = '").append(today_yyyyMMdd).append("'  ");
			} else {
				sqlBuff.append("					where collectingDate = '").append(toDate).append("'  ");
			}
			sqlBuff.append("				group by devicegroup  ");
			sqlBuff.append("			) wipTbl  ");
			sqlBuff.append("			where wipTbl.devicegroup = exeplan.deviceGroup ");
			sqlBuff.append("		) as WIP");
			
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("        ,round(dailyShip.C").append((i+"").substring(6, 8)).append("/1000,0) as C").append((i+"").substring(6, 8) + " ");
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
			sqlBuff.append("    full outer join ");
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
			sqlBuff.append("     on exeplan.division = dailyShip.dailyShipDivision and exeplan.devicegroup = dailyShip.dailyShipDevicegroup ");
			sqlBuff.append("	 order by division desc, devicegroup asc");
			
		} else if (method.equalsIgnoreCase("getDailySales")) {
			
			sqlBuff.append("    select case when dailySalesDivision is null then exeplan.division else dailySalesDivision end as division, case when dailySalesDeviceGroup is null then exeplan.devicegroup else dailySalesDeviceGroup end as devicegroup ");
			sqlBuff.append("		,round(exeplan.planOfSales/1000000,0) as planOfSales ");
			sqlBuff.append("		,round(exeplan.avgPlanOfDay/1000000,0) as avgPlanOfDay ");
			sqlBuff.append("        ,round(dailySales.totalSum/1000000,0) as totalSum ");
			sqlBuff.append("        ,round(dailySales.avgOfDay/1000000,0) as avgOfDay ");
			//exeplan.planOfSales 값이 0이 나올수 있음
			//sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfSales / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) as perSales ");
			sqlBuff.append("        ,decode(exeplan.planofsales,0,0, round(((totalSum / ((exeplan.planOfSales / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2)) as perSales ");
			
			sqlBuff.append("		,(");
			sqlBuff.append("			select round(wip/1000000,0) ");
			sqlBuff.append("			from ( ");
			sqlBuff.append("				select devicegroup, sum(wip) as wip from dailyShippingNSales  "); 
			//sqlBuff.append("				where collectingDate = '").append(toDate-1).append("'  ");
			if (toYear == Integer.parseInt(selYear) && toMonth == Integer.parseInt(selMonth)) {
				sqlBuff.append("					where collectingDate = '").append(today_yyyyMMdd).append("'  ");
			} else {
				sqlBuff.append("					where collectingDate = '").append(toDate).append("'  ");
			}
			sqlBuff.append("				group by devicegroup  ");
			sqlBuff.append("			) wipTbl  ");
			sqlBuff.append("			where wipTbl.devicegroup = exeplan.deviceGroup ");
			sqlBuff.append("		) as WIP");
			
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("        ,round(dailySales.C").append((i+"").substring(6, 8)).append("/1000000,0) as C").append((i+"").substring(6, 8)).append(" ");
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
			sqlBuff.append("    full outer join ");
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
			sqlBuff.append("     on exeplan.division = dailySales.dailySalesDivision and exeplan.devicegroup = dailySales.dailySalesDevicegroup ");
			sqlBuff.append("	 order by division desc, devicegroup asc");
		
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
			
			sqlBuff.append("SELECT case when dailyTbl.division is null then tbl.division else dailyTbl.division end as division ");
			sqlBuff.append("	   ,case when dailyTbl.gubun is null then tbl.gubun else dailyTbl.gubun end as gubun ");
			sqlBuff.append("	   ,tbl.targetOr, tbl.lastmonthavg, tbl.monthavg ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("        ,dailyTbl.C").append((i+"").substring(6, 8)).append(" ");
			}
			sqlBuff.append("FROM   (SELECT case when monthAvgTbl.division is null then (case when lastMonthAvgTbl.division is null then target.division else lastMonthAvgTbl.division end) else monthAvgTbl.division end as division ");
			sqlBuff.append("			   ,case when monthAvgTbl.gubun is null then (case when lastMonthAvgTbl.gubun is null then target.gubun else lastMonthAvgTbl.gubun end) else monthAvgTbl.gubun end as gubun ");
			sqlBuff.append("               ,target.targetOr ");
			sqlBuff.append("               ,lastMonthAvgTbl.lastmonthavg ");
			sqlBuff.append("               ,monthAvgTbl.monthAvg ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,gubun ");
			sqlBuff.append("                       ,targetoperationratio AS targetOr ");
			sqlBuff.append("                FROM   sw_monthlytargetoperationratio ");
			sqlBuff.append("                WHERE  collectingmonth = '").append(month).append("') target ");
			sqlBuff.append("               full outer join (SELECT division ");
			sqlBuff.append("                                       ,gubun ");
			sqlBuff.append("                                       ,Round(Avg(operationratio), 2) lastMonthAvg ");
			sqlBuff.append("                                FROM   dailyoperationratio ");
			sqlBuff.append("                                WHERE  collectingdate >= '").append(fromLastMonthStr).append("' ");
			sqlBuff.append("                                       AND collectingdate <= '").append(toLastMonthStr).append("' ");
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,gubun) lastMonthAvgTbl ");
			sqlBuff.append("                            ON target.division = lastMonthAvgTbl.division and target.gubun = lastMonthAvgTbl.gubun ");
			sqlBuff.append("               full outer join (SELECT division ");
			sqlBuff.append("                                       ,gubun ");
			sqlBuff.append("                                       ,Round(Avg(operationratio), 2) monthAvg ");
			sqlBuff.append("                                FROM   dailyoperationratio ");
			sqlBuff.append("                                WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("                                       AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,gubun) monthAvgTbl ");
			sqlBuff.append("                            ON lastMonthAvgTbl.division = monthAvgTbl.division and lastMonthAvgTbl.gubun = monthAvgTbl.gubun) tbl ");
			sqlBuff.append("       full outer join (SELECT division ,gubun ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("            ,Max(Decode(collectingdate, '").append(i).append("', operationratio)) as \"C").append((i+"").substring(6, 8)).append("\" ");
			}
			sqlBuff.append("                        FROM   dailyoperationratio ");
			sqlBuff.append("                        WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("                               AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("                        GROUP  BY division ");
			sqlBuff.append("                                  ,gubun) dailyTbl ");
			sqlBuff.append("                    ON tbl.division = dailyTbl.division and tbl.gubun = dailyTbl.gubun ");
			sqlBuff.append("		order by division desc, gubun asc ");	
			
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
			
			sqlBuff.append("SELECT * FROM (SELECT  case when dailyTbl.division is null then tbl.division ");
			sqlBuff.append("			else dailyTbl.division end as division  ");
			sqlBuff.append("		,case when dailyTbl.devicegroup is null then tbl.devicegroup ");
			sqlBuff.append("			else dailyTbl.devicegroup end as devicegroup  ");
			sqlBuff.append("       ,tbl.customerTat ");
			sqlBuff.append("       ,tbl.targetTat ");
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

			sqlBuff.append("       full outer join (SELECT division ");
			sqlBuff.append("                               ,devicegroup ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("            ,Max(Decode(collectingdate, '").append(i).append("', tatofday)) as \"C").append((i+"").substring(6, 8)).append("\" ");
			}
			sqlBuff.append("                        FROM   (SELECT division ");
			sqlBuff.append("                                       ,devicegroup ");
			sqlBuff.append("                                       ,collectingdate ");
			sqlBuff.append("                                       ,round( Decode( SUM(shipping), 0, 0, (sum(boh) / SUM(shipping))),2) AS tatOfDay ");
			sqlBuff.append("                                FROM   dailyshippingnsales ");
			sqlBuff.append("                                WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("                                       AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,devicegroup ");
			sqlBuff.append("                                          ,collectingdate ");
			sqlBuff.append("                                ORDER  BY collectingdate) ");
			sqlBuff.append("                        GROUP  BY division ");
			sqlBuff.append("                                  ,devicegroup) dailyTbl ");
			sqlBuff.append("                    ON tbl.division = dailyTbl.division and tbl.devicegroup = dailyTbl.devicegroup ");
			
			sqlBuff.append("       left outer join (");
			sqlBuff.append("						SELECT division ");
			sqlBuff.append("       							,devicegroup ");
			sqlBuff.append("       							,Round(Avg(tatofday), 2) AS lastMonthAvgTat ");
			sqlBuff.append("						FROM   (SELECT division ");
			sqlBuff.append("               							,devicegroup ");
			sqlBuff.append("               							,Substr(collectingdate, 0, 6)           AS collectingdate ");
			sqlBuff.append("               							,Round( Decode( SUM(shipping), 0, 0, (SUM(boh) / SUM(shipping))), 2) AS tatOfDay ");
			sqlBuff.append("        						FROM   dailyshippingnsales ");
			sqlBuff.append("        						WHERE  collectingdate >= '").append(fromLastMonthStr).append("' ");
			sqlBuff.append("               						AND collectingdate <= '").append(toLastMonthStr).append("' ");
			sqlBuff.append("        						GROUP  BY division ");
			sqlBuff.append("                  						,devicegroup ");
			sqlBuff.append("                  						,collectingdate ");
			sqlBuff.append(" 						) GROUP  BY division ");
			sqlBuff.append("          							,devicegroup ");
			sqlBuff.append("          							,collectingdate");
			sqlBuff.append(" 					) lastMonthAvgTatTbl ");
			sqlBuff.append("                    ON dailyTbl.division = lastMonthAvgTatTbl.division and dailyTbl.devicegroup = lastMonthAvgTatTbl.devicegroup ");
			sqlBuff.append("       left outer join (");
			sqlBuff.append("						SELECT division ");
			sqlBuff.append("       							,devicegroup ");
			sqlBuff.append("       							,Round(Avg(tatofday), 2) AS monthAvgTat ");
			sqlBuff.append("						FROM   (SELECT division ");
			sqlBuff.append("               							,devicegroup ");
			sqlBuff.append("               							,Substr(collectingdate, 0, 6)           AS collectingdate ");
			sqlBuff.append("               							,Round( Decode( SUM(shipping), 0, 0, (SUM(boh) / SUM(shipping))), 2) AS tatOfDay ");
			sqlBuff.append("        						FROM   dailyshippingnsales ");
			sqlBuff.append("        						WHERE  collectingdate >= '").append(fromDate).append("' ");
			sqlBuff.append("               						AND collectingdate <= '").append(toDate).append("' ");
			sqlBuff.append("        						GROUP  BY division ");
			sqlBuff.append("                  						,devicegroup ");
			sqlBuff.append("                  						,collectingdate ");
			sqlBuff.append(" 						) GROUP  BY division ");
			sqlBuff.append("          							,devicegroup ");
			sqlBuff.append("          							,collectingdate");
			sqlBuff.append("					) monthAvgTatTbl ");
			sqlBuff.append("                    ON dailyTbl.division = monthAvgTatTbl.division and dailyTbl.devicegroup = monthAvgTatTbl.devicegroup ");
			sqlBuff.append("                    ) order by division desc, deviceGroup asc  ");
			
		} else if (method.equalsIgnoreCase("getMonthlyShippingForYear")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			Calendar endCal = Calendar.getInstance();
			endCal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			endCal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) );
			endCal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8))-1);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat monthsdf = new SimpleDateFormat("yyyyMM");
			String toDateStr = sdf.format(endCal.getTime());
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
				String dateStr = monthsdf.format(fromDateCal.getTime());
				sqlBuff.append(" ,round(max(decode(collectingMonth, ").append(dateStr).append(", sumofShipping))/1000,0) as \"C").append(dateStr).append("01\" ");
			}
			
			sqlBuff.append(" ,round(sum(sumofShipping)/1000,0) as \"TOTAL\" ");
			sqlBuff.append(" from  ");
			sqlBuff.append(" ( ");
			sqlBuff.append("      select division, substr(collectingdate,0,6) as collectingmonth, sum(shipping) as sumofshipping ");
			sqlBuff.append("     from ");
			sqlBuff.append("         dailyshippingnsales ");
			sqlBuff.append("     where collectingdate >= '").append(fromDateStr).append("' and collectingdate <= '").append(toDateStr).append("' ");
			sqlBuff.append("     group by division, substr(collectingdate,0,6)  ");
			sqlBuff.append(" ) ");
			sqlBuff.append(" group by division order by division desc ");
			
		} else if (method.equalsIgnoreCase("getMonthlySalesForYear")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			Calendar endCal = Calendar.getInstance();
			endCal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			endCal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) );
			endCal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8))-1);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat monthsdf = new SimpleDateFormat("yyyyMM");
			String toDateStr = sdf.format(endCal.getTime());
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
				String dateStr = monthsdf.format(fromDateCal.getTime());
				sqlBuff.append(" ,round(max(decode(collectingMonth, ").append(dateStr).append(", sumOfSales))/1000000,0) as \"C").append(dateStr).append("01\" ");
			}
			
			sqlBuff.append(" ,round(sum(sumOfSales)/1000000,0) as \"TOTAL\" ");
			sqlBuff.append(" from  ");
			sqlBuff.append(" ( ");
			sqlBuff.append("     select division, substr(collectingdate,0,6) as collectingmonth, sum(salesOfDay) as sumofsales ");
			sqlBuff.append("     from ");
			sqlBuff.append("         dailyshippingnsales ");
			sqlBuff.append("     where collectingdate >= '").append(fromDateStr).append("' and collectingdate <= '").append(toDateStr).append("' ");
			sqlBuff.append("     group by division, substr(collectingdate,0,6) ");
			sqlBuff.append(" ) ");
			sqlBuff.append(" group by division order by division desc");
			
		} else if (method.equalsIgnoreCase("getMonthlyCapacityPkgForYear")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			Calendar endCal = Calendar.getInstance();
			endCal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			endCal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)));
			endCal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8))-1);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat monthsdf = new SimpleDateFormat("yyyyMM");
			String toDateStr = sdf.format(endCal.getTime());
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
				//sqlBuff.append(" ,round(max(decode(collectingMonth, ").append(dateStr).append(", monthlycapashipping))/1000,0) as \"C").append(dateStr).append("\" ");
				sqlBuff.append(" ,max(decode(collectingMonth, ").append(dateStr).append(", monthlycapashipping)) as \"C").append(dateStr).append("\" ");
			}
			
			sqlBuff.append("        ,round(SUM(monthlyCapaShipping)/1000,0) as TOTAL ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,collectingmonth ");
			sqlBuff.append("                       ,SUM(planofshipping) monthlyCapaShipping ");
			sqlBuff.append("                FROM   planofmonthlycapacity ");
			sqlBuff.append("                WHERE  division = 'PKG' ");
			sqlBuff.append("     			AND collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("' ");
			sqlBuff.append("                GROUP  BY division, ");
			sqlBuff.append("                          collectingmonth) ");
			sqlBuff.append("        GROUP  BY division ");
			sqlBuff.append("        UNION ALL ");
			sqlBuff.append("        SELECT division ");
			sqlBuff.append("               ,'Shipping'                                                  AS gubun ");
			
			for (int i = 0; i < 12; i++) {
				fromDateCal2.add(Calendar.MONTH, 1);
				String dateStr = monthsdf.format(fromDateCal2.getTime());
				sqlBuff.append(" ,round(max(decode(collectingMonth, ").append(dateStr).append(", sumofShipping))/1000,0) as \"C").append(dateStr).append("01\" ");
			}
			sqlBuff.append(" ,round(sum(sumofShipping)/1000,0) as \"TOTAL\" ");
			sqlBuff.append(" from  ");
			sqlBuff.append(" ( ");
			sqlBuff.append("     select division, substr(collectingdate,0,6) as collectingmonth, sum(shipping) as sumofshipping ");
			sqlBuff.append("     from ");
			sqlBuff.append("         dailyshippingnsales ");
			sqlBuff.append("     where division = 'PKG' ");
			sqlBuff.append("     and collectingdate >= '").append(fromDateStr).append("' and collectingdate <= '").append(toDateStr).append("' ");
			sqlBuff.append("     group by division, substr(collectingdate,0,6) ");
			sqlBuff.append(" ) ");
			sqlBuff.append(" group by division ");
			
		} else if (method.equalsIgnoreCase("getMonthlyCapacityPkgForYearByGroup")) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(month.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(month.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(month.substring(6,8)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String toDateStr = sdf.format(cal.getTime());
			cal.add(Calendar.MONTH, -12);
			String fromDateStr = sdf.format(cal.getTime());
			
			Calendar fromDateCal = Calendar.getInstance();
			fromDateCal.set(Calendar.YEAR, Integer.parseInt(fromDateStr.substring(0, 4)));
			fromDateCal.set(Calendar.MONTH, Integer.parseInt(fromDateStr.substring(4, 6)) -1);
			fromDateCal.set(Calendar.DATE, Integer.parseInt(fromDateStr.substring(6,8)));
			
			sqlBuff.append("SELECT tbl.division ");
			sqlBuff.append("       ,tbl.gubun as deviceGroup ");
			
			for (int i = 0; i < 12; i++) {
				fromDateCal.add(Calendar.MONTH, 1);
				String dateStr = sdf.format(fromDateCal.getTime());
				sqlBuff.append(" ,round(max(decode(tbl.collectingMonth, ").append(dateStr).append(", capavalue))/1000,0) as \"C").append(dateStr).append("\" ");
			}
			
			sqlBuff.append("FROM   (SELECT capa.division ");
			sqlBuff.append("               ,capa.gubun ");
			sqlBuff.append("               ,capa.collectingmonth ");
			sqlBuff.append("               ,round(Nvl(capa.planofshipping, 0) ");
			sqlBuff.append("                ||'_' ");
			sqlBuff.append("                || Nvl(shipping.sumofshipping, 0) ");
			sqlBuff.append("                ||'_' ");
			sqlBuff.append("                || Nvl(Round(( ( shipping.sumofshipping / capa.planofshipping ) * 100 ), 2), 0)/1000,0) AS capaValue ");
			sqlBuff.append("        FROM   (SELECT * ");
			sqlBuff.append("                FROM   planofmonthlycapacity ");
			sqlBuff.append("                WHERE  division = 'pkg' ");
			sqlBuff.append("                       AND collectingmonth >= '").append(fromDateStr).append("' ");
			sqlBuff.append("                       AND collectingmonth <= '").append(toDateStr).append("') capa ");
			sqlBuff.append("               left outer join (SELECT division ");
			sqlBuff.append("                                       ,devicegroup ");
			sqlBuff.append("                                       ,collectingmonth ");
			sqlBuff.append("                                       ,SUM(shipping) AS sumOfShipping ");
			sqlBuff.append("                                FROM   (SELECT division ");
			sqlBuff.append("                                               ,devicegroup ");
			sqlBuff.append("                                               ,Substr(collectingdate, 0, 6) ");
			sqlBuff.append("                                                || '01' AS collectingMonth ");
			sqlBuff.append("                                               ,shipping ");
			sqlBuff.append("                                        FROM   dailyshippingnsales ");
			sqlBuff.append("                                        WHERE  division = 'pkg' ");
			sqlBuff.append("                                               AND collectingdate >= '").append(fromDateStr).append("' ");
			sqlBuff.append("                                               AND collectingdate <= '").append(toDateStr).append("') ");
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,devicegroup ");
			sqlBuff.append("                                          ,collectingmonth ");
			sqlBuff.append("                                ORDER  BY collectingmonth) shipping ");
			sqlBuff.append("                            ON capa.gubun = shipping.devicegroup ");
			sqlBuff.append("                               AND capa.collectingmonth = shipping.collectingmonth) tbl ");
			sqlBuff.append("GROUP  BY tbl.division ");
			sqlBuff.append("          ,tbl.gubun order by tbl.division desc, tbl.gugun asc ");
			
		} else if (method.equalsIgnoreCase("getMonthlyShipping")) {
			
			int fromDateStr = Integer.parseInt(month.substring(0, 4) + "0101");
			int toDateStr = Integer.parseInt(month.substring(0, 4) + "1231");
			
			sqlBuff.append("SELECT division ");
			
			for (int i = fromDateStr; i <= toDateStr; i = i + 100) {
				sqlBuff.append("       ,NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.collectingmonth))/1000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.planshipping))/1000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.focplanshipping))/1000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.sumofshipping))/1000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithplan)),2), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithfocplan)),2), 0) AS C").append((i+"").substring(4, 6)).append(" ");
			}
			
			sqlBuff.append("FROM   (SELECT case when pln.division is null then (case when foc.division is null then ship.division else foc.division end) else pln.division end as division ");
			sqlBuff.append("               ,case when pln.collectingMonth is null then (case when foc.collectingMonth is null then ship.collectingMonth else foc.collectingMonth end) else pln.collectingMonth end as collectingMonth ");
			sqlBuff.append("               ,pln.planShipping ");
			sqlBuff.append("               ,foc.focshipping AS focPlanShipping ");
			sqlBuff.append("               ,ship.sumofshipping as sumofshipping ");
			sqlBuff.append("               ,NVL( Decode(pln.planshipping, 0, 0, ((ship.sumofshipping / pln.planshipping) * 100)), 0) as perWithPlan ");
			sqlBuff.append("               ,NVL( Decode(foc.focshipping, 0, 0, ((ship.sumofshipping / foc.focshipping) * 100)), 0) as  perWithFocPlan ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,collectingmonth ");
			sqlBuff.append("                       ,SUM(shippingplanofmonth) planShipping ");
			sqlBuff.append("                FROM   sw_planofmanagement ");
			sqlBuff.append("                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                GROUP  BY division ");
			sqlBuff.append("                          ,collectingmonth) pln ");
			sqlBuff.append("               full outer join (SELECT division ");
			sqlBuff.append("                                       ,collectingmonth ");
			sqlBuff.append("                                       ,SUM(shippingplanofmonth) focShipping ");
			sqlBuff.append("                                FROM   sw_planofforecast ");

			sqlBuff.append("                                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,collectingmonth) foc ");
			sqlBuff.append("                            ON pln.division = foc.division ");
			sqlBuff.append("                               AND pln.collectingmonth = foc.collectingmonth ");
			sqlBuff.append("               full outer join (SELECT division ");
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
			sqlBuff.append("GROUP  BY division order by division desc ");
			
		} else if (method.equalsIgnoreCase("getMonthlySales")) {
			
			int fromDateStr = Integer.parseInt(month.substring(0, 4) + "0101");
			int toDateStr = Integer.parseInt(month.substring(0, 4) + "1231");
			
			sqlBuff.append("SELECT division ");
			
			for (int i = fromDateStr; i <= toDateStr; i = i + 100) {
				sqlBuff.append("       ,NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.collectingmonth))/1000000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.plansales))/1000000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.focplansales))/1000000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.sumofsales))/1000000,0), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithplan)),2), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(round(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithfocplan)),2), 0) AS C").append((i+"").substring(4, 6)).append(" ");
			}
			
			sqlBuff.append("FROM   (SELECT case when pln.division is null then (case when foc.division is null then ship.division else foc.division end) else pln.division end as division ");
			sqlBuff.append("               ,case when pln.collectingMonth is null then (case when foc.collectingMonth is null then ship.collectingMonth else foc.collectingMonth end) else pln.collectingMonth end as collectingMonth ");
			sqlBuff.append("               ,pln.plansales ");
			sqlBuff.append("               ,foc.focsales AS focPlansales ");
			sqlBuff.append("               ,ship.sumofsales as sumofsales ");
			sqlBuff.append("               ,Round( Decode(pln.plansales, 0, 0, ((ship.sumofsales / pln.plansales) * 100)), 2) as perWithPlan ");
			sqlBuff.append("               ,Round( Decode(foc.focsales, 0, 0, ((ship.sumofsales / foc.focsales) * 100)), 2) as  perWithFocPlan ");
			sqlBuff.append("        FROM   (SELECT division ");
			sqlBuff.append("                       ,collectingmonth ");
			sqlBuff.append("                       ,SUM(salesplanofmonth) plansales ");
			sqlBuff.append("                FROM   sw_planofmanagement ");
			sqlBuff.append("                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                GROUP  BY division ");
			sqlBuff.append("                          ,collectingmonth) pln ");
			sqlBuff.append("               full outer join (SELECT division ");
			sqlBuff.append("                                       ,collectingmonth ");
			sqlBuff.append("                                       ,SUM(salesplanofmonth) focsales ");
			sqlBuff.append("                                FROM   sw_planofforecast ");

			sqlBuff.append("                                WHERE collectingMonth >= '").append(fromDateStr).append("' AND collectingMonth <= '").append(toDateStr).append("'");
			
			sqlBuff.append("                                GROUP  BY division ");
			sqlBuff.append("                                          ,collectingmonth) foc ");
			sqlBuff.append("                            ON pln.division = foc.division ");
			sqlBuff.append("                               AND pln.collectingmonth = foc.collectingmonth ");
			sqlBuff.append("               full outer join (SELECT division ");
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
			sqlBuff.append("GROUP  BY division order by division desc ");
			
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
