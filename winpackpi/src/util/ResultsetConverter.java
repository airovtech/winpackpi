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
					resultMap.put(key, "�������");
					shippingMap = resultMap;
				}
			}
			if (capacityMap == null)
				return null;
			Map acheMap = new HashMap();
			Iterator capaItr = capacityMap.keySet().iterator();
			DecimalFormat df = new DecimalFormat("#.##");
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
				if (shippingMap != null && shippingMap.get(capaKey) != null && capaValue != null && Float.parseFloat((String)shippingMap.get(capaKey))>0 && Float.parseFloat(capaValue)>0) {
					result = df.format((Float.parseFloat((String)shippingMap.get(capaKey)) / Float.parseFloat(capaValue))*100) + "";
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
				shippingMap.put("GUBUN","�������");
				
				Map perShippingMap = new HashMap();
				perShippingMap.put("GUBUN", "�޼���");
				
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
			
			//����� ��ü YTD�� ���ϱ� ����
			int allDivisionPlanYtdSum = 0;
			int allDivisionForecastYtdSum = 0;
			int allDivisionShippingYtdSum = 0;
			
			//����� ��ü TOTAL�� ���ϱ� ����
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
				
				//����κ� YTD�� ���ϱ�����
				int planYtdSum = 0;
				int forecastYtdSum = 0;
				int shippingYtdSum = 0;
				
				//����κ� TOTAL�� ���ϱ� ����
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
						shippingMap.put("GUBUN", "�������");
						if (!shippingTotalMap.containsKey(key)) {
							shippingTotalMap.put("DIVISION", "Total");
							shippingTotalMap.put("GUBUN", "�������");
						}
						
						perWithPlanMap.put(key, value);
						perWithPlanMap.put("GUBUN", "Plan vs ����(%)");
						if (!perWithPlanTotalMap.containsKey(key)) {
							perWithPlanTotalMap.put("DIVISION", "Total");
							perWithPlanTotalMap.put("GUBUN", "Plan vs ����(%)");
						}
						
						perWithForecastMap.put(key, value);
						perWithForecastMap.put("GUBUN", "Forecast vs ����(%)");
						if (!perWithForecastTotalMap.containsKey(key)) {
							perWithForecastTotalMap.put("DIVISION", "Total");
							perWithForecastTotalMap.put("GUBUN", "Forecast vs ����(%)");
						}
						
					} else {
						String[] values = StringUtils.tokenizeToStringArray(value, "_");
						
						//YTD�� ����ϱ� ���Ͽ�...
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
				//����κ� YTD, TOTAL�� ����Ͽ� �����Ѵ�
				planMap.put("YTD", planYtdSum + "");
				forecastMap.put("YTD", forecastYtdSum + "");
				shippingMap.put("YTD", shippingYtdSum + "");
				
				DecimalFormat df = new DecimalFormat("#.##");
				if(shippingYtdSum==0 || planYtdSum==0)
					perWithPlanMap.put("YTD", "0");
				else 
					perWithPlanMap.put("YTD", (df.format(((float)shippingYtdSum/(float)planYtdSum) * 100)) + "");
				if(shippingYtdSum==0 || forecastYtdSum==0)
					perWithForecastMap.put("YTD", "0");
				else
					perWithForecastMap.put("YTD", (df.format(((float)shippingYtdSum/(float)forecastYtdSum) * 100)) + "");
				
				planMap.put("Total", planTotalSum + "");
				forecastMap.put("Total", forecastTotalSum + "");
				shippingMap.put("Total", shippingTotalSum + "");
				if(shippingTotalSum==0 || planTotalSum==0)
					perWithPlanMap.put("Total", "0");
				else
					perWithPlanMap.put("Total", (df.format(((float)shippingTotalSum/(float)planTotalSum) * 100)) + "");
				if(shippingTotalSum==0 || forecastTotalSum==0)
					perWithForecastMap.put("Total", "0");
				else
					perWithForecastMap.put("Total", (df.format(((float)shippingTotalSum/(float)forecastTotalSum) * 100)) + "");
				
				//����� ��ü YTD���� ���ϱ� ����
				allDivisionPlanYtdSum = allDivisionPlanYtdSum + planYtdSum;
				allDivisionForecastYtdSum = allDivisionForecastYtdSum + forecastYtdSum;
				allDivisionShippingYtdSum = allDivisionShippingYtdSum + shippingYtdSum;
				//����� ��ü TOTAL���� ���ϱ� ����
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
			if(allDivisionShippingYtdSum==0 || allDivisionPlanYtdSum==0)
				perWithPlanTotalMap.put("YTD", "0");
			else
				perWithPlanTotalMap.put("YTD", (df.format(((float)allDivisionShippingYtdSum/(float)allDivisionPlanYtdSum) * 100)) + "");
			if(allDivisionShippingYtdSum==0 || allDivisionForecastYtdSum==0)
				perWithForecastTotalMap.put("YTD", "0");
			else
				perWithForecastTotalMap.put("YTD", (df.format(((float)allDivisionShippingYtdSum/(float)allDivisionForecastYtdSum) * 100)) + "");
			

			planTotalMap.put("Total", allDivisionPlanTotalSum);
			forecastTotalMap.put("Total", allDivisionForecastTotalSum);
			shippingTotalMap.put("Total", allDivisionShippingTotalSum);
			if(allDivisionShippingTotalSum==0 || allDivisionPlanTotalSum==0)
				perWithPlanTotalMap.put("Total", "0");
			else
				perWithPlanTotalMap.put("Total", (df.format(((float)allDivisionShippingTotalSum/(float)allDivisionPlanTotalSum) * 100)) + "");
			if(allDivisionShippingTotalSum==0 || allDivisionForecastTotalSum==0)
				perWithForecastTotalMap.put("Total", "0");
			else
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
			
			//����� ��ü YTD�� ���ϱ� ����
			int allDivisionPlanYtdSum = 0;
			int allDivisionForecastYtdSum = 0;
			int allDivisionsalesYtdSum = 0;
			
			//����� ��ü TOTAL�� ���ϱ� ����
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
				
				//����κ� YTD�� ���ϱ�����
				int planYtdSum = 0;
				int forecastYtdSum = 0;
				int salesYtdSum = 0;
				
				//����κ� TOTAL�� ���ϱ� ����
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
						salesMap.put("GUBUN", "�������");
						if (!salesTotalMap.containsKey(key)) {
							salesTotalMap.put("DIVISION", "Total");
							salesTotalMap.put("GUBUN", "�������");
						}
						
						perWithPlanMap.put(key, value);
						perWithPlanMap.put("GUBUN", "Plan vs ����(%)");
						if (!perWithPlanTotalMap.containsKey(key)) {
							perWithPlanTotalMap.put("DIVISION", "Total");
							perWithPlanTotalMap.put("GUBUN", "Plan vs ����(%)");
						}
						
						perWithForecastMap.put(key, value);
						perWithForecastMap.put("GUBUN", "Forecast vs ����(%)");
						if (!perWithForecastTotalMap.containsKey(key)) {
							perWithForecastTotalMap.put("DIVISION", "Total");
							perWithForecastTotalMap.put("GUBUN", "Forecast vs ����(%)");
						}
						
					} else {
						String[] values = StringUtils.tokenizeToStringArray(value, "_");
						
						//YTD�� ����ϱ� ���Ͽ�...
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
				//����κ� YTD, TOTAL�� ����Ͽ� �����Ѵ�
				planMap.put("YTD", planYtdSum + "");
				forecastMap.put("YTD", forecastYtdSum + "");
				salesMap.put("YTD", salesYtdSum + "");
				
				DecimalFormat df = new DecimalFormat("#.##");
				if(salesYtdSum==0 || planYtdSum==0)
					perWithPlanMap.put("YTD", "0");
				else
					perWithPlanMap.put("YTD", (df.format(((float)salesYtdSum/(float)planYtdSum) * 100)) + "");
				if(salesYtdSum==0 || forecastYtdSum==0)
					perWithForecastMap.put("YTD", "0");
				else
					perWithForecastMap.put("YTD", (df.format(((float)salesYtdSum/(float)forecastYtdSum) * 100)) + "");
				
				planMap.put("Total", planTotalSum + "");
				forecastMap.put("Total", forecastTotalSum + "");
				salesMap.put("Total", salesTotalSum + "");
				if(salesTotalSum==0 || planTotalSum==0)
					perWithPlanMap.put("Total", "0");
				else
					perWithPlanMap.put("Total", (df.format(((float)salesTotalSum/(float)planTotalSum) * 100)) + "");
				if(salesTotalSum==0 || forecastTotalSum==0)
					perWithForecastMap.put("Total", "0");
				else
					perWithForecastMap.put("Total", (df.format(((float)salesTotalSum/(float)forecastTotalSum) * 100)) + "");
				
				//����� ��ü YTD���� ���ϱ� ����
				allDivisionPlanYtdSum = allDivisionPlanYtdSum + planYtdSum;
				allDivisionForecastYtdSum = allDivisionForecastYtdSum + forecastYtdSum;
				allDivisionsalesYtdSum = allDivisionsalesYtdSum + salesYtdSum;
				//����� ��ü TOTAL���� ���ϱ� ����
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
			if(allDivisionsalesYtdSum==0 || allDivisionPlanYtdSum==0)
				perWithPlanTotalMap.put("YTD", "0");
			else
				perWithPlanTotalMap.put("YTD", (df.format(((float)allDivisionsalesYtdSum/(float)allDivisionPlanYtdSum) * 100)) + "");
			if(allDivisionsalesYtdSum==0 || allDivisionForecastYtdSum==0)
				perWithForecastTotalMap.put("YTD", "0");
			else
				perWithForecastTotalMap.put("YTD", (df.format(((float)allDivisionsalesYtdSum/(float)allDivisionForecastYtdSum) * 100)) + "");
			

			planTotalMap.put("Total", allDivisionPlanTotalSum);
			forecastTotalMap.put("Total", allDivisionForecastTotalSum);
			salesTotalMap.put("Total", allDivisionsalesTotalSum);
			if(allDivisionsalesTotalSum==0 || allDivisionPlanTotalSum==0)
				perWithPlanTotalMap.put("Total", "0");
			else
				perWithPlanTotalMap.put("Total", (df.format(((float)allDivisionsalesTotalSum/(float)allDivisionPlanTotalSum) * 100)) + "");
			if(allDivisionsalesTotalSum==0 || allDivisionForecastTotalSum==0)
				perWithForecastTotalMap.put("Total", "0");
			else
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
			put("SHIPPINGPLAN","Plan(��).����");put("SALESPLAN","Plan(��).����");
			put("SHIPPINGFOCPLAN","Forecast(��).����");put("SALESFOCPLAN","Forecast(��).����");
			put("SHIPPINGEXEPLAN","�����ȹ(��).����");put("SALESEXEPLAN","�����ȹ(��).����");
			put("BOH","Movement.����");put("SUMOFRECEIVING","Movement.�԰�");put("SUMOFSHIPPING","Movement.����");put("WIP","Movement.WIP");
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
			put("TARGETOR","��ǥ������");put("LASTMONTHAVG","����������");	put("MONTHAVG","�ݿ�������");
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
			
			GroupHeader gh1 = new GroupHeader("Plan(��).����", 2, 2, "Plan(��)");
			GroupHeader gh2 = new GroupHeader("Forecast(��).����", 4, 2, "Forecast(��)" );
			GroupHeader gh3 = new GroupHeader("�����ȹ(��).����", 6, 2, "�����ȹ(��)");
			GroupHeader gh4 = new GroupHeader("Movement.����", 8, 4, "Movement");
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
					} else if (columnName.equalsIgnoreCase("Total")) {
						rowMap.put("Total", value);
						groupNames[i] = "Total"; 
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
					} else if (columnName.equalsIgnoreCase("Total")) {
						rowMap.put("Total", value);
						groupNames[i] = "Total"; 
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
