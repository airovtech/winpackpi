<%@page import="java.util.HashMap"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="util.WebServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%
	try {
		String method = (String)request.getParameter("method");

		JSONObject json = new JSONObject();
		if (method.equalsIgnoreCase("getMonthlyShippingNSalesForYearColumnName")) {
			
			String yearMonth = (String)request.getParameter("yearMonth");
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(yearMonth.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(yearMonth.substring(4, 6)) -1);
			cal.set(Calendar.DATE, Integer.parseInt(yearMonth.substring(6,8)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String toDateStr = sdf.format(cal.getTime());
			cal.add(Calendar.MONTH, -12);
			String fromDateStr = sdf.format(cal.getTime());
			
			LinkedList<Map> resultListMap = new LinkedList<Map>();
			
			Map colNameMap = new HashMap();
			
			colNameMap.put("C_0", "DIVISION");			
			
			Calendar fromDateCal = Calendar.getInstance();
			fromDateCal.set(Calendar.YEAR, Integer.parseInt(fromDateStr.substring(0, 4)));
			fromDateCal.set(Calendar.MONTH, Integer.parseInt(fromDateStr.substring(4, 6)) -1);
			fromDateCal.set(Calendar.DATE, Integer.parseInt(fromDateStr.substring(6,8)));
			
			for (int i = 0; i < 12; i++) {
				fromDateCal.add(Calendar.MONTH, 1);
				String dateStr = sdf.format(fromDateCal.getTime());
				colNameMap.put("C_"+(i+1), dateStr.substring(0,6));
			}
			colNameMap.put("C_13", "TOTAL");
			resultListMap.add(0, colNameMap);
			json.put("colName", resultListMap);
			
		}
		out.print(json.toString());
	} catch (Exception e) {
	  e.printStackTrace();
	}
%>