<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>   

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!--
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
-->	
	<title>Пользовательская сессия Showcase закрыта</title>
	<link rel="shortcut icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
	


	
	
	
</head>
<body>

<b style="color: red;">Пользовательская сессия Showcase была разорвана по причине разрыва связи с сервером либо таймаут сессии истек.</b>
<br>
Пожалуйста, войдите в приложение еще раз.

<br>

<button onclick="top.location.href='logout';">Войти в Showcase</button>


</body>
</html>
