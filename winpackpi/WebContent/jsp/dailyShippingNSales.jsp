<%@page import="java.util.Date"%>
<%@page import="util.DateUtil"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<jsp:include page="commons.jsp"/>

<script type="text/javascript">

<%
	int thisYear = Calendar.getInstance().get(Calendar.YEAR);
	int thisMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
	int toDay = Calendar.getInstance().get(Calendar.DATE) -1;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	String dateStr = sdf.format(Calendar.getInstance().getTime());
	
	int daycountOfThisMonth = DateUtil.GetDaysOfMonth(thisYear, thisMonth);
	
%>
 
	var date = <%=dateStr + "01"%>;
	var dayCountOfThisMonth = <%=daycountOfThisMonth%>;
	var toDay = <%=toDay%>;
	
	var tShippingGroupBy;
	var tSalesGroupBy;
	
	var shippingSum = 0.0;
	var salesSum = 0.0;
	
	var shippingPlanSum = 0.0;
	var salesPlanSum = 0.0;
	
	function perShippingSum(cellValue, opetions, rowObject)
	{
		console.log(cellValue, rowObject);
		return cellValue;
//		if (tShippingGroupBy != rowObject.DIVISION) {
	        shippingSum= 0.0;
	        shippingPlanSum = 0.0;
	        tShippingGroupBy= rowObject.DIVISION;
	        shippingSum= shippingSum + parseFloat(rowObject.SUMOFSHIPPING?rowObject.SUMOFSHIPPING:0);
	        shippingPlanSum = shippingPlanSum + parseFloat(rowObject.SHIPPINGEXEPLAN?rowObject.SHIPPINGEXEPLAN:0);
/* 	    }
	    else {
	        shippingSum= shippingSum + parseFloat(rowObject.SUMOFSHIPPING?rowObject.SUMOFSHIPPING:0);
	        shippingPlanSum = shippingPlanSum + parseFloat(rowObject.SHIPPINGEXEPLAN?rowObject.SHIPPINGEXEPLAN:0);
	    }
 */

		if(shippingSum == 0 || shippingPlanSum == 0 || dayCountOfThisMonth == 0) return (0).toFixed(2);
		
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return ((shippingSum / ((shippingPlanSum/dayCountOfThisMonth)* toDay)) * 100).toFixed(2);
		} else {
			return ((shippingSum / shippingPlanSum) * 100).toFixed(2);
		}
	}
	function test(cellValue, opetions, rowObject)
	{
		console.log(cellValue)
		return 100;
//		if (tShippingGroupBy != rowObject.DIVISION) {
	        shippingSum= 0.0;
	        shippingPlanSum = 0.0;
	        tShippingGroupBy= rowObject.DIVISION;
	        shippingSum= shippingSum + parseFloat(rowObject.SUMOFSHIPPING?rowObject.SUMOFSHIPPING:0);
	        shippingPlanSum = shippingPlanSum + parseFloat(rowObject.SHIPPINGEXEPLAN?rowObject.SHIPPINGEXEPLAN:0);
/* 	    }
	    else {
	        shippingSum= shippingSum + parseFloat(rowObject.SUMOFSHIPPING?rowObject.SUMOFSHIPPING:0);
	        shippingPlanSum = shippingPlanSum + parseFloat(rowObject.SHIPPINGEXEPLAN?rowObject.SHIPPINGEXEPLAN:0);
	    }
 */

 
		if(shippingSum == 0 || shippingPlanSum == 0 || dayCountOfThisMonth == 0) return (0).toFixed(2);
		
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return ((shippingSum / ((shippingPlanSum/dayCountOfThisMonth)* toDay)) * 100).toFixed(2);
		} else {
			return ((shippingSum / shippingPlanSum) * 100).toFixed(2);
		}
	}
	function perSalesSum(cellValue, opetions, rowObject)
	{
//		if (tSalesGroupBy != rowObject.DIVISION) {
	        salesSum= 0.0;
	        salesPlanSum = 0.0;
	        tSalesGroupBy= rowObject.DIVISION;
	        salesSum= salesSum + parseFloat(rowObject.SUMOFSALES?rowObject.SUMOFSALES:0);
	        salesPlanSum = salesPlanSum + parseFloat(rowObject.SALESEXEPLAN?rowObject.SALESEXEPLAN:0);
/* 	    }
	    else {
	    	salesSum= salesSum + parseFloat(rowObject.SUMOFSALES?rowObject.SUMOFSALES:0);
         	salesPlanSum = salesPlanSum + parseFloat(rowObject.SALESEXEPLAN?rowObject.SALESEXEPLAN:0);
	    }
 */	
		if(salesSum == 0 || salesPlanSum == 0 || dayCountOfThisMonth == 0) return (0).toFixed(2);
		
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return ((salesSum / ((salesPlanSum/dayCountOfThisMonth)* toDay)) * 100).toFixed(2);
		} else {
			return ((salesSum / salesPlanSum) * 100).toFixed(2);
		}
	}
	 var method = 'getDailyShippingNSales';
	 $(document).ready( function() { 
		selectMenuItem('dailyShippingNSales');
		 jQuery("#list").jqGrid({
		   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
		         datatype: "json",      //json형태로 데이터 받음.  
		         height: "auto",
		         caption: "당월 생산 매출 현황",
		         footerrow:true,
		         grouping:true, //그룹화 하기위한 옵션
		         autowidth:true,
		         groupingView : {
		             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
		             groupSummary : [true], //소계를 보인다.
		             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
		             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
		         },
		         colNames:['사업부','구분','생산', '매출','생산', '매출','생산', '매출', '기초', '입고', '출하', 'WIP', '출하달성률', '매출', '매출달성율'],
		         colModel:[                  
		             {name:'DIVISION', width:'100', index:'DIVISION', align: 'center' },
		             {name:'DEVICEGROUP',width:'100', index:'DEVICEGROUP', summaryTpl: '<div><b>total</b></div>', summaryType: function(){}},
		             {name:'SHIPPINGPLAN',width:'100', index:'SHIPPINGPLAN', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'SALESPLAN',width:'100', index:'SALESPLAN', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'SHIPPINGFOCPLAN',width:'100', index:'SHIPPINGFOCPLAN', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'SALESFOCPLAN',width:'100', index:'SALESFOCPLAN', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'SHIPPINGEXEPLAN',width:'100', index:'SHIPPINGEXEPLAN', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'SALESEXEPLAN',width:'100', index:'SALESEXEPLAN', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'BOH',width:'100', index:'boh', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'SUMOFRECEIVING',width:'100', index:'SUMOFRECEIVING', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'SUMOFSHIPPING',width:'100', index:'SUMOFSHIPPING', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'WIP',width:'100', index:'wip', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'PERSHIPPING',width:'100', index:'PERSHIPPING', align: 'right', summaryType:test, formatter:perShippingSum},
		             {name:'SUMOFSALES',width:'100', index:'SUMOFSALES', align: 'right', summaryType: 'sum', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
		             {name:'PERSALES',width:'100', index:'PERSALES', align: 'right', summaryType:'avg', formatter:perSalesSum}
		         ],
		         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
		          gridComplete : function() {        //---데이터를 성공적으로 가져오면 실행 됨
	            	var sumOfSales = $("#list").jqGrid('getCol', 'SUMOFSALES', false, 'sum');
	            	var shippingPlan = $("#list").jqGrid('getCol', 'SHIPPINGPLAN', false, 'sum');
	            	var salesPlan = $("#list").jqGrid('getCol', 'SALESPLAN', false, 'sum');
	            	var shippingFocPlan = $("#list").jqGrid('getCol', 'SHIPPINGFOCPLAN', false, 'sum');
	            	var salesFocPlan = $("#list").jqGrid('getCol', 'SALESFOCPLAN', false, 'sum');
	            	var shippingExePlan = $("#list").jqGrid('getCol', 'SHIPPINGEXEPLAN', false, 'sum');
	            	var salesExePlan = $("#list").jqGrid('getCol', 'SALESEXEPLAN', false, 'sum');
	            	var sumOfBoh = $("#list").jqGrid('getCol', 'BOH', false, 'sum');
	            	var sumOfReceiving = $("#list").jqGrid('getCol', 'SUMOFRECEIVING', false, 'sum');
	            	var sumOfShipping = $("#list").jqGrid('getCol', 'SUMOFSHIPPING', false, 'sum');
	            	var sumOfWip = $("#list").jqGrid('getCol', 'WIP', false, 'sum');

	            	var perTotalShipping = 0;
        			var perTotalSales = 0;
	        		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
		            	perTotalShipping = ((sumOfShipping / ((shippingExePlan/dayCountOfThisMonth) * toDay)) * parseInt(100)).toFixed(2);
		            	perTotalSales = ((sumOfSales / ((salesExePlan/dayCountOfThisMonth) * toDay)) * parseInt(100)).toFixed(2);
	        		} else {
		            	perTotalShipping = ((sumOfShipping / shippingExePlan) * parseInt(100)).toFixed(2);
		            	perTotalSales = ((sumOfSales / salesExePlan) * parseInt(100)).toFixed(2);
	        		}
	            	
	             	jQuery("#list").jqGrid('footerData', 'set', { DEVICEGROUP: 'Grand Total', SUMOFSALES: sumOfSales, SHIPPINGPLAN: shippingPlan, SALESPLAN: salesPlan, SHIPPINGFOCPLAN: shippingFocPlan, SALESFOCPLAN: salesFocPlan, SHIPPINGEXEPLAN: shippingExePlan, SALESEXEPLAN: salesExePlan, BOH: sumOfBoh, SUMOFRECEIVING: sumOfReceiving, SUMOFSHIPPING: sumOfShipping, WIP: sumOfWip, PERSHIPPING: perTotalShipping, PERSALES:perTotalSales});

	             	$("tr.jqfoot>td").css('background-color', 'floralwhite');
		          },
		          loadError:function(xhr, status, error) {          //---데이터 못가져오면 실행 됨
		          },
		          jsonReader : {                             //가져온 데이터를 읽을 때 사용
		             root: "rows",   // json으로 저장 된 객체의 root명
		             repeatitems: false     //얜 뭐지... 일단 필요한거같은데... 
		   		},
		         multiselect: false,         //전체선택 체크박스 유무, 테이블에서 row 체크를 멀티로 할 수 있는 옵션.
		     });
	  
		  jQuery("#list").jqGrid('setGroupHeaders', {
			  useColSpanStyle: false, 
			  groupHeaders:[
				{startColumnName: 'SHIPPINGPLAN', numberOfColumns: 2, titleText: 'PLAN(월)'},
				{startColumnName: 'SHIPPINGFOCPLAN', numberOfColumns: 2, titleText: 'FORECAST(월)'},
				{startColumnName: 'SHIPPINGEXEPLAN', numberOfColumns: 2, titleText: '실행계획(월)'},
				{startColumnName: 'BOH', numberOfColumns: 4, titleText: 'Movement'},
				{startColumnName: 'PERSHIPPING', numberOfColumns: 3, titleText: '실적'}
			  ]
			});
	
		  	$(".ui-jqgrid-hdiv .jqg-second-row-header th").css('text-align', 'center');
		  	$(".ui-jqgrid-bdiv").css('overflow-x', 'hidden');
			$(".footrow td").css('background-color', '#E8FFFF');
	 });
 
 
 	$(function() { 
	   $('#sel_month').change(function() {
			$("#list").setGridParam(
			{
				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $(this).val() ,
			}).trigger("reloadGrid");
			
		  	$(".ui-jqgrid-hdiv .jqg-second-row-header th").css('text-align', 'center');
		  	$(".ui-jqgrid-bdiv").css('overflow-x', 'hidden');
			$(".footrow td").css('background-color', '#E8FFFF');
	   }); 
	}); 
 	$(function() { 
 	   $('#sel_year').change(function() {
 			$("#list").setGridParam(
 			{
 				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $(this).val() + $('#sel_month').val() ,
 			}).trigger("reloadGrid");
 			
		  	$(".ui-jqgrid-hdiv .jqg-second-row-header th").css('text-align', 'center');
		  	$(".ui-jqgrid-bdiv").css('overflow-x', 'hidden');
			$(".footrow td").css('background-color', '#E8FFFF');
 	   }); 
		$(window).resize(function() {
			if(swReportResizing) return;			
			if(!isEmpty($('.js_work_report_view_page'))){
				swReportResizing = true;
				setTimeout(function(){
					$("#list").setGridWidth($('.js_work_report_view_page').width());				
					swReportResizing = false;
				},1000);
			}
		});
 	}); 
</script>
 
</head>
<body>
<div  class="js_work_report_view_page">
<jsp:include page="./chartMenu.jsp" flush="false"/>
</div>
<div>
	<span>
		<%
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
		%>
		
		<select id='sel_year' class='selDate'>
			<%
			if(year>=2012){
				for(int i=0; i+2012<=year; i++){
					int currentYear = i+2012;
			%>
					<option <%if(currentYear==year) {%> selected <%} %>value='<%=currentYear%>'><%=currentYear%>년</option>
			<%
				}
			}
			%>
		</select>
		<select id='sel_month' class='selDate'>
			<%
			for(int i=0; i<12; i++){
			%>
				<option <%if(month==i) {%>selected<%} %> value='<%=String.format("%02d", i+1)%>01'><%=i+1 %>월</option>
			<%
			}
			%>
		 </select>
	</span>
	<span style="float:right">(단위 : K / 백만원)</span>
</div>
<table id="list"></table> 
</body>
</html>
