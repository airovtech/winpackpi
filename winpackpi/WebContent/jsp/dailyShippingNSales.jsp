<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/themes/redmond/jquery-ui-1.8.2.custom.css" />
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/themes/ui.jqgrid.css" />
<script src="../js/jqgrid/js/jquery.min.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery-ui-1.8.2.custom.min.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery.layout.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>

<script type="text/javascript">
 var method = 'dailyShippingNSales';
 $(document).ready( function() { 
	  jQuery("#list").jqGrid({
	   		 url:'../getKpi.jsp?method=' + method + '&yearMonth=' + $('#sel_year').val() + $('#sel_month').val(),        //데이터를 요청 할 주소...  
	         datatype: "json",      //json형태로 데이터 받음.  
	         height: 400,
	         caption: "당월 생산 매출 현황",
	         footerrow:true,
	         grouping:true, //그룹화 하기위한 옵션
	         autowidth:true,
	         groupingView : {
	             groupField : ['DIVISION'], //그룹화 기준이 되는 컬럼명
	             groupSummary : [true], //소계를 보인다.
	             groupColumnShow : [true], //그룹화된 컬럼을 컬럼안에서 다시 표기한다.
	             groupText : ['<span style="color:blue"><b>{0}</b></span>'] //그룹화된 이름에 <b> 태그를 추가했다.
	         },
	         colNames:['사업부','구분','생산', '매출','생산', '매출','생산', '매출', '기초', '입고', '출하', 'WIP', '출하달성률', '매출', '매출달성율'],
	         colModel:[                  
	             {name:'DIVISION', width:'100', index:'DIVISION', align: 'center' },
	             {name:'DEVICEGROUP',width:'100', index:'DEVICEGROUP'},
	             {name:'SHIPPINGPLAN',width:'100', index:'SHIPPINGPLAN', summaryType: 'sum'},
	             {name:'SALESPLAN',width:'100', index:'SALESPLAN', summaryType: 'sum'},
	             {name:'SHIPPINGFOCPLAN',width:'100', index:'SHIPPINGFOCPLAN', summaryType: 'sum'},
	             {name:'SALESFOCPLAN',width:'100', index:'SALESFOCPLAN', summaryType: 'sum'},
	             {name:'SHIPPINGEXEPLAN',width:'100', index:'SHIPPINGEXEPLAN', summaryType: 'sum'},
	             {name:'SALESEXEPLAN',width:'100', index:'SALESEXEPLAN', summaryType: 'sum'},
	             {name:'BOH',width:'100', index:'boh'},
	             {name:'SUMOFRECEIVING',width:'100', index:'SUMOFRECEIVING', summaryType: 'sum'},
	             {name:'SUMOFSHIPPING',width:'100', index:'SUMOFSHIPPING', summaryType: 'sum'},
	             {name:'WIP',width:'100', index:'wip'},
	             {name:'PERSHIPPING',width:'100', index:'PERSHIPPING'},
	             {name:'SUMOFSALES',width:'100', index:'SUMOFSALES', summaryType: 'sum', formatter: 'currency', formatoptions: { decimalSeparator: '.', thousandsSeparator: ',', decimalPlaces: 0, prefix: '' }},
	             {name:'PERSALES',width:'100', index:'PERSALES'}
	         ],
	         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
	          gridComplete : function() {        //---데이터를 성공적으로 가져오면 실행 됨
            	var sumOfSales = $("#list").jqGrid('getCol', 'SUMOFSALES', false, 'sum');
            	var shippingPlan = $("#list").jqGrid('getCol', 'SHIPPINGPLAN', false, 'sum');
            	var salesPlan = $("#list").jqGrid('getCol', 'SALESPLAN', false, 'sum');
            	var shippingFocPlan = $("#list").jqGrid('getCol', 'SHIPPINGFOCPLAN', false, 'sum');
            	var salesFocPlan = $("#list").jqGrid('getCol', 'SALESFOCPLAN', false, 'sum');
            	var shippingExePlan = $("#list").jqGrid('getCol', 'SHIPPINGEXEPLAN', false, 'sum');
            	var salesExePlan = $("#list").jqGrid('getCol', 'SALESEXEPLAN', false, 'sum');
            	var sumOfReceiving = $("#list").jqGrid('getCol', 'SUMOFRECEIVING', false, 'sum');
            	var sumOfShipping = $("#list").jqGrid('getCol', 'SUMOFSHIPPING', false, 'sum');
            	
             	jQuery("#list").jqGrid('footerData', 'set', { DEVICEGROUP: 'Grand Total', SUMOFSALES: sumOfSales, SHIPPINGPLAN: shippingPlan, SALESPLAN: salesPlan, SHIPPINGFOCPLAN: shippingFocPlan, SALESFOCPLAN: salesFocPlan, SHIPPINGEXEPLAN: shippingExePlan, SALESEXEPLAN: salesExePlan, SUMOFRECEIVING: sumOfReceiving, SUMOFSHIPPING: sumOfShipping});
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
  
	  jQuery("#list").jqGrid('setGroupHeaders', {
		  useColSpanStyle: true, 
		  groupHeaders:[
			{startColumnName: 'SHIPPINGPLAN', numberOfColumns: 2, titleText: 'PLAN(월)'},
			{startColumnName: 'SHIPPINGFOCPLAN', numberOfColumns: 2, titleText: 'FORECAST(월)'},
			{startColumnName: 'SHIPPINGEXEPLAN', numberOfColumns: 2, titleText: '실행계획(월)'},
			{startColumnName: 'BOH', numberOfColumns: 4, titleText: 'Movement'},
			{startColumnName: 'PERSHIPPING', numberOfColumns: 3, titleText: '실적'}
		  ]
		});

		 $(".footrow td").css('background-color', '#E8FFFF');
 });
 
 
 	$(function() { 
	   $('#sel_month').change(function() {
			$("#list").setGridParam(
			{
				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $('#sel_year').val() + $(this).val() ,
			}).trigger("reloadGrid");
	   }); 
	}); 
 	$(function() { 
 	   $('#sel_year').change(function() {
 			$("#list").setGridParam(
 			{
 				url : "../getKpi.jsp?method=" + method + "&yearMonth=" +  $(this).val() + $('#sel_month').val() ,
 			}).trigger("reloadGrid");
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
</body>
</html>