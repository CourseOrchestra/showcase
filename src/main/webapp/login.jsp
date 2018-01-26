<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>   
<%@page import="ru.curs.showcase.runtime.UserDataUtils"%> 
<%@page import="ru.curs.showcase.security.esia.EsiaSettings"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
<%@page import="ru.curs.showcase.app.server.AppAndSessionEventsListener"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<%
	String title = "Авторизация в КУРС: Showcase";
	if (UserDataUtils.getGeneralOptionalProp("login.title") != null) {
		title = UserDataUtils.getGeneralOptionalProp("login.title");
	}

	String webAppName = request.getContextPath();
	if (webAppName.contains("/")) {
		webAppName = webAppName.replace("/", "");
	}
	
	if(request.getParameter("error") == null && request.getParameter("exited") == null)
	{
		Cookie cookie = new Cookie("queryString" + request.getServerPort() + webAppName, "");
		cookie.setPath(AppAndSessionEventsListener.getContextPath());
		response.addCookie(cookie);
	}
%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!--
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
-->	
	<title><%=title%></title>
	<link rel="shortcut icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
	
    <script type="text/javascript">
    	var djConfig = {
            parseOnLoad: false,
            isDebug: false
        };
    </script>  
	<script src="js/dojo/dojo.js"></script>
	
<script type="text/javascript">

	function checkAuthenticationImageSize() {
		var w;
		<%if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") != null && "true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim())) {%>
		var pic = document.getElementById("authenticationImage");
		w = pic.naturalWidth;<%}%>  
		<%if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") == null || !("true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim()))) {%>
		w = 1000;
		<%}%>
		
		if (w == 178) {	
			if (document.getElementById('helloMessage')) 
		 		dojo.attr("helloMessage", "innerHTML", "");
			if (document.getElementById('informationMessage')) 
				dojo.attr("informationMessage", "innerHTML", "Идет проверка подлинности пользователя...<br>Пожалуйста подождите...");
			id = setTimeout("checkIsAuthenticatedSession()",1000);
		}
		else {			
		    document.formlogin.style.display = "";
		    document.getElementById("j_username").focus();
		}
	}
	
	function checkIsAuthenticatedSession() {
		dojo.xhrGet({
			sync: true,
			url: "<%=request.getContextPath()%>/auth/isAuthenticatedServlet?sesid=<%=request.getSession().getId()%>",
			handleAs: "json",
			preventCache: true,
			timeout: 10000,
			load: function(data) {
				document.getElementsByName("j_username")[0].value = data.login;
				document.getElementsByName("j_password")[0].value = data.pwd;
				document.formlogin.submit();
			},
			error: function(error) {
				alert("Ошибка соединения с сервером аутентификации.");
			}
		});
	}
   

   
</script>	
	
	
	
</head>
<body onLoad="checkAuthenticationImageSize()">

     <!--[if lte IE 7]>
     <p style="margin: 0.2em 0; background: #ccc; color: #000; padding: 0.2em 0;">Ваша текущая версия Internet explorer устарела. Приложение будет работать некорректно. <a href="http://browsehappy.com/">Обновите свой браузер!</a></p>
     <![endif]-->

<%  
	if(UserDataUtils.getGeneralOptionalProp("security.ssl.keystore.path") != null){
		System.setProperty("javax.net.ssl.trustStore", 
				UserDataUtils.getGeneralOptionalProp("security.ssl.keystore.path").trim());
	}

String authGifSrc = String.format("%s/authentication.gif?sesid=%s",
		SecurityParamsFactory.getAuthServerUrl(), request.getSession() 
				.getId());

authGifSrc = SecurityParamsFactory.correctAuthGifSrcRequestInCaseOfInaccessibility(authGifSrc);
%>
<c:if test="${not empty param.error}">
<div id="accessDenied">
  <font color="red">
  <b>Ошибка!</b>
  <br/>
  <%if(((Exception)request.getSession().
		  getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).getMessage().contains("Bad credentials")) {%>
  Имя пользователя и/или пароль неверны!<br/>
  Отказано в доступе. <br/>
  <%}%>
  Ответ сервера: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} 
  <br/>
  </font>
  </div>
</c:if>
<span id="helloMessage" style="font-size: 27px;color:green">Авторизация в КУРС: Showcase</span>
<span id="informationMessage" style="font-family: sans-serif;"></span>
<form name="formlogin" method="POST" action="<c:url value="/j_spring_security_check" />" style="display:none">
<table>


<tr>			    <td align='right'>Домен</td>

<%=SecurityParamsFactory.getHTMLTextForPrividerGroupsComboBoxSecector(authGifSrc)%>

</tr>
<!--   <tr> -->
<!--     <td align="rigfht">Домен</td> -->
<!--     <td> -->
<!--      <select id="j_domain" type="text" name="j_domain"> -->
<!--       <option value="Группа1">Группа1</option> -->
<!--       <option selected  value="Группа2">Группа2</option> -->
<!--      </select> -->
<!--     </td> -->
<!--   </tr> -->

  <!--   test -->
  
  
  
  
  <tr>
    <td align="right">Имя пользователя</td>
    <td><input id="j_username" type="text" name="j_username" /></td>
    <td></td>
  </tr>
  <tr>
    <td align="right">Пароль</td>
    <td><input  id="j_password" type="password" name="j_password" autocomplete = "off"/></td>
    <td></td>
  </tr>
  <tr>
    <td align="right">Запомнить меня</td>
    <td><input type="checkbox" name="_spring_security_remember_me" /></td>
    <td></td>
  </tr>

  
  <!--    
  -->
  
  <tr>
    <td>
     <%if (EsiaSettings.isEsiaEnable()){%><a href="esia?auth=esia">Вход с помощью </br>учетной записи  </br>портала госуслуг</a><%}%>    
    </td>
  </tr>
  
  
  <tr>
    <td colspan="2" align="right">
      <input type="submit" value="Войти" />
      <input type="reset" value="Сбросить" />
    </td>
    <td><%if (UserDataUtils.getGeneralOauth2Properties() != null) {%><a href="oauth?auth=websphere">WebSphere авторизация</a><%}%> <%if (UserDataUtils.getGeneralSpnegoProperties() != null) {%><a href="spnego">Spnego авторизация</a><%}%></td>
  </tr>
  <tr>
  <td colspan="3">
  <a href="forall/state" target="_blank">О программе</a>
  </td>
  </tr>
</table>
</form>

<br/>
<%if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") != null && "true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim())) {%><img src="<%=authGifSrc%>" alt=" " id="authenticationImage" style="visibility:hidden" /><%}%>

</body>
</html>
