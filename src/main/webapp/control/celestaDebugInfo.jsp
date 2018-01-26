<?xml version="1.0" encoding="UTF-8" ?>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="ru.curs.celesta.Celesta"%>
<%@page import="ru.curs.celesta.CallContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- <meta http-equiv="refresh" content="10;url=celestaDebugInfo.jsp" /> -->
<title>Таблица процессов Celesta</title>
<link rel="shortcut icon" href="solutions/default/resources/favicon.ico"
	type="image/x-icon" />
<link rel="icon" href="solutions/default/resources/favicon.ico"
	type="image/x-icon" />
<style>

<!--
table {
 	table-layout: fixed; 
 	width: 90%;
	border-width: 2px;
}

form {
	text-align: center;
}
div {
	text-align: center;	
}
-->

</style>

<script src="../js/dojo/dojo.js"></script>
<script type="text/javascript">
		function refresh() {
			//window.setInterval('window.location.reload()',100);
			var table = document.getElementById('table');			
 			document.getElementById("table").innerHTML = "";
 			document.getElementById("table").style.display = "";
			
			document.getElementById("table_div").innerHTML = "";
			document.getElementById("table_title").style.display = "";

			dojo.xhrGet({
			sync: true,
			url: "<%=request.getContextPath()%>/control/debugInfo?sesid=<%=request.getSession().getId()%>",
			handleAs: "javascript",
			load: function(data) {
				data;
			},
			error: function(error) {
				alert(error);
			}
			});
			
		}
	</script>

</head>

<body>

	<h3 align="center">Трассировка процедур Celesta</h3>
	<table>
		<tr>
			<td align="right">
				<form target="fake" method="post" action="debugInfo"> 
					<input type="hidden" name="trassert" value="on" /><input
						type="submit" value="Включить трассировку" onclick="window.location.reload();"/>
		 		</form>
			</td>
<!-- 		</tr> -->
<!-- 		<tr> -->
			<td align="left">
				<form target="fake" method="post" action="debugInfo"> 
					<input type="hidden" name="trassert" value="off" />
					<input type="submit" value="Выключить трассировку" onclick="window.location.reload();"/>
		 		</form>
			</td>
		</tr>
	</table>
	
	<h4 align="center">Состояние трассировки</h4>
		<p align="center"><%=Celesta.getInstance().isProfilemode() ? "Трассировка включена" : 
			"Трассировка выключена"%></p>
	
	<h3 align="center">Получение таблицы процессов</h3>
<!-- 	<table> -->
<!-- 		<tr> -->
<!-- 			<td width="30%">-->
				<form method="get" onsubmit="return false;"> 
					<input
						type="submit" value="Получить таблицу процессов"
						onclick="refresh();" />
		 		</form>
		<!--	</td> -->
<!-- 		</tr>		 -->
<!-- 	</table> -->
	
	<div id = "table_title" style="display:none"><h4>Таблица процессов</h4></div>
	<div id = "table_div" style="display:none"></div>
	<table id = "table" border = "1" style="display:none"></table>
	
	<iframe name="fake"
		style="position: absolute; width: 0; height: 0; border: 0"
		src="javascript:''" />
</body>
</html>