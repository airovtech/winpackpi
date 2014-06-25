/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2013. 11. 6.
 * =========================================================
 * Copyright (c) 2013 ManinSoft, Inc. All rights reserved.
 */

package util;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import model.Data;
import model.GroupHeader;
import net.sf.json.JSONObject;

public class ResultsetConverter {

	public static String convertToJson(String operation, ResultSet rs, String month) throws Exception {

		List<Map> resultListMap = new ArrayList<Map>();
		List columnNameList = new ArrayList();

		JSONObject json = new JSONObject();
		
		int columnCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			columnNameList.add(rs.getMetaData().getColumnName(i));
		}
		while (rs.next()) {
			Map rowMap = new HashMap();
			for (int i = 0; i < columnNameList.size(); i++) {
				String columnName = (String) columnNameList.get(i);
				String value = rs.getString(columnName);
				rowMap.put(columnName, value);
			}
			resultListMap.add(rowMap);
		}
		
		List<Map> tempList = addSummeryRow(operation, resultListMap);
		if (tempList != null) {
			resultListMap = tempList;
		}
		json.put("rows", resultListMap);
		return json.toString();
	}
	
	public static List<Map> addSummeryRow(String method, List<Map> resultListMap) throws Exception {
		
		if (resultListMap == null)
			return null;
		
		if (method.equalsIgnoreCase("getMonthlyCapacityPkgForYear")) {
			Map capacityMap = null;
			Map shippingMap = null;
			for (int i = 0; i < resultListMap.size(); i++) {
				Map<String, String> resultMap = resultListMap.get(i);
				Iterator itr = resultMap.keySet().iterator();
				String key = null;
				String value = null;
				while (itr.hasNext()) {
					key = (String)itr.next();
					value = resultMap.get(key);
					if (key.equalsIgnoreCase("GUBUN")) {
						break;
					}
				}
				if (value.equalsIgnoreCase("Capacity")) {
					capacityMap = resultMap;
				} else if (value.equalsIgnoreCase("Shipping")) {
					resultMap.remove(key);
					resultMap.put(key, "생산실적");
					shippingMap = resultMap;
				}
			}
			if (capacityMap == null)
				return null;
			Map acheMap = new HashMap();
			Iterator capaItr = capacityMap.keySet().iterator();
			while (capaItr.hasNext()) {
				String capaKey = (String)capaItr.next();
				String capaValue = (String)capacityMap.get(capaKey);
				if (capaKey.equalsIgnoreCase("division")) {
					acheMap.put(capaKey, capaValue);
					continue;
				} else if (capaKey.equalsIgnoreCase("gubun")) {
					acheMap.put(capaKey, "달성율");
					continue;
				}
				String result = null;
				if (shippingMap != null && shippingMap.get(capaKey) != null && capaValue != null && Float.parseFloat((String)shippingMap.get(capaKey))>0 && Float.parseFloat(capaValue)>0) {
					result = ((Float.parseFloat((String)shippingMap.get(capaKey)) / Float.parseFloat(capaValue))*100) + "";
				}
				acheMap.put(capaKey, result);
			}
			resultListMap.add(acheMap);
			
			return resultListMap;
			
		} else if(method.equalsIgnoreCase("getMonthlyCapacityPkgForYearByGroup")) { 
		
			List<Map> returnList = new ArrayList<Map>();
			
			for (int i = 0; i < resultListMap.size(); i++) {
				Map<String, String> resultMap = resultListMap.get(i);
				Iterator itr = resultMap.keySet().iterator();
				
				Map capaMap = new HashMap();
				capaMap.put("GUBUN", "Capacity");
				
				Map shippingMap = new HashMap();
				shippingMap.put("GUBUN","생산실적");
				
				Map perShippingMap = new HashMap();
				perShippingMap.put("GUBUN", "달성율");
				
				while (itr.hasNext()) {
					String key = (String)itr.next();
					String value = resultMap.get(key);	
				
					if (key.equalsIgnoreCase("DIVISION")) {
						capaMap.put("DIVISION", value);
						shippingMap.put("DIVISION", value);
						perShippingMap.put("DIVISION", value);
					} else if (key.equalsIgnoreCase("DEVICEGROUP")) {
						capaMap.put("DEVICEGROUP", value);
						shippingMap.put("DEVICEGROUP", value);
						perShippingMap.put("DEVICEGROUP", value);
					} else {
						String[] values = StringUtils.tokenizeToStringArray(value, "_");
						if (values == null)
							continue;
						capaMap.put(key, values[0]);
						shippingMap.put(key, values[1]);
						perShippingMap.put(key, values[2]);
					}
				}
				returnList.add(capaMap);
				returnList.add(shippingMap);
				returnList.add(perShippingMap);
			}
			return returnList;
			
		} else if (method.equalsIgnoreCase("getMonthlyShipping")) {
			
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			int thisMonth = Integer.parseInt(sdf.format(cal.getTime()) + "01");
			
			List<Map> returnList = new ArrayList<Map>();
			
			Map planTotalMap = new HashMap();
			Map forecastTotalMap = new HashMap();
			Map shippingTotalMap = new HashMap();
			Map perWithPlanTotalMap = new HashMap();
			Map perWithForecastTotalMap = new HashMap();
			
			//사업부 전체 YTD를 구하기 위한
			int allDivisionPlanYtdSum = 0;
			int allDivisionForecastYtdSum = 0;
			int allDivisionShippingYtdSum = 0;
			
			//사업부 전체 TOTAL을 구하기 위한
			int allDivisionPlanTotalSum = 0;
			int allDivisionForecastTotalSum = 0;
			int allDivisionShippingTotalSum = 0;
			
			for (int i = 0; i < resultListMap.size(); i++) {

				Map<String, String> resultMap = resultListMap.get(i);
				Iterator itr = resultMap.keySet().iterator();
				
				Map planMap = new HashMap();
				Map forecastMap = new HashMap();
				Map shippingMap = new HashMap();
				Map perWithPlanMap = new HashMap();
				Map perWithForecastMap = new HashMap();
				
				//사업부별 YTD를 구하기위한
				int planYtdSum = 0;
				int forecastYtdSum = 0;
				int shippingYtdSum = 0;
				
				//사업부별 TOTAL을 구하기 위한
				int planTotalSum = 0;
				int forecastTotalSum = 0;
				int shippingTotalSum = 0;
				
				while (itr.hasNext()) {
					String key = (String)itr.next();
					String value = (String)resultMap.get(key);
					
					if (key.equals("DIVISION")) {
						planMap.put(key, value);
						planMap.put("GUBUN", "Plan");
						if (!planTotalMap.containsKey(key)) {
							planTotalMap.put("DIVISION", "Total");
							planTotalMap.put("GUBUN", "Plan");
						}
						
						forecastMap.put(key, value);
						forecastMap.put("GUBUN", "Forecast");
						if (!forecastTotalMap.containsKey(key)) {
							forecastTotalMap.put("DIVISION", "Total");
							forecastTotalMap.put("GUBUN", "Forecast");
						}
						
						shippingMap.put(key, value);
						shippingMap.put("GUBUN", "생산실적");
						if (!shippingTotalMap.containsKey(key)) {
							shippingTotalMap.put("DIVISION", "Total");
							shippingTotalMap.put("GUBUN", "생산실적");
						}
						
						perWithPlanMap.put(key, value);
						perWithPlanMap.put("GUBUN", "Plan vs 실적(%)");
						if (!perWithPlanTotalMap.containsKey(key)) {
							perWithPlanTotalMap.put("DIVISION", "Total");
							perWithPlanTotalMap.put("GUBUN", "Plan vs 실적(%)");
						}
						
						perWithForecastMap.put(key, value);
						perWithForecastMap.put("GUBUN", "Forecast vs 실적(%)");
						if (!perWithForecastTotalMap.containsKey(key)) {
							perWithForecastTotalMap.put("DIVISION", "Total");
							perWithForecastTotalMap.put("GUBUN", "Forecast vs 실적(%)");
						}
						
					} else {
						String[] values = StringUtils.tokenizeToStringArray(value, "_");
						
						//YTD를 계산하기 위하여...
						if (Integer.parseInt(values[0]) <= thisMonth) {
							planYtdSum = planYtdSum + Integer.parseInt(values[1]);
							forecastYtdSum = forecastYtdSum + Integer.parseInt(values[2]);
							shippingYtdSum = shippingYtdSum + Integer.parseInt(values[3]);
						}
						
						planTotalSum = planTotalSum + Integer.parseInt(values[1]);
						forecastTotalSum = forecastTotalSum + Integer.parseInt(values[2]);
						shippingTotalSum = shippingTotalSum + Integer.parseInt(values[3]);
						
						planMap.put(key, values[1]);
						forecastMap.put(key, values[2]);
						shippingMap.put(key, values[3]);
						perWithPlanMap.put(key, values[4]);
						perWithForecastMap.put(key, values[5]);
						
						if (!planTotalMap.containsKey(key)) {
							planTotalMap.put(key, values[1]);
						} else {
							int intValue = Integer.parseInt((String)planTotalMap.get(key));
							planTotalMap.remove(key);
							planTotalMap.put(key, (intValue + Integer.parseInt(values[1])) + "");
						}
						if (!forecastTotalMap.containsKey(key)) {
							forecastTotalMap.put(key, values[2]);
						} else {
							int intValue = Integer.parseInt((String)forecastTotalMap.get(key));
							forecastTotalMap.remove(key);
							forecastTotalMap.put(key, (intValue + Integer.parseInt(values[2])) + "");
						}
						if (!shippingTotalMap.containsKey(key)) {
							shippingTotalMap.put(key, values[3]);
						} else {
							int intValue = Integer.parseInt((String)shippingTotalMap.get(key));
							shippingTotalMap.remove(key);
							shippingTotalMap.put(key, (intValue + Integer.parseInt(values[3])) + "");
						}
						
						
						int planTotal = planTotalMap.get(key) == null ? 0 : Integer.parseInt((String)planTotalMap.get(key));
						int forecastTotal = forecastTotalMap.get(key) == null ? 0 : Integer.parseInt((String)forecastTotalMap.get(key));
						int shippingTotal = shippingTotalMap.get(key) == null ? 0 : Integer.parseInt((String)shippingTotalMap.get(key));
						
						if (planTotal == 0 || shippingTotal == 0) {
							perWithPlanTotalMap.remove(key);
							perWithPlanTotalMap.put(key, "0");
						} else {
							DecimalFormat df = new DecimalFormat("#.##");
							perWithPlanTotalMap.remove(key);
							perWithPlanTotalMap.put(key, (df.format(((float)shippingTotal/(float)planTotal) * 100)) + "");
						}
						
						if (forecastTotal == 0 || shippingTotal == 0) {
							perWithForecastTotalMap.remove(key);
							perWithForecastTotalMap.put(key, "0");
						} else {
							DecimalFormat df = new DecimalFormat("#.##");
							perWithForecastTotalMap.remove(key);
							perWithForecastTotalMap.put(key, (df.format(((float)shippingTotal/(float)forecastTotal) * 100)) + "");
						}
					}
				}
				//사업부별 YTD, TOTAL을 계산하여 삽입한다
				planMap.put("YTD", planYtdSum + "");
				forecastMap.put("YTD", forecastYtdSum + "");
				shippingMap.put("YTD", shippingYtdSum + "");
				
				DecimalFormat df = new DecimalFormat("#.##");
				perWithPlanMap.put("YTD", (df.format(((float)shippingYtdSum/(float)planYtdSum) * 100)) + "");
				perWithForecastMap.put("YTD", (df.format(((float)shippingYtdSum/(float)forecastYtdSum) * 100)) + "");
				
				planMap.put("Total", planTotalSum + "");
				forecastMap.put("Total", forecastTotalSum + "");
				shippingMap.put("Total", shippingTotalSum + "");
				perWithPlanMap.put("Total", (df.format(((float)shippingTotalSum/(float)planTotalSum) * 100)) + "");
				perWithForecastMap.put("Total", (df.format(((float)shippingTotalSum/(float)forecastTotalSum) * 100)) + "");
				
				//사업부 전체 YTD값을 구하기 위한
				allDivisionPlanYtdSum = allDivisionPlanYtdSum + planYtdSum;
				allDivisionForecastYtdSum = allDivisionForecastYtdSum + forecastYtdSum;
				allDivisionShippingYtdSum = allDivisionShippingYtdSum + shippingYtdSum;
				//사업부 전체 TOTAL값을 구하기 위한
				allDivisionPlanTotalSum = allDivisionPlanTotalSum + planTotalSum;
				allDivisionForecastTotalSum = allDivisionForecastTotalSum + forecastTotalSum;
				allDivisionShippingTotalSum = allDivisionShippingTotalSum + shippingTotalSum;
				
				returnList.add(planMap);
				returnList.add(forecastMap);
				returnList.add(shippingMap);
				returnList.add(perWithPlanMap);
				returnList.add(perWithForecastMap);
			}
			
			
			DecimalFormat df = new DecimalFormat("#.##");
			
			planTotalMap.put("YTD", allDivisionPlanYtdSum);
			forecastTotalMap.put("YTD", allDivisionForecastYtdSum);
			shippingTotalMap.put("YTD", allDivisionShippingYtdSum);
			perWithPlanTotalMap.put("YTD", (df.format(((float)allDivisionShippingYtdSum/(float)allDivisionPlanYtdSum) * 100)) + "");
			perWithForecastTotalMap.put("YTD", (df.format(((float)allDivisionShippingYtdSum/(float)allDivisionForecastYtdSum) * 100)) + "");
			

			planTotalMap.put("Total", allDivisionPlanTotalSum);
			forecastTotalMap.put("Total", allDivisionForecastTotalSum);
			shippingTotalMap.put("Total", allDivisionShippingTotalSum);
			perWithPlanTotalMap.put("Total", (df.format(((float)allDivisionShippingTotalSum/(float)allDivisionPlanTotalSum) * 100)) + "");
			perWithForecastTotalMap.put("Total", (df.format(((float)allDivisionShippingTotalSum/(float)allDivisionForecastTotalSum) * 100)) + "");
			
			returnList.add(planTotalMap);
			returnList.add(forecastTotalMap);
			returnList.add(shippingTotalMap);
			returnList.add(perWithPlanTotalMap);
			returnList.add(perWithForecastTotalMap);
			
			return returnList;
		} else if (method.equalsIgnoreCase("getMonthlySales")) {
			
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			int thisMonth = Integer.parseInt(sdf.format(cal.getTime()) + "01");
			
			List<Map> returnList = new ArrayList<Map>();
			
			Map planTotalMap = new HashMap();
			Map forecastTotalMap = new HashMap();
			Map salesTotalMap = new HashMap();
			Map perWithPlanTotalMap = new HashMap();
			Map perWithForecastTotalMap = new HashMap();
			
			//사업부 전체 YTD를 구하기 위한
			int allDivisionPlanYtdSum = 0;
			int allDivisionForecastYtdSum = 0;
			int allDivisionsalesYtdSum = 0;
			
			//사업부 전체 TOTAL을 구하기 위한
			int allDivisionPlanTotalSum = 0;
			int allDivisionForecastTotalSum = 0;
			int allDivisionsalesTotalSum = 0;
			
			for (int i = 0; i < resultListMap.size(); i++) {

				Map<String, String> resultMap = resultListMap.get(i);
				Iterator itr = resultMap.keySet().iterator();
				
				Map planMap = new HashMap();
				Map forecastMap = new HashMap();
				Map salesMap = new HashMap();
				Map perWithPlanMap = new HashMap();
				Map perWithForecastMap = new HashMap();
				
				//사업부별 YTD를 구하기위한
				int planYtdSum = 0;
				int forecastYtdSum = 0;
				int salesYtdSum = 0;
				
				//사업부별 TOTAL을 구하기 위한
				int planTotalSum = 0;
				int forecastTotalSum = 0;
				int salesTotalSum = 0;
				
				while (itr.hasNext()) {
					String key = (String)itr.next();
					String value = (String)resultMap.get(key);
					
					if (key.equals("DIVISION")) {
						planMap.put(key, value);
						planMap.put("GUBUN", "Plan");
						if (!planTotalMap.containsKey(key)) {
							planTotalMap.put("DIVISION", "Total");
							planTotalMap.put("GUBUN", "Plan");
						}
						
						forecastMap.put(key, value);
						forecastMap.put("GUBUN", "Forecast");
						if (!forecastTotalMap.containsKey(key)) {
							forecastTotalMap.put("DIVISION", "Total");
							forecastTotalMap.put("GUBUN", "Forecast");
						}
						
						salesMap.put(key, value);
						salesMap.put("GUBUN", "매출실적");
						if (!salesTotalMap.containsKey(key)) {
							salesTotalMap.put("DIVISION", "Total");
							salesTotalMap.put("GUBUN", "매출실적");
						}
						
						perWithPlanMap.put(key, value);
						perWithPlanMap.put("GUBUN", "Plan vs 실적(%)");
						if (!perWithPlanTotalMap.containsKey(key)) {
							perWithPlanTotalMap.put("DIVISION", "Total");
							perWithPlanTotalMap.put("GUBUN", "Plan vs 실적(%)");
						}
						
						perWithForecastMap.put(key, value);
						perWithForecastMap.put("GUBUN", "Forecast vs 실적(%)");
						if (!perWithForecastTotalMap.containsKey(key)) {
							perWithForecastTotalMap.put("DIVISION", "Total");
							perWithForecastTotalMap.put("GUBUN", "Forecast vs 실적(%)");
						}
						
					} else {
						String[] values = StringUtils.tokenizeToStringArray(value, "_");
						
						//YTD를 계산하기 위하여...
						if (Integer.parseInt(values[0]) <= thisMonth) {
							planYtdSum = planYtdSum + Integer.parseInt(values[1]);
							forecastYtdSum = forecastYtdSum + Integer.parseInt(values[2]);
							salesYtdSum = salesYtdSum + Integer.parseInt(values[3]);
						}
						
						planTotalSum = planTotalSum + Integer.parseInt(values[1]);
						forecastTotalSum = forecastTotalSum + Integer.parseInt(values[2]);
						salesTotalSum = salesTotalSum + Integer.parseInt(values[3]);
						
						planMap.put(key, values[1]);
						forecastMap.put(key, values[2]);
						salesMap.put(key, values[3]);
						perWithPlanMap.put(key, values[4]);
						perWithForecastMap.put(key, values[5]);
						
						if (!planTotalMap.containsKey(key)) {
							planTotalMap.put(key, values[1]);
						} else {
							int intValue = Integer.parseInt((String)planTotalMap.get(key));
							planTotalMap.remove(key);
							planTotalMap.put(key, (intValue + Integer.parseInt(values[1])) + "");
						}
						if (!forecastTotalMap.containsKey(key)) {
							forecastTotalMap.put(key, values[2]);
						} else {
							int intValue = Integer.parseInt((String)forecastTotalMap.get(key));
							forecastTotalMap.remove(key);
							forecastTotalMap.put(key, (intValue + Integer.parseInt(values[2])) + "");
						}
						if (!salesTotalMap.containsKey(key)) {
							salesTotalMap.put(key, values[3]);
						} else {
							int intValue = Integer.parseInt((String)salesTotalMap.get(key));
							salesTotalMap.remove(key);
							salesTotalMap.put(key, (intValue + Integer.parseInt(values[3])) + "");
						}
						
						
						int planTotal = planTotalMap.get(key) == null ? 0 : Integer.parseInt((String)planTotalMap.get(key));
						int forecastTotal = forecastTotalMap.get(key) == null ? 0 : Integer.parseInt((String)forecastTotalMap.get(key));
						int salesTotal = salesTotalMap.get(key) == null ? 0 : Integer.parseInt((String)salesTotalMap.get(key));
						
						if (planTotal == 0 || salesTotal == 0) {
							perWithPlanTotalMap.remove(key);
							perWithPlanTotalMap.put(key, "0");
						} else {
							DecimalFormat df = new DecimalFormat("#.##");
							perWithPlanTotalMap.remove(key);
							perWithPlanTotalMap.put(key, (df.format(((float)salesTotal/(float)planTotal) * 100)) + "");
						}
						
						if (forecastTotal == 0 || salesTotal == 0) {
							perWithForecastTotalMap.remove(key);
							perWithForecastTotalMap.put(key, "0");
						} else {
							DecimalFormat df = new DecimalFormat("#.##");
							perWithForecastTotalMap.remove(key);
							perWithForecastTotalMap.put(key, (df.format(((float)salesTotal/(float)forecastTotal) * 100)) + "");
						}
					}
				}
				//사업부별 YTD, TOTAL을 계산하여 삽입한다
				planMap.put("YTD", planYtdSum + "");
				forecastMap.put("YTD", forecastYtdSum + "");
				salesMap.put("YTD", salesYtdSum + "");
				
				DecimalFormat df = new DecimalFormat("#.##");
				perWithPlanMap.put("YTD", (df.format(((float)salesYtdSum/(float)planYtdSum) * 100)) + "");
				perWithForecastMap.put("YTD", (df.format(((float)salesYtdSum/(float)forecastYtdSum) * 100)) + "");
				
				planMap.put("Total", planTotalSum + "");
				forecastMap.put("Total", forecastTotalSum + "");
				salesMap.put("Total", salesTotalSum + "");
				perWithPlanMap.put("Total", (df.format(((float)salesTotalSum/(float)planTotalSum) * 100)) + "");
				perWithForecastMap.put("Total", (df.format(((float)salesTotalSum/(float)forecastTotalSum) * 100)) + "");
				
				//사업부 전체 YTD값을 구하기 위한
				allDivisionPlanYtdSum = allDivisionPlanYtdSum + planYtdSum;
				allDivisionForecastYtdSum = allDivisionForecastYtdSum + forecastYtdSum;
				allDivisionsalesYtdSum = allDivisionsalesYtdSum + salesYtdSum;
				//사업부 전체 TOTAL값을 구하기 위한
				allDivisionPlanTotalSum = allDivisionPlanTotalSum + planTotalSum;
				allDivisionForecastTotalSum = allDivisionForecastTotalSum + forecastTotalSum;
				allDivisionsalesTotalSum = allDivisionsalesTotalSum + salesTotalSum;
				
				returnList.add(planMap);
				returnList.add(forecastMap);
				returnList.add(salesMap);
				returnList.add(perWithPlanMap);
				returnList.add(perWithForecastMap);
			}
			
			
			DecimalFormat df = new DecimalFormat("#.##");
			
			planTotalMap.put("YTD", allDivisionPlanYtdSum);
			forecastTotalMap.put("YTD", allDivisionForecastYtdSum);
			salesTotalMap.put("YTD", allDivisionsalesYtdSum);
			perWithPlanTotalMap.put("YTD", (df.format(((float)allDivisionsalesYtdSum/(float)allDivisionPlanYtdSum) * 100)) + "");
			perWithForecastTotalMap.put("YTD", (df.format(((float)allDivisionsalesYtdSum/(float)allDivisionForecastYtdSum) * 100)) + "");
			

			planTotalMap.put("Total", allDivisionPlanTotalSum);
			forecastTotalMap.put("Total", allDivisionForecastTotalSum);
			salesTotalMap.put("Total", allDivisionsalesTotalSum);
			perWithPlanTotalMap.put("Total", (df.format(((float)allDivisionsalesTotalSum/(float)allDivisionPlanTotalSum) * 100)) + "");
			perWithForecastTotalMap.put("Total", (df.format(((float)allDivisionsalesTotalSum/(float)allDivisionForecastTotalSum) * 100)) + "");
			
			returnList.add(planTotalMap);
			returnList.add(forecastTotalMap);
			returnList.add(salesTotalMap);
			returnList.add(perWithPlanTotalMap);
			returnList.add(perWithForecastTotalMap);
			
			return returnList;
		}
		return null;
	}
	
	public static final Map<String, String> dayColumnMapper = new HashMap<String, String>() {
		{
			put("C01","1일");put("C02","2일");put("C03","3일");put("C04","4일");put("C05","5일");	put("C06","6일");put("C07","7일");
			put("C08","8일");put("C09","9일");put("C10","10일");put("C11","11일");put("C12","12일");put("C13","13일");put("C14","14일");
			put("C15","15일");put("C16","16일");put("C17","17일");put("C18","18일");put("C19","19일");put("C20","20일");put("C21","21일");
			put("C22","22일");put("C23","23일");put("C24","24일");put("C25","25일");put("C26","26일");put("C27","27일");put("C28","28일");
			put("C29","29일");put("C30","30일");	put("C31","31일");
		}
	};
	public static final Map<String, String> getDailyShippingNSalesColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","사업부");put("DEVICEGROUP","구분");
			put("SHIPPINGPLAN","Plan(월).생산");put("SALESPLAN","Plan(월).매출");
			put("SHIPPINGFOCPLAN","Forecast(월).생산");put("SALESFOCPLAN","Forecast(월).매출");
			put("SHIPPINGEXEPLAN","실행계획(월).생산");put("SALESEXEPLAN","실행계획(월).매출");
			put("BOH","Movement.기초");put("SUMOFRECEIVING","Movement.입고");put("SUMOFSHIPPING","Movement.출하");put("WIP","Movement.WIP");
			put("PERSHIPPING","실적.출하달성율");	put("SUMOFSALES","실적.매출");put("PERSALES","실적.매출달성율");
		}
	};
	public static final Map<String, String> getDailyShippingColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","사업부");put("DEVICEGROUP","구분");
			put("PLANOFSHIPPING","실행계획.금월");put("AVGPLANOFDAY","실행계획.일평균");
			put("TOTALSUM","출하실적.MTD");put("AVGOFDAY","출하실적.일평균");put("PERSHIPPING","출하실적.달성률");
			put("WIP","WIP");
		}
	};
	public static final Map<String, String> getDailySalesColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","사업부");put("DEVICEGROUP","구분");
			put("PLANOFSALES","실행계획.금월");put("AVGPLANOFDAY","실행계획.일평균");
			put("TOTALSUM","출하실적.MTD");put("AVGOFDAY","출하실적.일평균");put("PERSALES","출하실적.달성률");
			put("WIP","WIP");
		}
	};
	public static final Map<String, String> getDailyOperationRatioColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","사업부");put("GUBUN","구분");
			put("TARGETOR","목표가동율");put("LASTMONTHAVG","전월가동율");	put("MONTHAVG","금월가동율");
		}
	};
	public static final Map<String, String> getDailyTatColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","사업부");put("DEVICEGROUP","구분");
			put("CUSTOMERTAT","고객TAT");put("TARGETTAT","목표TAT");	put("LASTMONTHAVGTAT","전월평균TAT");put("MONTHAVGTAT","금월평균TAT");
		}
	};
	
	public static Data convertToData(String operation, ResultSet rs, String month) throws Exception {
		
		Data data = new Data();
		
		if (operation.equalsIgnoreCase("getDailyShippingNSales")) {
			
			GroupHeader gh1 = new GroupHeader("Plan(월).생산", 2, 2, "Plan(월)");
			GroupHeader gh2 = new GroupHeader("Forecast(월).생산", 4, 2, "Forecast(월)" );
			GroupHeader gh3 = new GroupHeader("실행계획(월).생산", 6, 2, "실행계획(월)");
			GroupHeader gh4 = new GroupHeader("Movement.기초", 8, 4, "Movement");
			GroupHeader gh5 = new GroupHeader("실적.출하달성율", 12, 3, "실적");
			GroupHeader[] groupHeaders = new GroupHeader[]{gh1, gh2, gh3, gh4, gh5};
			
			List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();

			int columnCount = rs.getMetaData().getColumnCount();
			String[] columnNameArray = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				columnNameArray[i-1] = (rs.getMetaData().getColumnName(i));
			}
			
			String[] groupNames = new String[columnCount];
			while (rs.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnNameArray.length; i++) {
					String columnName = (String)columnNameArray[i];
					String value = toNotNull(rs.getString(columnName));
					rowMap.put(getDailyShippingNSalesColumnMapper.get(columnName), value);
					groupNames[i] = getDailyShippingNSalesColumnMapper.get(columnName);
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("사업부");
			
		} else if (operation.equalsIgnoreCase("getDailyShipping")) {
			
			GroupHeader gh1 = new GroupHeader("실행계획.금월", 2, 2, "실행계획");
			GroupHeader gh2 = new GroupHeader("출하실적.MTD", 4, 3, "출하실적" );
			GroupHeader[] groupHeaders = new GroupHeader[]{gh1, gh2};
			
			List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();

			int columnCount = rs.getMetaData().getColumnCount();
			String[] columnNameArray = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				columnNameArray[i-1] = (rs.getMetaData().getColumnName(i));
			}
			
			String[] groupNames = new String[columnCount];
			while (rs.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnNameArray.length; i++) {
					String columnName = (String)columnNameArray[i];
					String value = toNotNull(rs.getString(columnName));
					rowMap.put(getDailyShippingColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailyShippingColumnMapper.get(columnName) , value);
					groupNames[i] = getDailyShippingColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailyShippingColumnMapper.get(columnName);
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("사업부");
			
		} else if (operation.equalsIgnoreCase("getDailySales")) {
			
			GroupHeader gh1 = new GroupHeader("실행계획.금월", 2, 2, "실행계획");
			GroupHeader gh2 = new GroupHeader("출하실적.MTD", 4, 3, "출하실적" );
			GroupHeader[] groupHeaders = new GroupHeader[]{gh1, gh2};
			
			List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();

			int columnCount = rs.getMetaData().getColumnCount();
			String[] columnNameArray = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				columnNameArray[i-1] = (rs.getMetaData().getColumnName(i));
			}
			
			String[] groupNames = new String[columnCount];
			while (rs.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnNameArray.length; i++) {
					String columnName = (String)columnNameArray[i];
					String value = toNotNull(rs.getString(columnName));
					rowMap.put(getDailySalesColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailySalesColumnMapper.get(columnName) , value);
					groupNames[i] = getDailySalesColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailySalesColumnMapper.get(columnName);
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("사업부");
			
		} else if (operation.equalsIgnoreCase("getDailyOperationRatio")) {
			
			GroupHeader[] groupHeaders = null;
			
			List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();

			int columnCount = rs.getMetaData().getColumnCount();
			String[] columnNameArray = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				columnNameArray[i-1] = (rs.getMetaData().getColumnName(i));
			}
			
			String[] groupNames = new String[columnCount];
			while (rs.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnNameArray.length; i++) {
					String columnName = (String)columnNameArray[i];
					String value = toNotNull(rs.getString(columnName));
					rowMap.put(getDailyOperationRatioColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailyOperationRatioColumnMapper.get(columnName) , value);
					groupNames[i] = getDailyOperationRatioColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailyOperationRatioColumnMapper.get(columnName);
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("사업부");
			
		} else if (operation.equalsIgnoreCase("getDailyTat")) {
			
			GroupHeader[] groupHeaders = null;
			
			List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();

			int columnCount = rs.getMetaData().getColumnCount();
			String[] columnNameArray = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				columnNameArray[i-1] = (rs.getMetaData().getColumnName(i));
			}
			
			String[] groupNames = new String[columnCount];
			while (rs.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnNameArray.length; i++) {
					String columnName = (String)columnNameArray[i];
					String value = toNotNull(rs.getString(columnName));
					rowMap.put(getDailyTatColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailyTatColumnMapper.get(columnName) , value);
					groupNames[i] = getDailyTatColumnMapper.get(columnName) == null ? dayColumnMapper.get(columnName) : getDailyTatColumnMapper.get(columnName);
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("사업부");
			
		} else if (operation.equalsIgnoreCase("getMonthlyShippingForYear")) {
			
			GroupHeader[] groupHeaders = null;
			
			List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();

			int columnCount = rs.getMetaData().getColumnCount();
			String[] columnNameArray = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				columnNameArray[i-1] = (rs.getMetaData().getColumnName(i));
			}
			
			String[] groupNames = new String[columnCount];
			while (rs.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnNameArray.length; i++) {
					String columnName = (String)columnNameArray[i];
					String value = toNotNull(rs.getString(columnName));
					
					if (columnName.equalsIgnoreCase("DIVISION")) {
						rowMap.put("사업부", value);
						groupNames[i] = "사업부"; 
					} else if (columnName.equalsIgnoreCase("Total")) {
						rowMap.put("Total", value);
						groupNames[i] = "Total"; 
					} else {
						rowMap.put(columnName.substring(1, 5) + "년" + columnName.substring(5,7) + "월" , value);
						groupNames[i] = columnName.substring(1, 5) + "년" + columnName.substring(5,7) + "월";
					}
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("사업부");
			
		} else if (operation.equalsIgnoreCase("getMonthlySalesForYear")) {
			
			GroupHeader[] groupHeaders = null;
			
			List<Map<String, Object>> resultListMap = new ArrayList<Map<String, Object>>();

			int columnCount = rs.getMetaData().getColumnCount();
			String[] columnNameArray = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				columnNameArray[i-1] = (rs.getMetaData().getColumnName(i));
			}
			
			String[] groupNames = new String[columnCount];
			while (rs.next()) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				for (int i = 0; i < columnNameArray.length; i++) {
					String columnName = (String)columnNameArray[i];
					String value = toNotNull(rs.getString(columnName));
					
					if (columnName.equalsIgnoreCase("DIVISION")) {
						rowMap.put("사업부", value);
						groupNames[i] = "사업부"; 
					} else if (columnName.equalsIgnoreCase("Total")) {
						rowMap.put("Total", value);
						groupNames[i] = "Total"; 
					} else {
						rowMap.put(columnName.substring(1, 5) + "년" + columnName.substring(5,7) + "월" , value);
						groupNames[i] = columnName.substring(1, 5) + "년" + columnName.substring(5,7) + "월";
					}
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("사업부");
		}
		
		return data;
		
	}
	private static String toNotNull(Object value) {
		if (value == null)
			return "";
		if (value instanceof String) {
			String valueStr = value.toString();
			if (isEmpty(valueStr))
				return "";
			return valueStr;
		}
		return value.toString();
	}
	private static boolean isEmpty(String str) {
		if (str == null || str.length() == 0)
			return true;
		String tmp = str.trim();
		return tmp.length() == 0 || tmp.equalsIgnoreCase("null") || tmp.equalsIgnoreCase("undefined");
	}
	
}
