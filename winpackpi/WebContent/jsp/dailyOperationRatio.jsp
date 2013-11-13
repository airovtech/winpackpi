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

var method = 'getDailyOperationRatio';

var testData = {rows : [
                {DIVISION : 'pkg', GUBUN: 'G1', TARGETOR: '90', LASTMONTHAVG: '80', MONTHAVG: '75', C01: '77', C02: '67', C03: '79', C04: '67', C05: '88', C06: '97', C07: '67', C08: '87', C09: '75', C10: '77', C11: '66', C12: '87', C13: '88', C14: '77', C15: '67', C16: '55', C17: '99', C18: '77', C19: '45', C20: '77', C21: '88', C22: '77', C23: '93', C24: '72', C25: '60', C26: '91', C27: '84', C28: '40', C29: '59', C30: '100', C31: '83'},
                {DIVISION : 'pkt', GUBUN: 'G2', TARGETOR: '80', LASTMONTHAVG: '80', MONTHAVG: '85', C01: '69', C02: '77', C03: '59', C04: '77', C05: '78', C06: '90', C07: '87', C08: '57', C09: '70', C10: '47', C11: '66', C12: '97', C13: '68', C14: '87', C15: '77', C16: '55', C17: '80', C18: '67', C19: '85', C20: '87', C21: '78', C22: '67', C23: '90', C24: '78', C25: '80', C26: '81', C27: '89', C28: '80', C29: '89', C30: '90', C31: '80'},
                {DIVISION : 'nbiz', GUBUN: 'G3', TARGETOR: '85', LASTMONTHAVG: '80', MONTHAVG: '85', C01: '77', C02: '67', C03: '79', C04: '67', C05: '88', C06: '97', C07: '67', C08: '87', C09: '75', C10: '77', C11: '66', C12: '87', C13: '88', C14: '77', C15: '67', C16: '55', C17: '99', C18: '77', C19: '45', C20: '77', C21: '88', C22: '77', C23: '93', C24: '72', C25: '60', C26: '91', C27: '84', C28: '40', C29: '59', C30: '100', C31: '83'}
                ]};

var reportData = null;
var chartData = new Array();
var chart2FieldName = "목표가동율";

var getReportData = function(data){
	reportData = data.rows;
	console.log('reportData=', reportData);
	
	for(var i=0; i<reportData.length; i++){
		var chartValues = Array();
		
		
		for(var j=0; j<31; j++){
			chartValues.push({  
					일별: (j+1) + "", 
					가동율: parseInt(reportData[i]["C" + String("0" + (j+1)).slice(-2)]),
					목표가동율: parseInt(reportData[i].TARGETOR)
				});					
		}
		
		console.log('chartValues=', chartValues);
		
		chartData.push({
				title : reportData[i].GUBUN,
				values : chartValues,
				xFieldName : "일별",
				yValueName : "가동율",
				groupNames : ["가동율"]
		});
	}

	console.log('chartData=', chartData);


};

 $(document).ready( function() { 
	 Ext.onReady(function () {
	 
 	 $.ajax({
			url : '../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),
			data : {},
			dataType: 'json',
			success : function(data, status, jqXHR) {
				getReportData(data);
				
			  jQuery("#list").jqGrid({
 			   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
			         datatype: "local",      //json형태로 데이터 받음.  
			         height: "auto",
			         caption: "일별 가동율 현황",
			         footerrow:false,
			         grouping:true, //그룹화 하기위한 옵션
			         autowidth:true,
			         groupingView : {
			             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
			             groupSummary : [false], //소계를 보인다.
			             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
			             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
			         },
			         
			         colNames:['사업부','구분','목표가동율', '전월가동율','금월누적가동율', '1일', '2일','3일', '4일', '5일', '6일', '7일'	, '8일'	, '9일'	, '10일', '11일', '12일', '13일', '14일', '15일', '16일', '17일', '18일', '19일', '20일', '21일', '22일', '23일', '24일', '25일', '26일', '27일', '28일', '29일', '30일', '31일'],
			         colModel:[                  
			             {name:'DIVISION', index:'DIVISION', align: 'center',  sortable:false },
			             {name:'GUBUN', index:'GUBUN',  sortable:false},
			             {name:'TARGETOR', index:'TARGETOR', summaryType: 'sum',  sortable:false},
			             {name:'LASTMONTHAVG', index:'LASTMONTHAVG', summaryType: 'sum',  sortable:false},
			             {name:'MONTHAVG', index:'MONTHAVG', summaryType: 'sum',  sortable:false},
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
			             {name:'C13', index:'C13', summaryType: 'sum',  sortable:false},
			             {name:'C14', index:'C14', summaryType: 'sum',  sortable:false},
			             {name:'C15', index:'C15', summaryType: 'sum',  sortable:false},
			             {name:'C16', index:'C16', summaryType: 'sum',  sortable:false},
			             {name:'C17', index:'C17', summaryType: 'sum',  sortable:false},
			             {name:'C18', index:'C18', summaryType: 'sum',  sortable:false},
			             {name:'C19', index:'C19', summaryType: 'sum',  sortable:false},
			             {name:'C20', index:'C20', summaryType: 'sum',  sortable:false},
			             {name:'C21', index:'C21', summaryType: 'sum',  sortable:false},
			             {name:'C22', index:'C22', summaryType: 'sum',  sortable:false},
			             {name:'C23', index:'C23', summaryType: 'sum',  sortable:false},
			             {name:'C24', index:'C24', summaryType: 'sum',  sortable:false},
			             {name:'C25', index:'C25', summaryType: 'sum',  sortable:false},
			             {name:'C26', index:'C26', summaryType: 'sum',  sortable:false},
			             {name:'C27', index:'C27', summaryType: 'sum',  sortable:false},
			             {name:'C28', index:'C28', summaryType: 'sum',  sortable:false},
			             {name:'C29', index:'C29', summaryType: 'sum',  sortable:false},
			             {name:'C30', index:'C30', summaryType: 'sum',  sortable:false},
			             {name:'C31', index:'C31', summaryType: 'sum',  sortable:false}
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
				
 				for(var i=0; i<chartData.length; i++){
 					$('.js_work_report_view_page').append('<div id="chart_target' + (i+1) + '"></div>');
					smartChart.loadWithData(chartData[i], "line", false, "chart_target"+(i+1), chart2FieldName, "line");
 				}
 
 		},
		error : function(xhr, ajaxOptions, thrownError){
			
		}
		});
		});
 });
 
 
 	$(function() { 
	   $('#sel_month').change(function() {
		   $.ajax({
				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $("#sel_year").val() + $(this).val(),
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
						smartChart.loadWithData(chartData[i], "line", false, "chart_target"+(i+1), chart2FieldName, "line");
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
				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $(this).val() + $("#sel_month").val(),
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
						smartChart.loadWithData(chartData[i], "line", false, "chart_target"+(i+1), chart2FieldName, "line");
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
<br/><br/>
<div class="js_work_report_view_page">
</div>
</body>
</html>