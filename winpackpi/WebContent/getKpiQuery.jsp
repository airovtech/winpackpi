<%@page import="util.WebServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%
	try {
		String method = (String)request.getParameter("method");
		
		String targetEndpointAddress = "http://localhost:8080/winpackpi/GetKpiWS.jws?wsdl";
		String returnType = "string";
		String operation = null;
		String[] inputParams = null; 
		
		if (method.equalsIgnoreCase("getDailyShippingNSales")) {
			
			operation = "getDailyShippingNSales";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
			
		} else if (method.equalsIgnoreCase("getDailyShipping")) {
			operation = "getDailyShipping";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getDailySales")) {
			operation = "getDailySales";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getDailyOperationRatio")) {
			operation = "getDailyOperationRatio";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getDailyTat")) {
			operation = "getDailyTat";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getMonthlyShippingForYear")) {
			operation = "getMonthlyShippingForYear";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getMonthlySalesForYear")) {
			operation = "getMonthlySalesForYear";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getMonthlyCapacityPkgForYear")) {
			operation = "getMonthlyCapacityPkgForYear";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getMonthlyCapacityPkgForYearByGroup")) {
			operation = "getMonthlyCapacityPkgForYearByGroup";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getMonthlyShipping")) {
			operation = "getMonthlyShipping";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getMonthlySales")) {
			operation = "getMonthlySales";
			
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{yearMonth};	
			}
		} else if (method.equalsIgnoreCase("getQuery")) {
			String queryType = (String)request.getParameter("queryType");
			operation = "makeQueryString";		
			String yearMonth = (String)request.getParameter("yearMonth");
			if (yearMonth != null) {
				inputParams = new String[]{queryType, yearMonth};	
			}
		} else {
			throw new Exception("Not Exist Method!");
		}
				
		String[] result = WebServiceUtil.invokeWebService(targetEndpointAddress, operation, inputParams, returnType);
		if (result != null)
			out.print(result[0]);
		
	} catch (Exception e) {
	  e.printStackTrace();
	}
%>