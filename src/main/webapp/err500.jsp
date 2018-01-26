<%@page isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@page import="javax.servlet.ServletException"%>
<%@page import="javax.servlet.http.HttpServlet"%>
<%@page import="javax.servlet.http.HttpServletRequest"%>
<%@page import="javax.servlet.http.HttpServletResponse"%>
<%@page import="java.lang.Throwable"%>
    
<html>
<head><title>Error</title></head>
<body>
<h2>Internal server error</h2>

<strong>Error code:</strong>
<%Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");%>
<%=statusCode%>
<br><br>

<strong>Error type:</strong>
<%Throwable throwable = (Throwable) request
                .getAttribute("javax.servlet.error.exception");%>
<%=throwable.getClass().getName()%>
<br><br>

<strong>Error message:</strong>
<%=throwable.getMessage()%>
</body>
</html>

