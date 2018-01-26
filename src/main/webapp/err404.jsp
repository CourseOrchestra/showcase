<%@page isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<html>
<head><title>Error</title></head>
<body>
<strong>Error code:</strong>
<%Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");%>
<%=statusCode%>
<br><br>

<strong>Error message:</strong>
Requested resource: <%=request.getAttribute("javax.servlet.forward.request_uri")%> not found
</body>
</html>

