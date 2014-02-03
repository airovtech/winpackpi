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
		console.log('reportData=', reportData);
		console.log('colNames=', colNames);
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
			if(rate==0 && capacity>0 && result>0 )
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
	var loadGrid2 = function() {
		$.ajax({
		 	url:'../getKpiColumnInfo.jsp?method=getMonthlyCapacityPkgForYearByGroupColumnName&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			contentType : 'application/json',
			type : 'POST',
			dataType : "json",
			data : "",
			success : function(data, status, jqXHR) {
				var item = data.colName;
				for (var i = 0; i < item.length; i++) {
					colNames2[0] = "제품군";
					colNames2[1] = "구분";
					colNames2[2] = (item[i].C_1);
					colNames2[3] = (item[i].C_2);
					colNames2[4] = (item[i].C_3);
					colNames2[5] = (item[i].C_4);
					colNames2[6] = (item[i].C_5);
					colNames2[7] = (item[i].C_6);
					colNames2[8] = (item[i].C_7);
					colNames2[9] = (item[i].C_8);
					colNames2[10] = (item[i].C_9);
					colNames2[11] = (item[i].C_10);
					colNames2[12] = (item[i].C_11);
					colNames2[13] = (item[i].C_12);
					
					colModels2[0] = {name:item[i].C_DEVICEGROUP, index:item[i].C_DEVICEGROUP, align: 'center',  sortable:false };
					colModels2[1] = {name:item[i].C_GUBUN, index:item[i].C_GUBUN, align: 'center',  sortable:false };
					colModels2[2] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'center',  sortable:false };
					colModels2[3] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'center',  sortable:false };
					colModels2[4] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'center',  sortable:false };
					colModels2[5] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'center',  sortable:false };
					colModels2[6] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'center',  sortable:false };
					colModels2[7] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'center',  sortable:false };
					colModels2[8] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'center',  sortable:false };
					colModels2[9] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'center',  sortable:false };
					colModels2[10] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'center',  sortable:false };
					colModels2[11] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'center',  sortable:false };
					colModels2[12] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'center',  sortable:false };
					colModels2[13] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'center',  sortable:false };
				};
				jQuery("#list2").jqGrid({
			   		 url:'../getKpi.jsp?method=' + method2 + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
			         datatype: "json",      //json형태로 데이터 받음.  
			         height: "auto",
			         caption: "1년간 제품군별 Capacity 대비 실적",
			         footerrow:false,
			         grouping:true, //그룹화 하기위한 옵션
			         autowidth:true,
			         groupingView : {
			             groupField : ['DEVICEGROUP'], //그룹화 기준이 되는 컬럼명
			             groupSummary : [false], //소계를 보인다.
			             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
			             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
			         },
			         colNames:colNames2,
			         colModel:colModels2,
			         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
			         gridComplete : function() { 
			        	 console.log('colNames2', colNames2);
			        	 console.log('colModels2', colModels2);
			        	 console.log('data', jQuery("#list2").jqGrid('getRowData'));
			         },
			          loadError:function(xhr, status, error) {          //---데이터 못가져오면 실행 됨
			            alert('error'); 
			          },
			          jsonReader : {                             //가져온 데이터를 읽을 때 사용
			             root: "rows",   // json으로 저장 된 객체의 root명
			             repeatitems: false     //얜 뭐지... 일단 필요한거같은데... 
			   		 },
			         multiselect: false,         //전체선택 체크박스 유무, 테이블에서 row 체크를 멀티로 할 수 있는 옵션.
			     });
			},
			error : function(xhr, ajaxOptions, thrownError) {
				alert("통신실패");
			}
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
					
					colModels[0] = {name:item[i].C_DIVISION, index:item[i].C_DIVISION, align: 'center',  sortable:false };
					colModels[1] = {name:item[i].C_GUBUN, index:item[i].C_GUBUN, align: 'center',  sortable:false };
					colModels[2] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'center',  sortable:false };
					colModels[3] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'center',  sortable:false };
					colModels[4] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'center',  sortable:false };
					colModels[5] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'center',  sortable:false };
					colModels[6] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'center',  sortable:false };
					colModels[7] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'center',  sortable:false };
					colModels[8] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'center',  sortable:false };
					colModels[9] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'center',  sortable:false };
					colModels[10] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'center',  sortable:false };
					colModels[11] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'center',  sortable:false };
					colModels[12] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'center',  sortable:false };
					colModels[13] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'center',  sortable:false };
					colModels[14] = {name:item[i].C_13, index:item[i].C_13, align: 'center',  sortable:false };
				};
				jQuery("#list").jqGrid({
			   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
			         datatype: "json",      //json형태로 데이터 받음.  
			         height: "auto",
			         caption: "1년간 Capacity 대비 실적",
			         footerrow:false,
			         grouping:false, //그룹화 하기위한 옵션
			         autowidth:true,
			         colNames:colNames,
			         colModel:colModels,
			         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
			         gridComplete : function() { 
<<<<<<< HEAD
			        	 console.log('colNames', colNames);
			        	 console.log('colModels', colModels);
			        	 console.log('data', jQuery("#list").jqGrid('getRowData'));
=======
						$("#list").setGridWidth($('.js_work_report_view_page').width());				
>>>>>>> branch 'master' of https://github.com/maninsoft/winpackpi.git
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
	var refreshColName2 = function() {
		colNames2 = [];
		colModels2 = [];
		$.ajax({
			url:'../getKpiColumnInfo.jsp?method=getMonthlyCapacityPkgForYearByGroupColumnName&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			contentType : 'application/json',
			type : 'POST',
			dataType : "json",
			data : "",
			success : function(data, status, jqXHR) {
				var item = data.colName;
				for (var i = 0; i < item.length; i++) {
					colNames2[0] = "제품군";
					colNames2[1] = "구분";
					colNames2[2] = (item[i].C_1);
					colNames2[3] = (item[i].C_2);
					colNames2[4] = (item[i].C_3);
					colNames2[5] = (item[i].C_4);
					colNames2[6] = (item[i].C_5);
					colNames2[7] = (item[i].C_6);
					colNames2[8] = (item[i].C_7);
					colNames2[9] = (item[i].C_8);
					colNames2[10] = (item[i].C_9);
					colNames2[11] = (item[i].C_10);
					colNames2[12] = (item[i].C_11);
					colNames2[13] = (item[i].C_12);
					
					colModels2[0] = {name:item[i].C_DEVICEGROUP, index:item[i].C_DEVICEGROUP, align: 'center',  sortable:false };
					colModels2[1] = {name:item[i].C_GUBUN, index:item[i].C_GUBUN, align: 'center',  sortable:false };
					colModels2[2] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'center',  sortable:false };
					colModels2[3] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'center',  sortable:false };
					colModels2[4] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'center',  sortable:false };
					colModels2[5] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'center',  sortable:false };
					colModels2[6] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'center',  sortable:false };
					colModels2[7] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'center',  sortable:false };
					colModels2[8] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'center',  sortable:false };
					colModels2[9] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'center',  sortable:false };
					colModels2[10] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'center',  sortable:false };
					colModels2[11] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'center',  sortable:false };
					colModels2[12] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'center',  sortable:false };
					colModels2[13] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'center',  sortable:false };
					
				};

				$("#list2").jqGrid('setGridParam', { colModel: colModels2});
				var colNamess2= $("#list2").jqGrid('getGridParam', 'colModel');
				for (var i=0; i < colNamess2.length; i++) {
					$("#list2").jqGrid('setLabel', colNamess2[i]['name'], colNames2[i]);
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
				alert("통신실패");
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
					colModels[1] = {name:item[i].C_GUBUN, index:item[i].C_GUBUN, align: 'center',  sortable:false };
					colModels[2] = {name:'C'+item[i].C_1 + '01', index:item[i].C_1, align: 'center',  sortable:false };
					colModels[3] = {name:'C'+item[i].C_2 + '01', index:item[i].C_2, align: 'center',  sortable:false };
					colModels[4] = {name:'C'+item[i].C_3 + '01', index:item[i].C_3, align: 'center',  sortable:false };
					colModels[5] = {name:'C'+item[i].C_4 + '01', index:item[i].C_4, align: 'center',  sortable:false };
					colModels[6] = {name:'C'+item[i].C_5 + '01', index:item[i].C_5, align: 'center',  sortable:false };
					colModels[7] = {name:'C'+item[i].C_6 + '01', index:item[i].C_6, align: 'center',  sortable:false };
					colModels[8] = {name:'C'+item[i].C_7 + '01', index:item[i].C_7, align: 'center',  sortable:false };
					colModels[9] = {name:'C'+item[i].C_8 + '01', index:item[i].C_8, align: 'center',  sortable:false };
					colModels[10] = {name:'C'+item[i].C_9 + '01', index:item[i].C_9, align: 'center',  sortable:false };
					colModels[11] = {name:'C'+item[i].C_10 + '01', index:item[i].C_10, align: 'center',  sortable:false };
					colModels[12] = {name:'C'+item[i].C_11 + '01', index:item[i].C_11, align: 'center',  sortable:false };
					colModels[13] = {name:'C'+item[i].C_12 + '01', index:item[i].C_12, align: 'center',  sortable:false };
					colModels[14] = {name:item[i].C_13, index:item[i].C_13, align: 'center',  sortable:false };
					
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
	
	
<<<<<<< HEAD
	$(document).ready( function() {
		loadGrid2();
=======
	$(document).ready( function() { 
		selectMenuItem('monthlyCapacityForYear');
>>>>>>> branch 'master' of https://github.com/maninsoft/winpackpi.git
		loadGrid();	 
	});
	var reloadGrid2 = function(){
		refreshColName2();
		$("#list2").setGridParam(
			{
				url : "../getKpi.jsp?method=" + method2 + "&yearMonth=" +  $('#sel_year').val() + $('#sel_month').val() ,
			}).trigger("reloadGrid");
	};
	var reloadGrid = function(){
		refreshColName();
		$("#list").setGridParam(
				{
					url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $('#sel_month').val() ,
				}).trigger("reloadGrid");
	};
	$(function() { 
		$('.selDate').change(function() {
			reloadGrid2();
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
<select id='sel_year' class='selDate'>
	<option value='2013'>2013년</option>
	<option selected value='2014'>2014년</option>
</select>
<select id='sel_month' class='selDate'>
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
<table id="list2"></table> 
</br>
</br>
<table id="list"></table> 
<br/><br/>
<div class="js_work_report_view_page">
	<div id="chart_target"></div>
</div>
</body>
</html>
