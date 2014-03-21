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
	
	var planSalesAvgOfDayGroupBy;
	var mitAvgOfDayGroupBy;
	var planSalesAvgOfDay2GroupBy;
	var planOfSalesSum = 0.0;
	var mitAvgOfDaySum = 0.0;
	var mitAvgOfDaySum2 = 0.0;
	var planOfSalesSum2 = 0.0;
	
	function planSalesAvgOfDay(cellValue, opetions, rowObject) {
		var year = $('#sel_year').val();
		var month = $('#sel_month').val().substr(0,2);
		var nalsu = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
		if ((year % 4 === 0) && (year % 100 !== 0) || (year % 400 === 0)) {
			nalsu[1] = 29; //윤년 체크
		}
		if (planSalesAvgOfDayGroupBy != rowObject.DIVISION) {
			planOfSalesSum= 0.0;
			planSalesAvgOfDayGroupBy= rowObject.DIVISION;
			planOfSalesSum= planOfSalesSum + parseFloat(rowObject.PLANOFSALES);
	    } else {
	    	planOfSalesSum= planOfSalesSum + parseFloat(rowObject.PLANOFSALES);
	    }
		return (planOfSalesSum/nalsu[parseInt(month)-1]).toFixed(2);
	}
	function mitAvgOfDay(cellValue, opetions, rowObject) {
		var year = $('#sel_year').val();
		var month = $('#sel_month').val().substr(0,2);
		var nalsu = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
		if ((year % 4 === 0) && (year % 100 !== 0) || (year % 400 === 0)) {
			nalsu[1] = 29; //윤년 체크
		}
		if (mitAvgOfDayGroupBy != rowObject.DIVISION) {
			mitAvgOfDaySum= 0.0;
			mitAvgOfDayGroupBy= rowObject.DIVISION;
			mitAvgOfDaySum= mitAvgOfDaySum + parseFloat(rowObject.TOTALSUM);
	    } else {
	    	mitAvgOfDaySum= mitAvgOfDaySum + parseFloat(rowObject.TOTALSUM);
	    }
		
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return (mitAvgOfDaySum / toDay).toFixed(2);
		} else {
			return (mitAvgOfDaySum / nalsu[parseInt(month)-1]).toFixed(2);
		}
	}
	function salesRatio(cellValue, opetions, rowObject) {
		
		var year = $('#sel_year').val();
		var month = $('#sel_month').val().substr(0,2);
		var nalsu = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
		if ((year % 4 === 0) && (year % 100 !== 0) || (year % 400 === 0)) {
			nalsu[1] = 29; //윤년 체크
		}
		
		if (planSalesAvgOfDay2GroupBy != rowObject.DIVISION) {
			planOfSalesSum2= 0.0;
			mitAvgOfDaySum2= 0.0;
			planSalesAvgOfDay2GroupBy= rowObject.DIVISION;
			planOfSalesSum2= planOfSalesSum2 + parseFloat(rowObject.PLANOFSALES);
			mitAvgOfDaySum2= mitAvgOfDaySum2 + parseFloat(rowObject.TOTALSUM);
	    } else {
	    	planOfSalesSum2= planOfSalesSum2 + parseFloat(rowObject.PLANOFSALES);
			mitAvgOfDaySum2= mitAvgOfDaySum2 + parseFloat(rowObject.TOTALSUM);
	    }
		
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return ((mitAvgOfDaySum2 / ((planOfSalesSum2/dayCountOfThisMonth)* toDay)) * 100).toFixed(2) + '%';
		} else {
			return ((mitAvgOfDaySum2 / planOfSalesSum2) * 100).toFixed(2) + '%';
		}
	}
	
	var method = 'getDailySales';
	
	var reportData = null;
	var chartData = null;
	var chart2FieldNames = ["합계"];
	
	 var getReportData = function(reportData){
	 	//reportData = data.rows;
	 	console.log('reportData=', reportData);
	
	 	var chartValues = Array();
	 	for(var i=0; i<31; i++){
	 		var pktSum = 0;
	 		var pkgSum = 0;
	 		var nbizSum = 0;
	 		var sum = 0;
	 		for(var j=0; j<reportData.length; j++){
	 			if(reportData[j].DIVISION == "pkt"){
	 				pktSum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}else if(reportData[j].DIVISION == "pkg"){
	 				pkgSum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}else if(reportData[j].DIVISION == "nbiz"){
	 				nbizSum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}
	 			sum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 		}
	 		chartValues[i] = {  일별: (i+1) + "", 
	 				pkt: pktSum,
	 				pkg: pkgSum,
	 				nbiz: nbizSum,
	 				합계: sum};					
	 	}
	 	
	 	chartData = {
	 			values : chartValues,
	 			xFieldName : "일별",
	 			yValueName : "매출 실적",
	 			groupNames : ["pkt", "pkg", "nbiz"]
	 	};
	
	 };
	 var loadChart = function(data) {
		getReportData(data);
		Ext.onReady(function () {
			smartChart.loadWithData(chartData, "column", false, "chart_target", chart2FieldNames, "line");
		});
	 };
	
	 var loadGrid = function() {
		 jQuery("#list").jqGrid({
	   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
	         datatype: "json",      //json형태로 데이터 받음.  
	         height: "auto",
	         caption: "일별 매출 실적 현황",
	         footerrow:true,
	         grouping:true, //그룹화 하기위한 옵션
	         autowidth:true,
	         groupingView : {
	             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
	             groupSummary : [true], //소계를 보인다.
	             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
	             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
	         },
	         
	         colNames:['사업부','구분','금월', '일평균','MIT', '일평균','달성률(%)', 'WIP', '1일', '2일','3일', '4일', '5일', '6일'	, '7일'	, '8일'	, '9일'	, '10일', '11일', '12일', '13일', '14일', '15일', '16일', '17일', '18일', '19일', '20일', '21일', '22일', '23일', '24일', '25일', '26일', '27일', '28일', '29일', '30일', '31일'],
	         colModel:[                  
	             {name:'DIVISION', width:'70', index:'DIVISION', align: 'center' , sortable:false},
	             {name:'DEVICEGROUP', width:'70', index:'DEVICEGROUP', summaryTpl: '<div><b>total</b></div>', summaryType: function(){}, sortable:false},
	             {name:'PLANOFSALES', width:'70', index:'PLANOFSALES', summaryType: 'sum', sortable:false},
	             {name:'AVGPLANOFDAY', width:'70', index:'AVGPLANOFDAY', summaryType: planSalesAvgOfDay, sortable:false},
	             {name:'TOTALSUM', width:'70', index:'TOTALSUM', summaryType: 'sum', sortable:false},
	             {name:'AVGOFDAY', width:'70', index:'AVGOFDAY', summaryType: mitAvgOfDay, sortable:false},
	             {name:'PERSALES', width:'70', index:'PERSALES',  summaryType: salesRatio, sortable:false},
	             {name:'WIP', width:'50', index:'WIP', sortable:false },
	             {name:'C01', width:'50', index:'C01', summaryType: 'sum', sortable:false},
	             {name:'C02', width:'50', index:'C02', summaryType: 'sum', sortable:false},
	             {name:'C03', width:'50', index:'C03', summaryType: 'sum', sortable:false},
	             {name:'C04', width:'50', index:'C04', summaryType: 'sum', sortable:false},
	             {name:'C05', width:'50', index:'C05', summaryType: 'sum', sortable:false},
	             {name:'C06', width:'50', index:'C06', summaryType: 'sum', sortable:false},
	             {name:'C07', width:'50', index:'C07', summaryType: 'sum', sortable:false},
	             {name:'C08', width:'50', index:'C08', summaryType: 'sum', sortable:false},
	             {name:'C09', width:'50', index:'C09', summaryType: 'sum', sortable:false},
	             {name:'C10', width:'50', index:'C10', summaryType: 'sum', sortable:false},
	             {name:'C11', width:'50', index:'C11', summaryType: 'sum', sortable:false},
	             {name:'C12', width:'50', index:'C12', summaryType: 'sum', sortable:false},
	             {name:'C13', width:'50', index:'C13', summaryType: 'sum', sortable:false},
	             {name:'C14', width:'50', index:'C14', summaryType: 'sum', sortable:false},
	             {name:'C15', width:'50', index:'C15', summaryType: 'sum', sortable:false},
	             {name:'C16', width:'50', index:'C16', summaryType: 'sum', sortable:false},
	             {name:'C17', width:'50', index:'C17', summaryType: 'sum', sortable:false},
	             {name:'C18', width:'50', index:'C18', summaryType: 'sum', sortable:false},
	             {name:'C19', width:'50', index:'C19', summaryType: 'sum', sortable:false},
	             {name:'C20', width:'50', index:'C20', summaryType: 'sum', sortable:false},
	             {name:'C21', width:'50', index:'C21', summaryType: 'sum', sortable:false},
	             {name:'C22', width:'50', index:'C22', summaryType: 'sum', sortable:false},
	             {name:'C23', width:'50', index:'C23', summaryType: 'sum', sortable:false},
	             {name:'C24', width:'50', index:'C24', summaryType: 'sum', sortable:false},
	             {name:'C25', width:'50', index:'C25', summaryType: 'sum', sortable:false},
	             {name:'C26', width:'50', index:'C26', summaryType: 'sum', sortable:false},
	             {name:'C27', width:'50', index:'C27', summaryType: 'sum', sortable:false},
	             {name:'C28', width:'50', index:'C28', summaryType: 'sum', sortable:false},
	             {name:'C29', width:'50', index:'C29', summaryType: 'sum', sortable:false},
	             {name:'C30', width:'50', index:'C30', summaryType: 'sum', sortable:false},
	             {name:'C31', width:'50', index:'C31', summaryType: 'sum', sortable:false}
	         ],
	         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
	          gridComplete : function() {
	        	  
        	  	var PLANOFSALESSUM = $("#list").jqGrid('getCol', 'PLANOFSALES', false, 'sum');
        	 	var MITTOTALSUM = $("#list").jqGrid('getCol', 'TOTALSUM', false, 'sum');
		        	 
	        	 	var year = $('#sel_year').val();
	        		var month = $('#sel_month').val().substr(0,2);
	        		var nalsu = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
	        		if ((year % 4 === 0) && (year % 100 !== 0) || (year % 400 === 0)) {
	        			nalsu[1] = 29; //윤년 체크
	        		}
	        	 	var totalPlanAvgOfDay = (PLANOFSALESSUM/nalsu[parseInt(month)-1]).toFixed(2);;
	        	 	var totalSalesAvgOfDay = 0;
	    			var perTotalSales = 0;
	        		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
	        			totalSalesAvgOfDay = (MITTOTALSUM / toDay).toFixed(2);
		            	perTotalSales = ((MITTOTALSUM / ((PLANOFSALESSUM/dayCountOfThisMonth) * toDay)) * parseInt(100)).toFixed(2) + '%';
	        		} else {
		            	totalSalesAvgOfDay = (MITTOTALSUM / nalsu[parseInt(month)-1]).toFixed(2);
		            	perTotalSales = ((MITTOTALSUM / PLANOFSALESSUM) * parseInt(100)).toFixed(2) + '%';
	        		}
	        	 	
	        	 	var C01SUM = $("#list").jqGrid('getCol', 'C01', false, 'sum');
	        	 	var C02SUM = $("#list").jqGrid('getCol', 'C02', false, 'sum');
	        	 	var C03SUM = $("#list").jqGrid('getCol', 'C03', false, 'sum');
	        	 	var C04SUM = $("#list").jqGrid('getCol', 'C04', false, 'sum');
	        	 	var C05SUM = $("#list").jqGrid('getCol', 'C05', false, 'sum');
	        	 	var C06SUM = $("#list").jqGrid('getCol', 'C06', false, 'sum');
	        	 	var C07SUM = $("#list").jqGrid('getCol', 'C07', false, 'sum');
	        	 	var C08SUM = $("#list").jqGrid('getCol', 'C08', false, 'sum');
	        	 	var C09SUM = $("#list").jqGrid('getCol', 'C09', false, 'sum');
	        	 	var C10SUM = $("#list").jqGrid('getCol', 'C10', false, 'sum');
	        	 	var C11SUM = $("#list").jqGrid('getCol', 'C11', false, 'sum');
	        	 	var C12SUM = $("#list").jqGrid('getCol', 'C12', false, 'sum');
	        	 	var C13SUM = $("#list").jqGrid('getCol', 'C13', false, 'sum');
	        	 	var C14SUM = $("#list").jqGrid('getCol', 'C14', false, 'sum');
	        	 	var C15SUM = $("#list").jqGrid('getCol', 'C15', false, 'sum');
	        	 	var C16SUM = $("#list").jqGrid('getCol', 'C16', false, 'sum');
	        	 	var C17SUM = $("#list").jqGrid('getCol', 'C17', false, 'sum');
	        	 	var C18SUM = $("#list").jqGrid('getCol', 'C18', false, 'sum');
	        	 	var C19SUM = $("#list").jqGrid('getCol', 'C19', false, 'sum');
	        	 	var C20SUM = $("#list").jqGrid('getCol', 'C20', false, 'sum');
	        	 	var C21SUM = $("#list").jqGrid('getCol', 'C21', false, 'sum');
	        	 	var C22SUM = $("#list").jqGrid('getCol', 'C22', false, 'sum');
	        	 	var C23SUM = $("#list").jqGrid('getCol', 'C23', false, 'sum');
	        	 	var C24SUM = $("#list").jqGrid('getCol', 'C24', false, 'sum');
	        	 	var C25SUM = $("#list").jqGrid('getCol', 'C25', false, 'sum');
	        	 	var C26SUM = $("#list").jqGrid('getCol', 'C26', false, 'sum');
	        	 	var C27SUM = $("#list").jqGrid('getCol', 'C27', false, 'sum');
	        	 	var C28SUM = $("#list").jqGrid('getCol', 'C28', false, 'sum');
	        	 	var C29SUM = $("#list").jqGrid('getCol', 'C29', false, 'sum');
	        	 	var C30SUM = $("#list").jqGrid('getCol', 'C30', false, 'sum');
	        	 	var C31SUM = $("#list").jqGrid('getCol', 'C31', false, 'sum');

	             	jQuery("#list").jqGrid('footerData', 'set', { DEVICEGROUP: 'Grand Total', PLANOFSALES:PLANOFSALESSUM, TOTALSUM:MITTOTALSUM, AVGPLANOFDAY:totalPlanAvgOfDay
	             		, AVGOFDAY : totalSalesAvgOfDay, PERSALES: perTotalSales
	             		, C01:C01SUM, C02:C02SUM, C03:C03SUM, C04:C04SUM, C05:C05SUM, C06:C06SUM
	             		, C07:C07SUM, C08:C08SUM, C09:C09SUM, C10:C10SUM, C11:C11SUM, C12:C12SUM, C13:C13SUM, C14:C14SUM, C15:C15SUM, C16:C16SUM, C17:C17SUM
	             		, C18:C18SUM, C19:C19SUM, C20:C20SUM, C21:C21SUM, C22:C22SUM, C23:C23SUM, C24:C24SUM, C25:C25SUM, C26:C26SUM, C27:C27SUM, C28:C28SUM
	             		, C29:C29SUM, C30:C30SUM, C31:C31SUM});
	        	  
				$("#list").setGridWidth($('.js_work_report_view_page').width());					        	 
	     		 loadChart(jQuery("#list").jqGrid('getRowData'));//---데이터를 성공적으로 가져오면 실행 됨
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
		              {startColumnName: 'PLANOFSALES', numberOfColumns: 2, titleText: '실행계획'},	
		              {startColumnName: 'TOTALSUM', numberOfColumns: 3, titleText: '매출실적'}
		              ]
			});

			$(".footrow td").css('background-color', '#E8FFFF');	
		 
	 };
	 
 
	 $(document).ready( function() { 
			selectMenuItem('dailySales');
		 loadGrid();
	 });

	var reloadGrid = function(){
		 $("#list").setGridParam(
	   	 			{
	   	 				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $('#sel_month').val() ,
	   	 			}).trigger("reloadGrid");
	};
	$(function() { 
		$('.selDate').change(function() { 
			reloadGrid();	  
		});   
		$(window).resize(function() {
			if(swReportResizing) return;			
			if(!isEmpty($('.js_work_report_view_page'))){
				swReportResizing = true;
				setTimeout(function(){
					$("#list").setGridWidth($('.js_work_report_view_page').width());				
		     		loadChart(jQuery("#list").jqGrid('getRowData'));
					swReportResizing = false;
				},1000);
			}
		});
	}); 
</script>
 
</head>
<body>
<div>
<jsp:include page="./chartMenu.jsp" flush="false"/>
</div>
<div style="text-align:right">
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
</div>
<table id="list"></table> 
<br/><br/>
<div class="js_work_report_view_page">
	<div id="chart_target"></div>
</div>
</body>
</html>