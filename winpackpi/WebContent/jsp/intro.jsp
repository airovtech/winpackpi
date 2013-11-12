<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/themes/redmond/jquery-ui-1.8.2.custom.css" />
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/themes/ui.jqgrid.css" />
<script src="../js/jqgrid/js/jquery.min.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery-ui-1.8.1.custom.min.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery.layout.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>

<script type="text/javascript">
 $(document).ready( function() { 
  
	  jQuery("#list").jqGrid({
	   url:'../getData.jsp',        //데이터를 요청 할 주소...  
	         datatype: "json",      //json형태로 데이터 받음. 그러니 memberlist.do는 json타입의 데이터를 리턴해야 한다. 
	         height: 200,
	         colNames:['id','name','password', 'lang', 'address'],
	         colModel:[                  
	             {name:'mbid',index:'mbid', align: 'center' },
	             {name:'mbname',index:'mbname'},
	             {name:'mbpswd',index:'mbpswd'},
	             {name:'locale',index:'locale'},
	             {name:'address',index:'address'}
	         ],
	         //객체에 담긴 이름값과 name이 같은 지 확인 잘하길... 나는 대소문자 구별 때문에 행은 늘어나는데 데이터가 나타나지 않아서 한참 헤맴...
	          //gridComplete : function() {        //---데이터를 성공적으로 가져오면 실행 됨
	          //	alert('success');
	          //},
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
			{startColumnName: 'mbname', numberOfColumns: 2, titleText: '<em>Price</em>'}
		  ]
		});
	  
 });
</script>
 
</head>
<body>
<table id="list"></table> 
</body>
</html>