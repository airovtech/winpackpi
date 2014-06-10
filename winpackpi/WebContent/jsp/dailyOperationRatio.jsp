<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<jsp:include page="commons.jsp"/>

<script type="text/javascript">

	var method = 'getDailyOperationRatio';
	
	var reportData = null;
	var chartData = null;
	var chart2FieldNames = ["목표가동율"];
	
	var getReportData = function(reportData){
		
		chartData = new Array();
		
		console.log('reportData=', reportData);
		
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
							가동율: parseInt(reportData[i]["C" + String("0" + (j+1)).slice(-2)]),
							목표가동율: parseInt(reportData[i].TARGETOR)
						});
				}			
			}
			
			chartData.push({
					title : reportData[i].GUBUN,
					values : chartValues,
					xFieldName : "일별",
					yValueName : "가동율",
					groupNames : ["가동율"]
			});
		}
	};

	var loadChart = function(data) {
		getReportData(data);
		console.log(chartData);
		Ext.onReady(function () {
				for(var i=0; i<chartData.length; i++){
					$('.js_work_report_view_page').append('<div id="chart_target' + (i+1) + '"></div>');
						smartChart.loadWithData(chartData[i], "line", false, "chart_target"+(i+1), chart2FieldNames, "line");
				}
		});
	};
	
	var loadGrid = function() {
		jQuery("#list").jqGrid({
	   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
	         datatype: "json",      //json형태로 데이터 받음.  
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
				$("#list").setGridWidth($('.js_work_report_view_page').width());					        	 
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
	};
	
	$(document).ready( function() {
		selectMenuItem('dailyOperationRatio');
		loadGrid();		 
	});
	
	var reloadGrid = function(){
		 $("#list").setGridParam(
	   	 			{
	   	 				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $('#sel_month').val() ,
	   	 			}).trigger("reloadGrid");
		if(!isEmpty($('.js_work_report_view_page'))){
			swReportResizing = true;
			setTimeout(function(){
				$("#list").setGridWidth($('.js_work_report_view_page').width());				
	     		loadChart(jQuery("#list").jqGrid('getRowData'));
				swReportResizing = false;
			},1000);
		}
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
<div>
<jsp:include page="./chartMenu.jsp" flush="false"/>
</div>
<div  style="height:10px">
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
</div>
</div>
</body>
</html>
