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
	
	var planShippingAvgOfDayGroupBy;
	var mitAvgOfDayGroupBy;
	var planShippingAvgOfDay2GroupBy;
	var planOfShippingSum = 0.0;
	var mitAvgOfDaySum = 0.0;
	var mitAvgOfDaySum2 = 0.0;
	var planOfShippingSum2 = 0.0;
	
	function planShippingAvgOfDay(cellValue, opetions, rowObject) {
		var year = $('#sel_year').val();
		var month = $('#sel_month').val().substr(0,2);
		var nalsu = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
		if ((year % 4 === 0) && (year % 100 !== 0) || (year % 400 === 0)) {
			nalsu[1] = 29; //윤년 체크
		}
		if (planShippingAvgOfDayGroupBy != rowObject.DIVISION) {
			planOfShippingSum= 0.0;
			planShippingAvgOfDayGroupBy= rowObject.DIVISION;
			planOfShippingSum= planOfShippingSum + parseFloat(rowObject.PLANOFSHIPPING);
	    } else {
	    	planOfShippingSum= planOfShippingSum + parseFloat(rowObject.PLANOFSHIPPING);
	    }
		if(planOfShippingSum == 0.0 || isNaN(planOfShippingSum)) return 0;
		return (planOfShippingSum/nalsu[parseInt(month)-1]).toFixed(0);
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
		
		if(mitAvgOfDaySum == 0.0 || isNaN(mitAvgOfDaySum)) return 0;
		
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return (mitAvgOfDaySum / toDay).toFixed(0);
		} else {
			return (mitAvgOfDaySum / nalsu[parseInt(month)-1]).toFixed(0);
		}
	}
	function shippingRatio(cellValue, opetions, rowObject) {
		
		var year = $('#sel_year').val();
		var month = $('#sel_month').val().substr(0,2);
		var nalsu = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
		if ((year % 4 === 0) && (year % 100 !== 0) || (year % 400 === 0)) {
			nalsu[1] = 29; //윤년 체크
		}
		
		if (planShippingAvgOfDay2GroupBy != rowObject.DIVISION) {
			planOfShippingSum2= 0.0;
			mitAvgOfDaySum2= 0.0;
			planShippingAvgOfDay2GroupBy= rowObject.DIVISION;
			planOfShippingSum2= planOfShippingSum2 + parseFloat(rowObject.PLANOFSHIPPING);
			mitAvgOfDaySum2= mitAvgOfDaySum2 + parseFloat(rowObject.TOTALSUM);
	    } else {
	    	planOfShippingSum2= planOfShippingSum2 + parseFloat(rowObject.PLANOFSHIPPING);
			mitAvgOfDaySum2= mitAvgOfDaySum2 + parseFloat(rowObject.TOTALSUM);
	    }
		
		if(mitAvgOfDaySum2 == 0.0 || planOfShippingSum2 == 0.0 || isNaN(mitAvgOfDaySum2) || isNaN(planOfShippingSum2)) return (0).toFixed(2);
		
		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
			return ((mitAvgOfDaySum2 / ((planOfShippingSum2/dayCountOfThisMonth)* toDay)) * 100).toFixed(2);
		} else {
			return ((mitAvgOfDaySum2 / planOfShippingSum2) * 100).toFixed(2);
		}
	}
	
	var method = 'getDailyShipping';
	var reportData = null;
	var chartData = null;
	var chart2FieldNames = ["합계"];
	
	var getReportData = function(reportData){
	
		var chartValues = Array();
		for(var i=0; i<31; i++){
			var pktSum = 0;
			var pkgSum = 0;
			var nbizSum = 0;
			var sum = 0;
			for(var j=0; j<reportData.length; j++){
				if(reportData[j].DIVISION == "TEST"){
					pktSum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
				}else if(reportData[j].DIVISION == "PKG"){
					pkgSum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
				}else if(reportData[j].DIVISION == "SOC"){
					nbizSum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
				}
				sum += parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
			}
			chartValues[i] = {  일별: (i+1) + "", 
					TEST: pktSum,
					PKG: pkgSum,
					SOC: nbizSum,
					합계: sum};					
		}
		
		chartData = {
				values : chartValues,
				xFieldName : "일별",
				yValueName : "생산 실적",
				groupNames : ["TEST", "PKG", "SOC"]
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
	         height: 'auto',
	         footerrow:true,
	         grouping:true, //그룹화 하기위한 옵션
	         autowidth:true,
	         groupingView : {
	             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
	             groupSummary : [true], //소계를 보인다.
	             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
	             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
	         },
 	         colNames:['사업부','구분','금월', '일평균','MIT', '일평균','달성률', 'WIP', '1', '2','3', '4', '5', '6'	, '7'	, '8'	, '9'	, '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'],
	         colModel:[                  
	             {name:'DIVISION', index:'DIVISION', align: 'center',  sortable:false },
	             {name:'DEVICEGROUP', index:'DEVICEGROUP', width: '350',  sortable:false, summaryTpl: '<div><b>total</b></div>', summaryType: function(){}},
	             {name:'PLANOFSHIPPING', index:'PLANOFSHIPPING', width: '250', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'AVGPLANOFDAY', index:'AVGPLANOFDAY', width: '250', summaryType: planShippingAvgOfDay, sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'TOTALSUM', index:'TOTALSUM', width: '250', summaryType: 'sum', sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             //{name:'AVGOFDAY', index:'AVGOFDAY', width: '250', summaryType: mitAvgOfDay, sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'AVGOFDAY', index:'AVGOFDAY', width: '250', summaryType: 'sum', sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'PERSHIPPING', index:'PERSHIPPING', width: '250', summaryType: shippingRatio,  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ",", suffix:"%", defaultValue:"0%"}},
	             {name:'WIP', index:'WIP', summaryType: 'sum', sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C01', index:'C01', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C02', index:'C02', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C03', index:'C03', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C04', index:'C04', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C05', index:'C05', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C06', index:'C06', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C07', index:'C07', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C08', index:'C08', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C09', index:'C09', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C10', index:'C10', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C11', index:'C11', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C12', index:'C12', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C13', index:'C13', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C14', index:'C14', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C15', index:'C15', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C16', index:'C16', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C17', index:'C17', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C18', index:'C18', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C19', index:'C19', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C20', index:'C20', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C21', index:'C21', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C22', index:'C22', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C23', index:'C23', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C24', index:'C24', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C25', index:'C25', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C26', index:'C26', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C27', index:'C27', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C28', index:'C28', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C29', index:'C29', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C30', index:'C30', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}},
	             {name:'C31', index:'C31', summaryType: 'sum',  sortable:false, align: 'right', formatter:'integer', formatoptions: {thousandsSeparator: ","}}
	         ],
 	         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
	         gridComplete : function() { 

        	 	var PLANOFSHIPPINGSUM = $("#list").jqGrid('getCol', 'PLANOFSHIPPING', false, 'sum');
        	 	var MITTOTALSUM = $("#list").jqGrid('getCol', 'TOTALSUM', false, 'sum');

        	 	var year = $('#sel_year').val();
        		var month = $('#sel_month').val().substr(0,2);
        		var nalsu = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
        		if ((year % 4 === 0) && (year % 100 !== 0) || (year % 400 === 0)) {
        			nalsu[1] = 29; //윤년 체크
        		}
       	 	
        	 	var totalPlanAvgOfDay = PLANOFSHIPPINGSUM==0 ? (0).toFixed(0) : (PLANOFSHIPPINGSUM/nalsu[parseInt(month)-1]).toFixed(0);;
        	 	var totalShippingAvgOfDay = 0;
    			var perTotalShipping = 0;
        		if (($('#sel_year').val() + '' + $('#sel_month').val()) == date) {
        			if(MITTOTALSUM == 0 ) totalShippingAvgOfDay = 0;
        			else totalShippingAvgOfDay = (MITTOTALSUM / toDay).toFixed(0);
        			if(MITTOTALSUM == 0 || PLANOFSHIPPINGSUM == 0 ) perTotalShipping = (0).toFixed(2);
        			else perTotalShipping = ((MITTOTALSUM / ((PLANOFSHIPPINGSUM/dayCountOfThisMonth) * toDay)) * parseInt(100)).toFixed(2);
        		} else {
        			if(MITTOTALSUM == 0 ) totalShippingAvgOfDay = 0;
        			else totalShippingAvgOfDay = (MITTOTALSUM / nalsu[parseInt(month)-1]).toFixed(0);
        			if(MITTOTALSUM == 0 || PLANOFSHIPPINGSUM == 0 ) perTotalShipping = (0).toFixed(2);
        			else perTotalShipping = ((MITTOTALSUM / PLANOFSHIPPINGSUM) * parseInt(100)).toFixed(2);
        		}
        	 	
        	 	var WIPSUM = $("#list").jqGrid('getCol', 'WIP', false, 'sum');
        	 	
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

             	jQuery("#list").jqGrid('footerData', 'set', { DEVICEGROUP: 'Grand Total', PLANOFSHIPPING:PLANOFSHIPPINGSUM, TOTALSUM:MITTOTALSUM, AVGPLANOFDAY:totalPlanAvgOfDay
             		, AVGOFDAY : totalShippingAvgOfDay, PERSHIPPING: perTotalShipping, WIP: WIPSUM
             		, C01:C01SUM, C02:C02SUM, C03:C03SUM, C04:C04SUM, C05:C05SUM, C06:C06SUM
             		, C07:C07SUM, C08:C08SUM, C09:C09SUM, C10:C10SUM, C11:C11SUM, C12:C12SUM, C13:C13SUM, C14:C14SUM, C15:C15SUM, C16:C16SUM, C17:C17SUM
             		, C18:C18SUM, C19:C19SUM, C20:C20SUM, C21:C21SUM, C22:C22SUM, C23:C23SUM, C24:C24SUM, C25:C25SUM, C26:C26SUM, C27:C27SUM, C28:C28SUM
             		, C29:C29SUM, C30:C30SUM, C31:C31SUM});
	        	 
				$("#list").setGridWidth($('.js_work_report_view_page').width()-2);					        	 
	     		 loadChart(jQuery("#list").jqGrid('getRowData'));
		      	$('.ui-jqgrid-hdiv th').css('font-size', '11px').css('text-align', 'center');
	     		$('.ui-jqgrid-bdiv').css('overflow', 'hidden');
	    		$('.ui-jqgrid-bdiv td').css('font-size', '11px');
	    		$('.ui-jqgrid-sdiv td').css('font-size', '11px');
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
					{startColumnName: 'PLANOFSHIPPING', numberOfColumns: 2, titleText: '실행계획'},
					{startColumnName: 'TOTALSUM', numberOfColumns: 3, titleText: '출하실적'},
				]
		});

		$(".footrow td").css('background-color', '#E8FFFF');
 	};
	
	$(document).ready( function() {
		selectMenuItem('dailyShipping');
	 	loadGrid();
	});
	
	var reloadGrid = function(){
		 $("#list").setGridParam(
	   	 			{
	   	 				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $('#sel_month').val()
	   	 			}).trigger("reloadGrid");
	};
	$(function() { 
		$('.selDate').change(function() {
			reloadGrid();	  
		});   
		var prevWidth = $(window).width();
		$(window).resize(function() {
			if($(window).width() == prevWidth) return;
			if(!isEmpty($('.js_work_report_view_page'))){
				$("#list").setGridWidth($('.js_work_report_view_page').width()-2);				
	     		loadChart(jQuery("#list").jqGrid('getRowData'));
			}
		});
	}); 
	
</script>
 
</head>
<body>
	<div  class="js_work_report_view_page">
		<div class="kpi_tab_section">
			<jsp:include page="./chartMenu.jsp" flush="false"/>
			<table>
				<tr style="background-color:#dfeffc;border-left: 1px solid #bbbaba;border-right: 1px solid #bbbaba">
					<th>
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
					</th>
					<th style="float:right">(단위 : K)</th>
				</tr>
			</table>
			<table id="list"></table> 
			<br/><br/>
			<div class="js_work_report_view_page"><div id="chart_target"></div></div>
		</div>
	</div>
</body>
</html>
