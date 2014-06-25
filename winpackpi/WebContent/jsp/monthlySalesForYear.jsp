<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<jsp:include page="commons.jsp"/>

<script type="text/javascript">

	var method = 'getMonthlySalesForYear';
	
	var colNames = [];
	var colModels = [];
	
	var reportData = null;
	var chartData = null;
	var chart2FieldNames = ["합계"];
	
	var getReportData = function(reportData){
		var chartValues = Array();
		for(var i=0; i<12; i++){
			var pkt = 0;
			var pkg = 0;
			var nbiz = 0;
			var sum = 0;
			for(var j=0; j<reportData.length; j++){
				var value = isEmpty(reportData[j]["C" + colNames[i+1] + "01"]) ? 0 : parseInt(reportData[j]["C" + colNames[i+1] + "01"]);
				if(reportData[j].DIVISION === "PKT"){
					pkt = value;
				}else if(reportData[j].DIVISION === "PKG"){
					pkg = value;
					
				}else if(reportData[j].DIVISION === "NBIZ"){
					nbiz = value;
				}
				sum += value;
			}
			chartValues[i] = {  월별: colNames[i+1], 
					PKT: pkt,
					PKG: pkg,
					NBIZ: nbiz,
					합계: sum};					
		}
		
		chartData = {
				values : chartValues,
				xFieldName : "월별",
				yValueName : "PKT",
				groupNames : ["PKT", "PKG", "NBIZ"]
		};
	};
	
	var loadChart = function(data) {
		getReportData(data);
		Ext.onReady(function () {
			smartChart.loadWithData(chartData, "line", false, "chart_target", chart2FieldNames, "area");
		});
	};

	var loadGrid = function() {
		$.ajax({
		 	url:'../getKpiColumnInfo.jsp?method=getMonthlyShippingNSalesForYearColumnName&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			contentType : 'application/json',
			type : 'POST',
			dataType : "json",
			data : "",
			success : function(data, status, jqXHR) {
				
				var item = data.colName;
				for (var i = 0; i < item.length; i++) {
					colNames[0] = (item[i].C_0);
					colNames[1] = (item[i].C_1);
					colNames[2] = (item[i].C_2);
					colNames[3] = (item[i].C_3);
					colNames[4] = (item[i].C_4);
					colNames[5] = (item[i].C_5);
					colNames[6] = (item[i].C_6);
					colNames[7] = (item[i].C_7);
					colNames[8] = (item[i].C_8);
					colNames[9] = (item[i].C_9);
					colNames[10] = (item[i].C_10);
					colNames[11] = (item[i].C_11);
					colNames[12] = (item[i].C_12);
					colNames[13] = (item[i].C_13);
					
					colModels[0] = {name:item[i].C_0, index:item[i].C_0, align: 'center',  sortable:false };
					colModels[1] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[2] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[3] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[4] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[5] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[6] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[7] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[8] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[9] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[10] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[11] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[12] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					colModels[13] = {name:item[i].C_13, index:item[i].C_13, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","} };
					
				}
					 
				jQuery("#list").jqGrid({
			   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
			         datatype: "json",      //json형태로 데이터 받음.  
			         height: "auto",
			         footerrow:true,
			         grouping:false, //그룹화 하기위한 옵션
			         autowidth:true,
			         colNames:colNames,
			         colModel:colModels,
			         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
			          gridComplete : function() { 
			        	  
			        	 var modelValue = {}; 
			        	 for (var i =0; i < colModels.length; i++) {
			        		 if (colModels[i].name == 'DIVISION') {
			        			 modelValue['DIVISION'] = 'Total';      			 
			        		 } else {
				        		 var colName = colModels[i].name;
				         	 	 var value = $("#list").jqGrid('getCol', colName, false, 'sum');
				         	 	 modelValue[colName] = value;
			        		 }
			        	 }
			        	 jQuery("#list").jqGrid('footerData', 'set', modelValue);

			 			 $(".footrow td").css('background-color', '#E8FFFF');
			        	  
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
			        // caption: "" //"Manipulating Array Data"    //caption을 달겠다는 거겠지. 
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
		 	url:'../getKpiColumnInfo.jsp?method=getMonthlyShippingNSalesForYearColumnName&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			contentType : 'application/json',
			type : 'POST',
			dataType : "json",
			data : "",
			success : function(data, status, jqXHR) {
				
				var item = data.colName;
				for (var i = 0; i < item.length; i++) {
					colNames[0] = (item[i].C_0);
					colNames[1] = (item[i].C_1);
					colNames[2] = (item[i].C_2);
					colNames[3] = (item[i].C_3);
					colNames[4] = (item[i].C_4);
					colNames[5] = (item[i].C_5);
					colNames[6] = (item[i].C_6);
					colNames[7] = (item[i].C_7);
					colNames[8] = (item[i].C_8);
					colNames[9] = (item[i].C_9);
					colNames[10] = (item[i].C_10);
					colNames[11] = (item[i].C_11);
					colNames[12] = (item[i].C_12);
					colNames[13] = (item[i].C_13);
					
					colModels[0] = {name:item[i].C_0, index:item[i].C_0, align: 'center',  sortable:false };
					colModels[1] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[2] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[3] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[4] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[5] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[6] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[7] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[8] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[9] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[10] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[11] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[12] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					colModels[13] = {name:item[i].C_13, index:item[i].C_13, align: 'right',  sortable:false, formatter:'integer',  formatoptions:{defaultValue:'0', thousandsSeparator: ","}  };
					
				}
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
		selectMenuItem('monthlySalesForYear');
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
		$(window).resize(function() {
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
				<tr style="background-color:#dfeffc">
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
					<th style="float:right">(단위 : 백만원)</th>
				</tr>
			</table>
			<table id="list"></table> 
			<br/><br/>
			<div class="js_work_report_view_page"><div id="chart_target"></div></div>
		</div>
	</div>
</body>
</html>
