<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<jsp:include page="commons.jsp"/>

<script type="text/javascript">

	var method = 'getDailyTat';
	
	var reportData = null;
	var chartData = null;
	var chart2FieldNames = ["고객TAT", "목표TAT"];
	
	var getReportData = function(reportData){
		
		chartData = new Array();
		
		for(var i=0; i<reportData.length; i++){
			var chartValues = Array();
			
			for(var j=0; j<31; j++){
				if(isEmpty(reportData[i]["C" + String("0" + (j+1)).slice(-2)])){
					chartValues.push({  
						일별: (j+1) + "" 
					});					
				}else{
					chartValues.push({  
							일별: (j+1) + "", 
							TAT: parseFloat(reportData[i]["C" + String("0" + (j+1)).slice(-2)]),
							고객TAT: parseFloat(reportData[i].CUSTOMERTAT),
							목표TAT: parseFloat(reportData[i].TARGETTAT)
						});
				}
			}
			
			chartData.push({
					title : reportData[i].DEVICEGROUP,
					values : chartValues,
					xFieldName : "일별",
					yValueName : "TAT",
					groupNames : ["TAT"]
			});
		}
	};
	
	var loadChart = function(data) {
		getReportData(data);
		Ext.onReady(function () {						 
			$('.js_work_report_view_page .js_chart_target').remove();			
			for(var i=0; i<chartData.length; i++){
				$('.js_work_report_view_page').append('<div id="chart_target' + (i+1) + '" class="js_chart_target"></div>');
				smartChart.loadWithData(chartData[i], "line", false, "chart_target"+(i+1), chart2FieldNames, "line");
			}
		});
	};

	var loadGrid = function() {
		jQuery("#list").jqGrid({
	   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
	         datatype: "json",      //json형태로 데이터 받음.  
	         height: "auto",
	         footerrow:false,
	         grouping:true, //그룹화 하기위한 옵션
	         autowidth:true,
	         groupingView : {
	             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
	             groupSummary : [false], //소계를 보인다.
	             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
	             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
	         },
	         
	         colNames:['사업부','구분','고객TAT', '목표TAT','전월평균','금월평균', '1', '2','3', '4', '5', '6', '7'	, '8'	, '9'	, '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'],
	         colModel:[                  
	             {name:'DIVISION', index:'DIVISION', align: 'center',  sortable:false },
	             {name:'DEVICEGROUP', index:'DEVICEGROUP', width:'250',  sortable:false},
	             {name:'CUSTOMERTAT', index:'CUSTOMERTAT', width:'250', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'TARGETTAT', index:'TARGETTAT', width:'250', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'LASTMONTHAVGTAT', index:'LASTMONTHAVGTAT', width:'250', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'MONTHAVGTAT', index:'MONTHAVGTAT', width:'250', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C01', index:'C01', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C02', index:'C02', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C03', index:'C03', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C04', index:'C04', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C05', index:'C05', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C06', index:'C06', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C07', index:'C07', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C08', index:'C08', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C09', index:'C09', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C10', index:'C10', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C11', index:'C11', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C12', index:'C12', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C13', index:'C13', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C14', index:'C14', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C15', index:'C15', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C16', index:'C16', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C17', index:'C17', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C18', index:'C18', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C19', index:'C19', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C20', index:'C20', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C21', index:'C21', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C22', index:'C22', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C23', index:'C23', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C24', index:'C24', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C25', index:'C25', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C26', index:'C26', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C27', index:'C27', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C28', index:'C28', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C29', index:'C29', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C30', index:'C30', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}},
	             {name:'C31', index:'C31', summaryType: 'sum',  align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ",", decimalPlaces: 2}}
	         ],
	         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
	          gridComplete : function() { 
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
	};
	
 	$(document).ready( function() { 
		selectMenuItem('dailyTat');
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
				</tr>
			</table>
			<table id="list"></table> 
			<br/><br/>
			<div class="js_work_report_view_page"></div>
		</div>
	</div>
</body>
</html>
