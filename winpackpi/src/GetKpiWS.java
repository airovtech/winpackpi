
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
	
	//占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占�
	public String getDailyShippingNSales(String month) throws Exception {
		String sql = makeQueryString("getDailyShippingNSales", month);
		return executeQuery("getDailyShippingNSales", sql, month);
	}
	//占싹븝옙占쏙옙占쏙옙占쏙옙
	public String getDailyShipping(String month) throws Exception {
		String sql = makeQueryString("getDailyShipping", month);
		return executeQuery("getDailyShipping", sql, month);
	}
	//占싹븝옙占쏙옙占쏙옙占쏙옙占�
	public String getDailySales(String month) throws Exception {
		String sql = makeQueryString("getDailySales", month);
		return executeQuery("getDailySales", sql, month);
	}
	//占싹븝옙占쏙옙占쏙옙占쏙옙占쏙옙황
	public String getDailyOperationRatio(String month) throws Exception {
		String sql = makeQueryString("getDailyOperationRatio", month);
		return executeQuery("getDailyOperationRatio", sql, month);
	}
	//占싹븝옙TAT占쏙옙황
	public String getDailyTat(String month) throws Exception {
		String sql = makeQueryString("getDailyTat", month);
		return executeQuery("getDailyTat", sql, month);
	}
	//1占썩간占쏙옙占쏙옙占쏙옙占�TREND
	public String getMonthlyShippingForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlyShippingForYear", month);
		return executeQuery("getMonthlyShippingForYear", sql, month);
	}
	//1占썩간占쏙옙占쏙옙占쏙옙占쏙옙TREND
	public String getMonthlySalesForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlySalesForYear", month);
		return executeQuery("getMonthlySalesForYear", sql, month);
	}
	//1占썩간占쏙옙Capacity占쏙옙占쏙옙占쏙옙
	public String getMonthlyCapacityPkgForYear(String month) throws Exception {
		String sql = makeQueryString("getMonthlyCapacityPkgForYear", month);
		return executeQuery("getMonthlyCapacityPkgForYear", sql, month);
	}
	//1占썩간占쏙옙Capacity占쏙옙占쏙옙占쏙옙 占쏙옙키占쏙옙 占쌓룹별
	public String getMonthlyCapacityPkgForYearByGroup(String month) throws Exception {
		String sql = makeQueryString("getMonthlyCapacityPkgForYearByGroup", month);
		return executeQuery("getMonthlyCapacityPkgForYearByGroup", sql, month);
	}
	//占쏙옙占쏙옙占쏙옙占쏙옙占싫�
	public String getMonthlyShipping(String month) throws Exception {
		String sql = makeQueryString("getMonthlyShipping", month);
		return executeQuery("getMonthlyShipping", sql, month);
	}
	//占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙황
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
			sqlBuff.append(" ,round(((tbl.sumOfShipping / ((tbl.shippingExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) as perShipping ");
			sqlBuff.append(" ,round(((tbl.sumOfSales / ((tbl.salesExePlan/").append(daycountOfMonth).append(")* ").append(toDay).append("))*100) ,2) as perSales ");
			sqlBuff.append(" from ");
			sqlBuff.append(" ( ");
			sqlBuff.append("     select baseTbl.*, round(dailySS.boh/1000,0) as boh, round(dailySS.sumOfReceiving/1000,0) as sumOfReceiving, round(dailySS.sumOfShipping/1000,0) as sumOfShipping, round(dailySS.wip/1000,0) as wip, round(dailySS.sumOfSales/1000000,0) as sumOfSales ");
			sqlBuff.append("     from ");
			sqlBuff.append("     ( ");
			sqlBuff.append("         select pln.division, pln.deviceGroup, round(pln.shippingPlan/1000,0) as shippingPlan, round(pln.salesPlan/1000000,0) as salesPlan , round(foc.shippingFocPlan/1000,0) shippingFocPlan, round(foc.salesFocPlan/1000000,0) as salesFocPlan, round(exe.shippingExePlan/1000,0) as shippingExePlan, round(exe.salesExePlan/1000000,0) as salesExePlan ");
			sqlBuff.append("         from ");
			sqlBuff.append("         ( ");
			sqlBuff.append("             select ");
			sqlBuff.append("                 collectingMonth, division, deviceGroup, round(sum(shippingPlanOfMonth)/1000,0) as shippingPlan, round(sum(salesPlanOfMonth)/1000000,0) as salesPlan ");

			sqlBuff.append("             from  ");
			sqlBuff.append("                 sw_planofmanagement ");
			sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
			sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
			sqlBuff.append("         ) pln ");
			
			sqlBuff.append("         left outer join ");
			
			sqlBuff.append("         ( ");
			sqlBuff.append("             select ");
			sqlBuff.append("                 collectingMonth, division, deviceGroup, sum(shippingPlanOfMonth) as shippingFocPlan, sum(salesPlanOfMonth) as salesFocPlan ");
			sqlBuff.append("             from  ");
			sqlBuff.append("                 sw_planOfForecast ");
			sqlBuff.append("             where collectingMonth = '").append(fromDate).append("' ");
			sqlBuff.append("             group by collectingMonth, division, deviceGroup ");
			sqlBuff.append("         ) foc ");
			
			sqlBuff.append("         on pln.devicegroup = foc.devicegroup  AND pln.devicegroup = foc.devicegroup ");
			sqlBuff.append("         left outer join ");
			
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
			
			sqlBuff.append("    select exeplan.division, exeplan.devicegroup, round(exeplan.planOfShipping/1000,0), round(exeplan.avgPlanOfDay/1000,0) ");
			sqlBuff.append("        ,round(dailyShip.totalSum/1000,0) ");
			sqlBuff.append("        ,round(dailyShip.avgOfDay/1000,0) ");
			sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfShipping / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) as perShipping ");
			
			sqlBuff.append("		,(");
			sqlBuff.append("			select round(wip/1000,0) ");
			sqlBuff.append("			from ( ");
			sqlBuff.append("				select devicegroup, sum(wip) as wip from dailyShippingNSales  "); 
			sqlBuff.append("				where collectingDate = '").append(toDate-1).append("'  ");
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
			
			sqlBuff.append("    select exeplan.division, exeplan.devicegroup, round(exeplan.planOfSales/1000000,0), round(exeplan.avgPlanOfDay/1000000,0) ");
			sqlBuff.append("        ,round(dailySales.totalSum/1000000,2) ");
			sqlBuff.append("        ,round(dailySales.avgOfDay/1000000,2) ");
			sqlBuff.append("        ,round(((totalSum / ((exeplan.planOfSales / ").append(daycountOfMonth).append(") * ").append(toDay).append(")) * 100),2) as perSales ");
			
			sqlBuff.append("		,(");
			sqlBuff.append("			select round(wip/1000000,0) ");
			sqlBuff.append("			from ( ");
			sqlBuff.append("				select devicegroup, sum(wip) as wip from dailyShippingNSales  "); 
			sqlBuff.append("				where collectingDate = '").append(toDate-1).append("'  ");
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
			/*sqlBuff.append("FROM   (SELECT division ");
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
			sqlBuff.append("                    ORDER BY tbl.division, tbl.devicegroup ");*/
			
			
			//20131120 占싹븝옙 TAT 占쏙옙占쏙옙 占쏙옙占쏙옙(BOH/SHIPPING)
			sqlBuff.append("FROM   (SELECT division ");
			sqlBuff.append("               ,devicegroup ");
			sqlBuff.append("               ,Avg(customertat) AS customerTat ");
			sqlBuff.append("               ,Avg(targettat)   AS targetTat ");
			sqlBuff.append("        FROM   sw_targettatofmonth ");
			sqlBuff.append("        WHERE  collectingmonth = '").append(month).append("' ");
			sqlBuff.append("        GROUP  BY division ");
			sqlBuff.append("                  ,devicegroup) tbl ");
			
			sqlBuff.append("       left outer join (");
			sqlBuff.append("						SELECT division ");
			sqlBuff.append("       							,devicegroup ");
			sqlBuff.append("       							,Round(Avg(tatofday), 2) AS lastMonthAvgTat ");
			sqlBuff.append("						FROM   (SELECT division ");
			sqlBuff.append("               							,devicegroup ");
			sqlBuff.append("               							,Substr(collectingdate, 0, 6)           AS collectingdate ");
			sqlBuff.append("               							,Round(( SUM(boh) / SUM(shipping) ), 2) AS tatOfDay ");
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
			sqlBuff.append("                    ON tbl.devicegroup = lastMonthAvgTatTbl.devicegroup ");
			sqlBuff.append("       left outer join (");
			sqlBuff.append("						SELECT division ");
			sqlBuff.append("       							,devicegroup ");
			sqlBuff.append("       							,Round(Avg(tatofday), 2) AS monthAvgTat ");
			sqlBuff.append("						FROM   (SELECT division ");
			sqlBuff.append("               							,devicegroup ");
			sqlBuff.append("               							,Substr(collectingdate, 0, 6)           AS collectingdate ");
			sqlBuff.append("               							,Round(( SUM(boh) / SUM(shipping) ), 2) AS tatOfDay ");
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
			sqlBuff.append("                    ON tbl.devicegroup = monthAvgTatTbl.devicegroup ");
			sqlBuff.append("       left outer join (SELECT division ");
			sqlBuff.append("                               ,devicegroup ");
			for (int i = fromDate; i <= toDate; i++) {
				sqlBuff.append("            ,Max(Decode(collectingdate, '").append(i).append("', tatofday)) as \"C").append((i+"").substring(6, 8)).append("\" ");
			}
			sqlBuff.append("                        FROM   (SELECT division ");
			sqlBuff.append("                                       ,devicegroup ");
			sqlBuff.append("                                       ,collectingdate ");
			sqlBuff.append("                                       ,round((sum(boh) / SUM(shipping)),2) AS tatOfDay ");
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
				sqlBuff.append(" ,round(max(decode(collectingMonth, ").append(dateStr).append(", sumofShipping))/1000,0) as \"C").append(dateStr).append("\" ");
			}
			
			sqlBuff.append(" ,round(sum(sumofShipping)/1000,0) as \"TOTAL\" ");
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
				sqlBuff.append(" ,round(max(decode(collectingMonth, ").append(dateStr).append(", sumOfSales))/1000000,0) as \"C").append(dateStr).append("\" ");
			}
			
			sqlBuff.append(" ,round(sum(sumOfSales)/1000000,0) as \"TOTAL\" ");
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
				sqlBuff.append(" ,round(max(decode(collectingMonth, ").append(dateStr).append(", monthlycapashipping))/1000,0) as \"C").append(dateStr).append("\" ");
			}
			
			sqlBuff.append("        ,round(SUM(monthlyCapaShipping)/1000,0) as TOTAL ");
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
			sqlBuff.append("               ,'占쏙옙占쏙옙占쏙옙'                                                  AS gubun ");
			
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
			sqlBuff.append("          ,tbl.gubun ");
			
		} else if (method.equalsIgnoreCase("getMonthlyShipping")) {
			
			int fromDateStr = Integer.parseInt(month.substring(0, 4) + "0101");
			int toDateStr = Integer.parseInt(month.substring(0, 4) + "1201");
			
			sqlBuff.append("SELECT division ");
			
			for (int i = fromDateStr; i <= toDateStr; i = i + 100) {
				sqlBuff.append("       ,round(NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.collectingmonth)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.planshipping)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.focplanshipping)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.sumofshipping)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithplan)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithfocplan)), 0)/1000,0) AS C").append((i+"").substring(4, 6)).append(" ");
			}
			
			sqlBuff.append("FROM   (SELECT pln.* ");
			sqlBuff.append("               ,round(foc.focshipping/1000,0)                                               AS focPlanShipping ");
			sqlBuff.append("               ,round(ship.sumofshipping/1000,0) as sumofshipping ");
			sqlBuff.append("               ,round(Round(( ( ship.sumofshipping / pln.planshipping ) * 100 ), 2)/1000,0) as perWithPlan ");
			sqlBuff.append("               ,round(Round(( ( ship.sumofshipping / foc.focshipping ) * 100 ), 2)/1000,0) as  perWithFocPlan ");
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
				sqlBuff.append("       ,round(NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.collectingmonth)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.plansales)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.focplansales)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.sumofsales)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithplan)), 0) ");
				sqlBuff.append("        ||'_' ");
				sqlBuff.append("        || NVL(Max(Decode(tbl.collectingmonth, '").append(i).append("', tbl.perwithfocplan)), 0)/1000000,0) AS C").append((i+"").substring(4, 6)).append(" ");
			}
			
			sqlBuff.append("FROM   (SELECT pln.* ");
			sqlBuff.append("               ,round(foc.focsales/1000000,0)                                               AS focPlansales ");
			sqlBuff.append("               ,round(ship.sumofsales/1000000,0) as sumofsales ");
			sqlBuff.append("               ,round(Round(( ( ship.sumofsales / pln.plansales ) * 100 ), 2)/1000000,0) as perWithPlan ");
			sqlBuff.append("               ,round(Round(( ( ship.sumofsales / foc.focsales ) * 100 ), 2)/1000000,0) as  perWithFocPlan ");
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
