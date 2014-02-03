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
		if (tShippingGroupBy != rowObject.DIVISION) {
	        shippingSum= 0.0;
	        shippingPlanSum = 0.0;
	        tShippingGroupBy= rowObject.DIVISION;
	        shippingSum= shippingSum + parseFloat(rowObject.SUMOFSHIPPING);
	        shippingPlanSum = shippingPlanSum + parseFloat(rowObject.SHIPPINGEXEPLAN);
	    }
	    else {
	        shippingSum= shippingSum + parseFloat(rowObject.SUMOFSHIPPING);
	        shippingPlanSum = shippingPlanSum + parseFloat(rowObject.SHIPPINGEXEPLAN);
	    }
	
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return ((shippingSum / ((shippingPlanSum/dayCountOfThisMonth)* toDay)) * 100).toFixed(2) + '%';
		} else {
			return ((shippingSum / shippingPlanSum) * 100).toFixed(2) + '%';
		}
	}
	function perSalesSum(cellValue, opetions, rowObject)
	{
		if (tSalesGroupBy != rowObject.DIVISION) {
	        salesSum= 0.0;
	        salesPlanSum = 0.0;
	        tSalesGroupBy= rowObject.DIVISION;
	        salesSum= salesSum + parseFloat(rowObject.SUMOFSALES);
	        salesPlanSum = salesPlanSum + parseFloat(rowObject.SALESEXEPLAN);
	    }
	    else {
	    	salesSum= salesSum + parseFloat(rowObject.SUMOFSALES);
         	salesPlanSum = salesPlanSum + parseFloat(rowObject.SALESEXEPLAN);
	    }
	
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return ((salesSum / ((salesPlanSum/dayCountOfThisMonth)* toDay)) * 100).toFixed(2) + '%';
		} else {
			return ((salesSum / salesPlanSum) * 100).toFixed(2) + '%';
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
		             {name:'SHIPPINGPLAN',width:'100', index:'SHIPPINGPLAN', summaryType: 'sum'},
		             {name:'SALESPLAN',width:'100', index:'SALESPLAN', summaryType: 'sum'},
		             {name:'SHIPPINGFOCPLAN',width:'100', index:'SHIPPINGFOCPLAN', summaryType: 'sum'},
		             {name:'SALESFOCPLAN',width:'100', index:'SALESFOCPLAN', summaryType: 'sum'},
		             {name:'SHIPPINGEXEPLAN',width:'100', index:'SHIPPINGEXEPLAN', summaryType: 'sum'},
		             {name:'SALESEXEPLAN',width:'100', index:'SALESEXEPLAN', summaryType: 'sum'},
		             {name:'BOH',width:'100', index:'boh', summaryType: 'sum'},
		             {name:'SUMOFRECEIVING',width:'100', index:'SUMOFRECEIVING', summaryType: 'sum'},
		             {name:'SUMOFSHIPPING',width:'100', index:'SUMOFSHIPPING', summaryType: 'sum'},
		             {name:'WIP',width:'100', index:'wip', summaryType: 'sum'},
		             {name:'PERSHIPPING',width:'100', index:'PERSHIPPING', summaryType:perShippingSum},
		             {name:'SUMOFSALES',width:'100', index:'SUMOFSALES', summaryType: 'sum', formatter: 'currency', formatoptions: { decimalSeparator: '.', thousandsSeparator: ',', decimalPlaces: 0, prefix: '' }},
		             {name:'PERSALES',width:'100', index:'PERSALES', summaryType:perSalesSum}
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
		            	perTotalShipping = ((sumOfShipping / ((shippingExePlan/dayCountOfThisMonth) * toDay)) * parseInt(100)).toFixed(2) + '%';
		            	perTotalSales = ((sumOfSales / ((salesExePlan/dayCountOfThisMonth) * toDay)) * parseInt(100)).toFixed(2) + '%';
	        		} else {
		            	perTotalShipping = ((sumOfShipping / shippingExePlan) * parseInt(100)).toFixed(2) + '%';
		            	perTotalSales = ((sumOfSales / salesExePlan) * parseInt(100)).toFixed(2) + '%';
	        		}
	            	
	             	jQuery("#list").jqGrid('footerData', 'set', { DEVICEGROUP: 'Grand Total', SUMOFSALES: sumOfSales, SHIPPINGPLAN: shippingPlan, SALESPLAN: salesPlan, SHIPPINGFOCPLAN: shippingFocPlan, SALESFOCPLAN: salesFocPlan, SHIPPINGEXEPLAN: shippingExePlan, SALESEXEPLAN: salesExePlan, BOH: sumOfBoh, SUMOFRECEIVING: sumOfReceiving, SUMOFSHIPPING: sumOfShipping, WIP: sumOfWip, PERSHIPPING: perTotalShipping, PERSALES:perTotalSales});
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
			  useColSpanStyle: true, 
			  groupHeaders:[
				{startColumnName: 'SHIPPINGPLAN', numberOfColumns: 2, titleText: 'PLAN(월)'},
				{startColumnName: 'SHIPPINGFOCPLAN', numberOfColumns: 2, titleText: 'FORECAST(월)'},
				{startColumnName: 'SHIPPINGEXEPLAN', numberOfColumns: 2, titleText: '실행계획(월)'},
				{startColumnName: 'BOH', numberOfColumns: 4, titleText: 'Movement'},
				{startColumnName: 'PERSHIPPING', numberOfColumns: 3, titleText: '실적'}
			  ]
			});
	
			 $(".footrow td").css('background-color', '#E8FFFF');
	 });
 
 
 	$(function() { 
	   $('#sel_month').change(function() {
			$("#list").setGridParam(
			{
				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $(this).val() ,
			}).trigger("reloadGrid");
	   }); 
	}); 
 	$(function() { 
 	   $('#sel_year').change(function() {
 			$("#list").setGridParam(
 			{
 				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $(this).val() + $('#sel_month').val() ,
 			}).trigger("reloadGrid");
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
<div style="text-align:right">
<select id='sel_year'>
	<option value='2013'>2013년</option>
	<option selected value='2014'>2014년</option>
</select>
<select id='sel_month'>
	<option selected value='0101'>1월</option>
	<option value='0201'>2월</option>
	<option value='0301'>3월</option>
	<option value='0401'>4월</option>
	<option value='0501'>5월</option>
	<option value='0601'>6월</option>
	<option value='0701'>7월</option>
	<option value='0801'>8월</option>
	<option value='0901'>9월</option>
	<option value='1001'>10월</option>
	<option value='1101'>11월</option>
	<option value='1201'>12월</option>
</select>
</div>
<table id="list"></table> 
</body>
</html>
