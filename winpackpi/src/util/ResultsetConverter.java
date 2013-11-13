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
				} else if (value.equalsIgnoreCase("�������")) {
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
					acheMap.put(capaKey, "�޼���");
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
						shippingMap.put("GUBUN", "�������");
						
						perWithPlanMap.put(key, value);
						perWithPlanMap.put("GUBUN", "PLAN vs ����(%)");
						
						perWithForecastMap.put(key, value);
						perWithForecastMap.put("GUBUN", "Forecast vs ����(%)");
						
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
			put("C01","1��");put("C02","2��");put("C03","3��");put("C04","4��");put("C05","5��");	put("C06","6��");put("C07","7��");
			put("C08","8��");put("C09","9��");put("C10","10��");put("C11","11��");put("C12","12��");put("C13","13��");put("C14","14��");
			put("C15","15��");put("C16","16��");put("C17","17��");put("C18","18��");put("C19","19��");put("C20","20��");put("C21","21��");
			put("C22","22��");put("C23","23��");put("C24","24��");put("C25","25��");put("C26","26��");put("C27","27��");put("C28","28��");
			put("C29","29��");put("C30","30��");	put("C31","31��");
		}
	};
	public static final Map<String, String> getDailyShippingNSalesColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","�����");put("DEVICEGROUP","����");
			put("SHIPPINGPLAN","PLAN(��).����");put("SALESPLAN","PLAN(��).����");
			put("SHIPPINGFOCPLAN","FORECAST(��).����");put("SALESFOCPLAN","FORECAST(��).����");
			put("SHIPPINGEXEPLAN","�����ȹ(��).����");put("SALESEXEPLAN","�����ȹ(��).����");
			put("BOH","MOVEMENT.����");put("SUMOFRECEIVING","MOVEMENT.�԰�");put("SUMOFSHIPPING","MOVEMENT.����");put("WIP","MOVEMENT.WIP");
			put("PERSHIPPING","����.���ϴ޼���");	put("SUMOFSALES","����.����");put("PERSALES","����.����޼���");
		}
	};
	public static final Map<String, String> getDailyShippingColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","�����");put("DEVICEGROUP","����");
			put("PLANOFSHIPPING","�����ȹ.�ݿ�");put("AVGPLANOFDAY","�����ȹ.�����");
			put("TOTALSUM","���Ͻ���.MTD");put("AVGOFDAY","���Ͻ���.�����");put("PERSHIPPING","���Ͻ���.�޼���");
			put("WIP","WIP");
		}
	};
	public static final Map<String, String> getDailySalesColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","�����");put("DEVICEGROUP","����");
			put("PLANOFSALES","�����ȹ.�ݿ�");put("AVGPLANOFDAY","�����ȹ.�����");
			put("TOTALSUM","���Ͻ���.MTD");put("AVGOFDAY","���Ͻ���.�����");put("PERSALES","���Ͻ���.�޼���");
			put("WIP","WIP");
		}
	};
	public static final Map<String, String> getDailyOperationRatioColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","�����");put("GUBUN","����");
			put("TARGETOR","��ǥ������");put("LASTMONTHAVG","����������");	put("MONTHAVG","�ݿ�����������");
		}
	};
	public static final Map<String, String> getDailyTatColumnMapper = new HashMap<String, String>() {
		{
			put("DIVISION","�����");put("DEVICEGROUP","����");
			put("CUSTOMERTAT","��TAT");put("TARGETTAT","��ǥTAT");	put("LASTMONTHAVGTAT","�������TAT");put("MONTHAVGTAT","�ݿ����TAT");
		}
	};
	
	public static Data convertToData(String operation, ResultSet rs, String month) throws Exception {
		
		Data data = new Data();
		
		if (operation.equalsIgnoreCase("getDailyShippingNSales")) {
			
			GroupHeader gh1 = new GroupHeader("PLAN(��).����", 2, 2, "PLAN(��)");
			GroupHeader gh2 = new GroupHeader("FORECAST(��).����", 4, 2, "FORECAST(��)" );
			GroupHeader gh3 = new GroupHeader("�����ȹ(��).����", 6, 2, "�����ȹ(��)");
			GroupHeader gh4 = new GroupHeader("MOVEMENT.����", 8, 4, "MOVEMENT");
			GroupHeader gh5 = new GroupHeader("����.���ϴ޼���", 12, 3, "����");
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
			data.setyGroupName("�����");
			
		} else if (operation.equalsIgnoreCase("getDailyShipping")) {
			
			GroupHeader gh1 = new GroupHeader("�����ȹ.�ݿ�", 2, 2, "�����ȹ");
			GroupHeader gh2 = new GroupHeader("���Ͻ���.MTD", 4, 3, "���Ͻ���" );
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
			data.setyGroupName("�����");
			
		} else if (operation.equalsIgnoreCase("getDailySales")) {
			
			GroupHeader gh1 = new GroupHeader("�����ȹ.�ݿ�", 2, 2, "�����ȹ");
			GroupHeader gh2 = new GroupHeader("���Ͻ���.MTD", 4, 3, "���Ͻ���" );
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
			data.setyGroupName("�����");
			
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
			data.setyGroupName("�����");
			
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
			data.setyGroupName("�����");
			
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
						rowMap.put("�����", value);
						groupNames[i] = "�����"; 
					} else if (columnName.equalsIgnoreCase("TOTAL")) {
						rowMap.put("TOTAL", value);
						groupNames[i] = "TOTAL"; 
					} else {
						rowMap.put(columnName.substring(1, 5) + "��" + columnName.substring(5,7) + "��" , value);
						groupNames[i] = columnName.substring(1, 5) + "��" + columnName.substring(5,7) + "��";
					}
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("�����");
			
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
						rowMap.put("�����", value);
						groupNames[i] = "�����"; 
					} else if (columnName.equalsIgnoreCase("TOTAL")) {
						rowMap.put("TOTAL", value);
						groupNames[i] = "TOTAL"; 
					} else {
						rowMap.put(columnName.substring(1, 5) + "��" + columnName.substring(5,7) + "��" , value);
						groupNames[i] = columnName.substring(1, 5) + "��" + columnName.substring(5,7) + "��";
					}
				}
				resultListMap.add(rowMap);
			}
			
			data.setGroupHeaders(groupHeaders);
			data.setGroupNames(groupNames);
			data.setValues(resultListMap);
			data.setyGroupName("�����");
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
