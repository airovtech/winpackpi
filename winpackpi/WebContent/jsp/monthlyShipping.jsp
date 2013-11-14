<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/themes/redmond/jquery-ui-1.8.2.custom.css" />
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/themes/ui.jqgrid.css" />
<link href="../js/ext/ext-all.css" type="text/css" rel="stylesheet" />

<script src="../js/jqgrid/js/jquery.min.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery-ui-1.8.2.custom.min.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery.layout.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/ext/ext-all.js"></script>
<script type="text/javascript" src="../js/smartChart-sencha.js"></script>


<link rel="stylesheet" type="text/css" href="../css/jquery.jqChart.css" />
<script src="../js/jquery.jqChart.min.js" type="text/javascript"></script>

<script type="text/javascript">

var method = 'getMonthlyShipping';

var reportData = null;
var chartData = new Array();
var chart2FieldNames = ["PLANvs실적", "Forecastvs실적"];
var y2Field = "PLANvs실적";

var getReportData = function(data){
	reportData = data.rows;
	console.log('reportData=', reportData);
	var divisions = ['pkt', 'pkg', 'nbiz', 'TOTAL'];
	for(var idx=0; idx<divisions.length; idx++){
		var chartValues = Array();
	 	for(var i=0; i<12; i++){
	 		var plan = 0;
	 		var forecast = 0;
	 		var result = 0;
	 		var planResult = 0;
	 		var forecastResult = 0;
	 		for(var j=0; j<reportData.length; j++){
	 			if(reportData[j].DIVISION != divisions[idx] ) continue;
	 			
	 			if(reportData[j].GUBUN == "PLAN"){
	 				plan = parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}else if(reportData[j].GUBUN == "Forecast"){
	 				forecast = parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}else if(reportData[j].GUBUN == "생산 실적"){
	 				result = parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}else if(reportData[j].GUBUN == "PLAN vs 실적 (%)"){
	 				planResult = parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}else if(reportData[j].GUBUN == "Forecast vs 실적 (%)"){
	 				forecastResult = parseInt(reportData[j]["C" + String("0" + (i+1)).slice(-2)]);
	 			}
	 		}
	 		chartValues[i] = {  월별: (i+1) + "", 
	 				PLAN: plan,
	 				Forecast: forecast,
	 				생산실적: result,
	 				PLANvs실적: planResult,
	 				Forecastvs실적 : forecastResult};					
	 	}
		
		chartData.push({
				title : divisions[idx],
				values : chartValues,
				xFieldName : "월별",
				yValueName : "PLAN",
				groupNames : ["PLAN", "Forecast", "생산실적"]
		});
		
		console.log("chartData=", chartData);
	}
};

 $(document).ready( function() { 
	 
	 $.ajax({
			url : '../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			data : {},
			dataType: 'json',
			success : function(data, status, jqXHR) {
				
				getReportData(data);
				  jQuery("#list").jqGrid({
				   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
				         datatype: "json",      //json형태로 데이터 받음.  
				         height: 'auto',
				         caption: "월별 생산 실적 현황",
				         footerrow:false,
				         grouping:true, //그룹화 하기위한 옵션
				         autowidth:true,
				         groupingView : {
				             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
				             groupSummary : [false], //소계를 보인다.
				             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
				             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
				         },
				         
				         colNames:['사업부','구분', '1월', '2월','3월', '4월', '5월', '6월', '7월'	, '8월'	, '9월'	, '10월', '11월', '12월', 'YTD', 'TOTAL'],
				         colModel:[                  
				             {name:'DIVISION', index:'DIVISION', align: 'center',  sortable:false },
				             {name:'GUBUN', index:'GUBUN', align: 'center',  sortable:false },
				             {name:'C01', index:'C01', summaryType: 'sum',  sortable:false},
				             {name:'C02', index:'C02', summaryType: 'sum',  sortable:false},
				             {name:'C03', index:'C03', summaryType: 'sum',  sortable:false},
				             {name:'C04', index:'C04', summaryType: 'sum',  sortable:false},
				             {name:'C05', index:'C05', summaryType: 'sum',  sortable:false},
				             {name:'C06', index:'C06', summaryType: 'sum',  sortable:false},
				             {name:'C07', index:'C07', summaryType: 'sum',  sortable:false},
				             {name:'C08', index:'C08', summaryType: 'sum',  sortable:false},
				             {name:'C09', index:'C09', summaryType: 'sum',  sortable:false},
				             {name:'C10', index:'C10', summaryType: 'sum',  sortable:false},
				             {name:'C11', index:'C11', summaryType: 'sum',  sortable:false},
				             {name:'C12', index:'C12', summaryType: 'sum',  sortable:false},
				             {name:'YTD', index:'YTD', summaryType: 'sum',  sortable:false},
				             {name:'TOTAL', index:'TOTAL', summaryType: 'sum',  sortable:false}
				         ],
				         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
				          gridComplete : function() { 
				        	  
				        	  //---데이터를 성공적으로 가져오면 실행 됨
			            	/* var sumOfSales = $("#list").jqGrid('getCol', 'SUMOFSALES', false, 'sum');
			            	var shippingPlan = $("#list").jqGrid('getCol', 'SHIPPINGPLAN', false, 'sum');
			            	var salesPlan = $("#list").jqGrid('getCol', 'SALESPLAN', false, 'sum');
			            	var shippingFocPlan = $("#list").jqGrid('getCol', 'SHIPPINGFOCPLAN', false, 'sum');
			            	var salesFocPlan = $("#list").jqGrid('getCol', 'SALESFOCPLAN', false, 'sum');
			            	var shippingExePlan = $("#list").jqGrid('getCol', 'SHIPPINGEXEPLAN', false, 'sum');
			            	var salesExePlan = $("#list").jqGrid('getCol', 'SALESEXEPLAN', false, 'sum');
			            	var sumOfReceiving = $("#list").jqGrid('getCol', 'SUMOFRECEIVING', false, 'sum');
			            	var sumOfShipping = $("#list").jqGrid('getCol', 'SUMOFSHIPPING', false, 'sum');
			            	
			             	jQuery("#list").jqGrid('footerData', 'set', { DEVICEGROUP: 'Grand Total', SUMOFSALES: sumOfSales, SHIPPINGPLAN: shippingPlan, SALESPLAN: salesPlan, SHIPPINGFOCPLAN: shippingFocPlan, SALESFOCPLAN: salesFocPlan, SHIPPINGEXEPLAN: shippingExePlan, SALESEXEPLAN: salesExePlan, SUMOFRECEIVING: sumOfReceiving, SUMOFSHIPPING: sumOfShipping}); */
				          },
				          loadError:function(xhr, status, error) {          //---데이터 못가져오면 실행 됨
				            alert('error'); 
				          },
				          jsonReader : {                             //가져온 데이터를 읽을 때 사용
				             root: "rows",   // json으로 저장 된 객체의 root명
				             repeatitems: false     //얜 뭐지... 일단 필요한거같은데... 
				   		},
				         multiselect: false,         //전체선택 체크박스 유무, 테이블에서 row 체크를 멀티로 할 수 있는 옵션.
				        // caption: "" //"Manipulating Array Data"    //caption을 달겠다는 거겠지. 
				     });
  
					for(var i=0; i<reportData.length; i++){
						$('#list').jqGrid('addRowData', i+1, reportData[i]);
					}
					
					Ext.onReady(function () {
						$('.js_work_report_view_page').html('');
		 				for(var i=0; i<chartData.length; i++){
		 					$('.js_work_report_view_page').append('<div id="chart_target' + (i+1) + '"></div>');
							smartChart.loadWithData(chartData[i], "column", false, "chart_target"+(i+1), chart2FieldNames, "line", y2Field);
		 				}
					});
		
			},
			error : function(xhr, ajaxOptions, thrownError){
				
			}
		});
 });
 
 
 	$(function() { 
	   $('#sel_month').change(function() {
		   $.ajax({
				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $(this).val(),
				data : {},
				dataType: 'json',
				success : function(data, status, jqXHR) {
					getReportData(data);
					for(var i=0; i<reportData.length; i++){
						$('#list').jqGrid('addRowData', i+1, reportData[i]);
					}
					
					$("#list").setGridParam(
					{
						url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $(this).val() ,
					}).trigger("reloadGrid");
					
					$('.js_work_report_view_page').html('');
	 				for(var i=0; i<chartData.length; i++){
	 					$('.js_work_report_view_page').append('<div id="chart_target' + (i+1) + '"></div>');
						smartChart.loadWithData(chartData[i], "column", false, "chart_target"+(i+1), chart2FieldNames, "line", y2Field);
	 				}
				},
				error : function(xhr, ajaxOptions, thrownError){
					
				}
		   });
		}); 
	}); 
 	$(function() { 
 	   $('#sel_year').change(function() {
			 $.ajax({
					url :  "../getKpi.jsp?method=" + method + "&yearMonth=" +  $(this).val() + $('#sel_month').val() ,
					data : {},
					dataType: 'json',
					success : function(data, status, jqXHR) {
						getReportData(data);
						
						for(var i=0; i<reportData.length; i++){
							$('#list').jqGrid('addRowData', i+1, reportData[i]);
						}
						
			 			$("#list").setGridParam(
			 			{
			 				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $(this).val() + $('#sel_month').val() ,
			 			}).trigger("reloadGrid");
			 			
						$('.js_work_report_view_page').html('');
		 				for(var i=0; i<chartData.length; i++){
		 					$('.js_work_report_view_page').append('<div id="chart_target' + (i+1) + '"></div>');
							smartChart.loadWithData(chartData[i], "column", false, "chart_target"+(i+1), chart2FieldNames, "line", y2Field);
		 				}
					},
					error : function(xhr, ajaxOptions, thrownError){
						
					}
				});
 	   }); 
 	}); 
</script>
 
</head>
<body>
<div>
<jsp:include page="./chartMenu.jsp" flush="false"/>
</div>
<div>
<select id='sel_year'>
	<option value='2012'>2012년</option>
	<option selected value='2013'>2013년</option>
</select>
<select id='sel_month' style='display:none;'>
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
<br/><br/>
<div class="js_work_report_view_page">
</div>
</body>
</html>