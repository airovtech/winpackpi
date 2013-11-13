/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2013. 11. 6.
 * =========================================================
 * Copyright (c) 2013 ManinSoft, Inc. All rights reserved.
 */

package util;

import java.sql.ResultSet;
import java.util.ArrayList;
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
		if (addSummeryRow(operation, resultListMap) != null) {
			resultListMap = addSummeryRow(operation, resultListMap);
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
				} else if (value.equalsIgnoreCase("생산실적")) {
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
				if (shippingMap != null && shippingMap.get(capaKey) != null && capaValue != null) {
					result = ((Float.parseFloat((String)shippingMap.get(capaKey)) / Float.parseFloat(capaValue))*100) + "";
				}
				acheMap.put(capaKey, result);
			}
			resultListMap.add(acheMap);
			
			return resultListMap;
			
		} else if (method.equalsIgnoreCase("getMonthlyShipping")) {
			
			
			List<Map> returnList = new ArrayList<Map>();
			
			for (int i = 0; i < resultListMap.size(); i++) {

				Map<String, String> resultMap = resultListMap.get(i);
				Iterator itr = resultMap.keySet().iterator();
				
				Map planMap = new HashMap();
				Map forecastMap = new HashMap();
				Map shippingMap = new HashMap();
				Map perWithPlanMap = new HashMap();
				Map perWithForecastMap = new HashMap();
				
				while (itr.hasNext()) {
					String key = (String)itr.next();
					String value = (String)resultMap.get(key);
					
					if (key.equals("DIVISION")) {
						planMap.put(key, value);
						planMap.put("GUBUN", "PLAN");
						
						forecastMap.put(key, value);
						forecastMap.put("GUBUN", "Forecast");
						
						shippingMap.put(key, value);
						shippingMap.put("GUBUN", "생산실적");
						
						perWithPlanMap.put(key, value);
						perWithPlanMap.put("GUBUN", "PLAN vs 실적(%)");
						
						perWithForecastMap.put(key, value);
						perWithForecastMap.put("GUBUN", "Forecast vs 실적(%)");
						
					} else {
						String[] values = StringUtils.tokenizeToStringArray(value, "_");
						planMap.put(key, values[1]);
						forecastMap.put(key, values[2]);
						shippingMap.put(key, values[3]);
						perWithPlanMap.put(key, values[4]);
						perWithForecastMap.put(key, values[5]);
						
					}
				}
				returnList.add(planMap);
				returnList.add(forecastMap);
				returnList.add(shippingMap);
				returnList.add(perWithPlanMap);
				returnList.add(perWithForecastMap);
			}
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
			put("SHIPPINGPLAN","PLAN(월).생산");put("SALESPLAN","PLAN(월).매출");
			put("SHIPPINGFOCPLAN","FORECAST(월).생산");put("SALESFOCPLAN","FORECAST(월).매출");
			put("SHIPPINGEXEPLAN","실행계획(월).생산");put("SALESEXEPLAN","실행계획(월).매출");
			put("BOH","MOVEMENT.기초");put("SUMOFRECEIVING","MOVEMENT.입고");put("SUMOFSHIPPING","MOVEMENT.출하");put("WIP","MOVEMENT.WIP");
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
			put("TARGETOR","목표가동율");put("LASTMONTHAVG","전월가동율");	put("MONTHAVG","금월누적가동율");
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
			
			GroupHeader gh1 = new GroupHeader("PLAN(월).생산", 2, 2, "PLAN(월)");
			GroupHeader gh2 = new GroupHeader("FORECAST(월).생산", 4, 2, "FORECAST(월)" );
			GroupHeader gh3 = new GroupHeader("실행계획(월).생산", 6, 2, "실행계획(월)");
			GroupHeader gh4 = new GroupHeader("MOVEMENT.기초", 8, 4, "MOVEMENT");
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
					} else if (columnName.equalsIgnoreCase("TOTAL")) {
						rowMap.put("TOTAL", value);
						groupNames[i] = "TOTAL"; 
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
					} else if (columnName.equalsIgnoreCase("TOTAL")) {
						rowMap.put("TOTAL", value);
						groupNames[i] = "TOTAL"; 
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
