<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.SortedSet"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Запущенные хранимые процедуры</title>
<style>
<!--
th, td
 {
 	border: 1px solid black;
 	white-space: pre-wrap;
 }
 table
 {
	table-layout:fixed; 
 }
-->
</style>
</head>
<body>
<%
	SortedSet<String> procs = AppInfoSingleton.getAppInfo().getExecutedProc();
	int number = 0;	
%>
<table width="90%">
<c:forEach items="<%=procs%>" var="proc">
<% 	number++;
%>
<tr>
<td width="18%" onclick="document.getElementById('row<%=number+1%>').scrollIntoView()">
<%=number%>
</td>
<td>
${proc} 
</td>
</tr>
</c:forEach>
</table>
</body>
</html>