<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="../css/jquery.jqChart.css" />
<script src="../js/jquery-1.9.0.min.js" type="text/javascript"></script>
<script src="../js/jquery.jqChart.min.js" type="text/javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
<script type="text/javascript">

	var dataValue = [];

	$(document).ready(function () {
		$.ajax({
			url : "./getJson.jsp",
			contentType : 'application/json',
			type : 'POST',
			dataType : "json",
			data : "",
			success : function(data, status, jqXHR) {
				alert('success');
				var item = data.rows;
				for (var i = 0; i < item.length; i++) {
					dataValue.push(["a", item[i].a]);
					dataValue.push(["b", item[i].b]);
					dataValue.push(["c", item[i].c]);
					dataValue.push(["d", item[i].d]);
					dataValue.push(["e", item[i].e]);
				}
				  $('#jqChart').jqChart({
				        title: { text: 'Chart Title' },
				        series: [
				                    {
										title: 'jjang',
				                        type: 'column',
//				                        data: [['a', 46], ['b', 35], ['c', 68], ['d', 30], ['e', 27], ['f', 85], ['d', 43], ['h', 29]]
				                        data: dataValue
				                    },
				                    {
										title: 'jjang gu',
				                        type: 'line',
//				                        data: [['a', 69], ['b', 57], ['c', 86], ['d', 23], ['e', 70], ['f', 60], ['d', 88], ['h', 22]]
				                        data: dataValue
				                    }
				                ],
				    	});
				
				
				
			},
			error : function(xhr, ajaxOptions, thrownError) {
				alert("통신실패");
			}
		});
		
	  
	});

</script>

</head>
<body>
<div id="jqChart" style="width: 500px; height: 300px;" />
</body>
</html>