<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<jsp:include page="commons.jsp"/>

<script type="text/javascript">
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
	         footerrow:false,
	         grouping:true, //그룹화 하기위한 옵션
	         autowidth:true,
	         groupingView : {
	             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
	             groupSummary : [false], //소계를 보인다.
	             groupColumnShow : [false], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
	             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
	         },
	         
	         colNames:['사업부','구분','금월', '일평균','MIT', '일평균','달성률(%)', 'WIP', '1일', '2일','3일', '4일', '5일', '6일'	, '7일'	, '8일'	, '9일'	, '10일', '11일', '12일', '13일', '14일', '15일', '16일', '17일', '18일', '19일', '20일', '21일', '22일', '23일', '24일', '25일', '26일', '27일', '28일', '29일', '30일', '31일'],
	         colModel:[                  
	             {name:'DIVISION', width:'70', index:'DIVISION', align: 'center' },
	             {name:'DEVICEGROUP', width:'70', index:'DEVICEGROUP'},
	             {name:'PLANOFSALES', width:'70', index:'PLANOFSALES', summaryType: 'sum'},
	             {name:'AVGPLANOFDAY', width:'70', index:'AVGPLANOFDAY', summaryType: 'sum'},
	             {name:'TOTALSUM', width:'70', index:'TOTALSUM', summaryType: 'sum'},
	             {name:'AVGOFDAY', width:'70', index:'AVGOFDAY', summaryType: 'sum'},
	             {name:'PERSALES', width:'70', index:'PERSALES', summaryType: 'sum'},
	             {name:'WIP', width:'50', index:'WIP', summaryType: 'sum'},
	             {name:'C01', width:'50', index:'C01', summaryType: 'sum'},
	             {name:'C02', width:'50', index:'C02', summaryType: 'sum'},
	             {name:'C03', width:'50', index:'C03', summaryType: 'sum'},
	             {name:'C04', width:'50', index:'C04', summaryType: 'sum'},
	             {name:'C05', width:'50', index:'C05', summaryType: 'sum'},
	             {name:'C06', width:'50', index:'C06', summaryType: 'sum'},
	             {name:'C07', width:'50', index:'C07', summaryType: 'sum'},
	             {name:'C08', width:'50', index:'C08', summaryType: 'sum'},
	             {name:'C09', width:'50', index:'C09', summaryType: 'sum'},
	             {name:'C10', width:'50', index:'C10', summaryType: 'sum'},
	             {name:'C11', width:'50', index:'C11', summaryType: 'sum'},
	             {name:'C12', width:'50', index:'C12', summaryType: 'sum'},
	             {name:'C13', width:'50', index:'C13', summaryType: 'sum'},
	             {name:'C14', width:'50', index:'C14', summaryType: 'sum'},
	             {name:'C15', width:'50', index:'C15', summaryType: 'sum'},
	             {name:'C16', width:'50', index:'C16', summaryType: 'sum'},
	             {name:'C17', width:'50', index:'C17', summaryType: 'sum'},
	             {name:'C18', width:'50', index:'C18', summaryType: 'sum'},
	             {name:'C19', width:'50', index:'C19', summaryType: 'sum'},
	             {name:'C20', width:'50', index:'C20', summaryType: 'sum'},
	             {name:'C21', width:'50', index:'C21', summaryType: 'sum'},
	             {name:'C22', width:'50', index:'C22', summaryType: 'sum'},
	             {name:'C23', width:'50', index:'C23', summaryType: 'sum'},
	             {name:'C24', width:'50', index:'C24', summaryType: 'sum'},
	             {name:'C25', width:'50', index:'C25', summaryType: 'sum'},
	             {name:'C26', width:'50', index:'C26', summaryType: 'sum'},
	             {name:'C27', width:'50', index:'C27', summaryType: 'sum'},
	             {name:'C28', width:'50', index:'C28', summaryType: 'sum'},
	             {name:'C29', width:'50', index:'C29', summaryType: 'sum'},
	             {name:'C30', width:'50', index:'C30', summaryType: 'sum'},
	             {name:'C31', width:'50', index:'C31', summaryType: 'sum'}
	         ],
	         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
	          gridComplete : function() {
	     		 loadChart(jQuery("#list").jqGrid('getRowData'));//---데이터를 성공적으로 가져오면 실행 됨
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
		  
			jQuery("#list").jqGrid('setGroupHeaders', {
				useColSpanStyle: true, 
				groupHeaders:[
		              {startColumnName: 'PLANOFSALES', numberOfColumns: 2, titleText: '실행계획'},	
		              {startColumnName: 'TOTALSUM', numberOfColumns: 3, titleText: '매출실적'}
		              ]
			});
		 
	 };
	 
 
	 $(document).ready( function() { 
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
	}); 
</script>
 
</head>
<body>
<div>
<jsp:include page="./chartMenu.jsp" flush="false"/>
</div>
<div>
<select id='sel_year' class='selDate'>
	<option value='2012'>2012년</option>
	<option selected value='2013'>2013년</option>
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
<table id="list"></table> 
<br/><br/>
<div class="js_work_report_view_page">
	<div id="chart_target"></div>
</div>
</body>
</html>