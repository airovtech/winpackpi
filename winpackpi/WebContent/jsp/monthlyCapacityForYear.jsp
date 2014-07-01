<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<jsp:include page="commons.jsp"/>

<script type="text/javascript">

	var method = 'getMonthlyCapacityPkgForYear';
	var method2 = 'getMonthlyCapacityPkgForYearByGroup';
	
	var colNames = [];
	var colModels = [];
	var colNames2 = [];
	var colModels2 = [];
	
	var reportData = null;
	var chartData = null;
	var chart2FieldNames = ["달성율"];
	var y2Field = "달성율";
	
	var getReportData = function(reportData){
		var chartValues = Array();
		for(var i=0; i<12; i++){
			var capacity = 0;
			var result = 0;
			var rate = 0;
			for(var j=0; j<reportData.length; j++){
				var value = isEmpty(reportData[j]["C" + colNames[i+2] + "01"]) ? 0 : parseFloat(reportData[j]["C" + colNames[i+2] + "01"]);
				if(reportData[j].GUBUN === "Capacity"){
					capacity = value;
				}else if(reportData[j].GUBUN === "생산실적"){
					result = value;				
				}else if(reportData[j].GUBUN === "달성율"){
					rate = value;
				}
			}
			if(capacity>0 && result>0 )
				rate = result/capacity * 100;
			chartValues[i] = {  월별: colNames[i+2], 
					Capacity: capacity,
					생산실적: result,
					달성율: rate};
		}
		
		chartData = {
				values : chartValues,
				xFieldName : "월별",
				yValueName : "Capacity",
				groupNames : ["Capacity", "생산실적"]
		};
	};

	var loadChart = function(data) {
		getReportData(data);
		Ext.onReady(function () {
			smartChart.loadWithData(chartData, "column", false, "chart_target", chart2FieldNames, "line", y2Field);
		});
	};
	var loadGrid = function() {
		$.ajax({
		 	url:'../getKpiColumnInfo.jsp?method=getMonthlyCapacityPkgForYearColumnName&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			contentType : 'application/json',
			type : 'POST',
			dataType : "json",
			data : "",
			success : function(data, status, jqXHR) {
				var item = data.colName;
				for (var i = 0; i < item.length; i++) {
					colNames[0] = "사업부";
					colNames[1] = "구분";
					colNames[2] = (item[i].C_1);
					colNames[3] = (item[i].C_2);
					colNames[4] = (item[i].C_3);
					colNames[5] = (item[i].C_4);
					colNames[6] = (item[i].C_5);
					colNames[7] = (item[i].C_6);
					colNames[8] = (item[i].C_7);
					colNames[9] = (item[i].C_8);
					colNames[10] = (item[i].C_9);
					colNames[11] = (item[i].C_10);
					colNames[12] = (item[i].C_11);
					colNames[13] = (item[i].C_12);
					colNames[14] = (item[i].C_13);
					
					colModels[0] = {name:item[i].C_DIVISION, index:item[i].C_DIVISION, align: 'center',  sortable:false};
					colModels[1] = {name:item[i].C_GUBUN, index:item[i].C_GUBUN,  sortable:false};
					colModels[2] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[3] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[4] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[5] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[6] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[7] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[8] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[9] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[10] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[11] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[12] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[13] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[14] = {name:item[i].C_13, index:item[i].C_13, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
				};
				jQuery("#list").jqGrid({
			   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
			         datatype: "json",      //json형태로 데이터 받음.  
			         height: "auto",
			         footerrow:false,
			         grouping:false, //그룹화 하기위한 옵션
			         autowidth:true,
			         colNames:colNames,
			         colModel:colModels,
			         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
			         gridComplete : function() { 
						$("#list").setGridWidth($('.js_work_report_view_page').width()-2);				
			     		 loadChart(jQuery("#list").jqGrid('getRowData'));
			         },
			          loadError:function(xhr, status, error) {          //---데이터 못가져오면 실행 됨
			          },
			          jsonReader : {                             //가져온 데이터를 읽을 때 사용
			             root: "rows",   // json으로 저장 된 객체의 root명
			             repeatitems: false     //얜 뭐지... 일단 필요한거같은데... 
			   		 },
			         multiselect: false,         //전체선택 체크박스 유무, 테이블에서 row 체크를 멀티로 할 수 있는 옵션.
			     });
			},
			error : function(xhr, ajaxOptions, thrownError) {
			}
		});
	};
	var refreshColName = function() {
		colNames = [];
		colModels = [];
		$.ajax({
			url:'../getKpiColumnInfo.jsp?method=getMonthlyCapacityPkgForYearColumnName&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			contentType : 'application/json',
			type : 'POST',
			dataType : "json",
			data : "",
			success : function(data, status, jqXHR) {
				var item = data.colName;
				for (var i = 0; i < item.length; i++) {
					colNames[0] = "사업부";
					colNames[1] = "구분";
					colNames[2] = (item[i].C_1);
					colNames[3] = (item[i].C_2);
					colNames[4] = (item[i].C_3);
					colNames[5] = (item[i].C_4);
					colNames[6] = (item[i].C_5);
					colNames[7] = (item[i].C_6);
					colNames[8] = (item[i].C_7);
					colNames[9] = (item[i].C_8);
					colNames[10] = (item[i].C_9);
					colNames[11] = (item[i].C_10);
					colNames[12] = (item[i].C_11);
					colNames[13] = (item[i].C_12);
					colNames[14] = (item[i].C_13);
					
					colModels[0] = {name:item[i].C_DIVISION, index:item[i].C_DIVISION, align: 'center',  sortable:false };
					colModels[1] = {name:item[i].C_GUBUN, index:item[i].C_GUBUN,  sortable:false };
					colModels[2] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[3] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[4] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[5] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[6] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[7] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[8] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[9] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[10] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[11] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[12] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[13] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					colModels[14] = {name:item[i].C_13, index:item[i].C_13, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}};
					
				};

				$("#list").jqGrid('setGridParam', { colModel: colModels});
				var colNamess= $("#list").jqGrid('getGridParam', 'colModel');
				for (var i=0; i < colNamess.length; i++) {
					$("#list").jqGrid('setLabel', colNamess[i]['name'], colNames[i]);
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
			}
		});
	};
	
	
	$(document).ready( function() { 
		selectMenuItem('monthlyCapacityForYear');
		loadGrid();	 
	});
	var reloadGrid = function(){
		refreshColName();
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
