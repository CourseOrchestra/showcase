<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="ru.curs.showcase.runtime.LoggingEventDecorator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.Collection"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Последние записи лога Showcase</title>
<link rel="shortcut icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
<link rel="icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
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

	Collection<LoggingEventDecorator> lastLogEvents = AppInfoSingleton.getAppInfo().getLastLogEvents(request);
	int number = 0;	
%>
<table width="90%">
<c:forEach items="<%=lastLogEvents%>" var="event">
<% 	number++;
%>
<tr>
<td width="18%" onclick="document.getElementById('row<%=number+1%>').scrollIntoView()">
level=${event.getLevel()} 
</td>
<td width="82%" rowspan="9">
<div id="row<%=number%>">
${event.getMessage()} 
</div>
</td>
</tr>
<tr>
<td  width="18%">
time=${event.getTime()} 
</td>
</tr>
<tr>
<td  width="18%" >
userName=${event.getUserName()} 
</td>
</tr>
<tr>
<td  width="18%" >
userdata=${event.getUserdata()} 
</td>
</tr>
<tr>
<td  width="18%" >
requestId=${event.getRequestId()} 
</td>
</tr>
<tr>
<td  width="18%" >
commandName=<br/>${event.getCommandName()} 
</td>
</tr>
<tr>
<td valign="top" width="18%">
process=${event.getProcess()} 
</td>
</tr>
<tr>
<td valign="top" width="18%">
direction=${event.getDirection()} 
</td>
</tr>
<tr>
<td valign="top" width="18%" onclick="document.getElementById('row<%=number-1%>').scrollIntoView()">
params:<br/>${event.getParams()} 
</td>
</tr>
</c:forEach>
</table>

</body>
</html>