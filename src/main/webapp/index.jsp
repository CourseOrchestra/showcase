<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>
<%@page import="ru.curs.showcase.runtime.UserDataUtils"%>      
<%@page import="ru.curs.showcase.runtime.ExternalClientLibrariesUtils"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
<%@page import="ru.curs.showcase.app.server.AppAndSessionEventsListener"%>  
<%@page import="java.io.File"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%
String webAppName = request.getContextPath();
if (webAppName.contains("/")) {
	webAppName = webAppName.replace("/", "");
}
request.getSession().setAttribute("queryString" + request.getServerPort() + webAppName, request.getQueryString());
Cookie cookie = new Cookie("queryString" + request.getServerPort() + webAppName, request.getQueryString());
cookie.setPath(AppAndSessionEventsListener.getContextPath());
response.addCookie(cookie);

	Cookie[] cookies = request.getCookies();
	if (cookies != null && cookies.length > 0) {
		for (Cookie cookie1 : cookies) {
			if (cookie1.getName().equals("remembermecookie")) {
				request.getSession(false).setAttribute("remembermecookie", cookie1);
			}
		}
	}

	String host = request.getRemoteHost();
	String query = request.getQueryString();
	
	String userdataId = null;
	if(request.getParameter("userdata") != null)
		userdataId = request.getParameter("userdata");
	if(request.getParameter("perspective") != null)
		userdataId = request.getParameter("perspective");
	if(request.getParameter("userdata") != null && request.getParameter("perspective") != null)
		userdataId = request.getParameter("perspective");
	
	if(userdataId != null){
		AppInfoSingleton.getAppInfo().getHostUserdataMap().put(host, query);
	}
	if (userdataId == null) {
		userdataId = "default";
		if(AppInfoSingleton.getAppInfo().getHostUserdataMap().get(host) == null) {
			if(query != null) {
				AppInfoSingleton.getAppInfo().getHostUserdataMap().put(host, query + "&userdata=" + userdataId);
			}
			if(query == null) {
				AppInfoSingleton.getAppInfo().getHostUserdataMap().put(host, "userdata=" + userdataId);
			}
		}
	} 
	
	String title = "Showcase index page";
	if (UserDataUtils.getOptionalProp("index.title", userdataId) != null) {
		title = UserDataUtils.getOptionalProp("index.title", userdataId);
		
	}
%>

	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!--
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
-->	
	<title><%=title%></title>
	
<%
if(request.getParameter("userdata") == null && AppInfoSingleton.getAppInfo().getHostUserdataMap().get(host).contains("userdata=")) {
%>
<script type="text/javascript">
var host = window.location.host;
var path = window.location.pathname;
var protocol = window.location.protocol;
<%-- window.location.replace(protocol + "//" + host + path + "?<%=AppInfoSingleton.getAppInfo().getHostUserdataMap().get(host)%>"); --%>
</script>
<%}%>

    <link rel="stylesheet" href="xsltforms/xsltforms.css" type="text/css" />	
	<link rel="shortcut icon" href="solutions/<%=userdataId%>/resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="solutions/<%=userdataId%>/resources/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" language="javascript" src="secured/secured.nocache.js"></script>
    
    <link rel="stylesheet" href="js/dijit/themes/claro/claro.css"/>
    <link rel="stylesheet" href="js/dojox/calendar/themes/claro/Calendar.css"/>
    <link rel="stylesheet" href="js/dojox/calendar/themes/claro/MonthColumnView.css"/>
    
    <link rel="stylesheet" href="js/dgrid/css/dgrid.css"/>
    <link rel="stylesheet" href="js/dgrid/css/skins/claro.css"/>
    
    <script language="javascript" src="js/Gettext.js"></script>
     
     <%if((new File(AppInfoSingleton.getAppInfo().getSolutionsDirRoot() + File.separator +
    		 userdataId + File.separator + "resources" + File.separator +
    		 UserDataUtils.getFinalPlatformPoFile(userdataId))).exists()){ %>
     <link rel="gettext" 
     href="solutions/<%=userdataId%>/resources/<%=UserDataUtils.getFinalPlatformPoFile(userdataId)%>" 
    		 type="application/x-po"/>
    <%}else{%>
    <link rel="gettext" href="resources/platform.po" type="application/x-po"/>
    <%}%>
    
      <%=//ExternalClientLibrariesUtils.addExternalCSSByStaticMetod(request.getParameter("userdata"))
    		  ExternalClientLibrariesUtils.addExternalCSSByStaticMetod(userdataId)
  	  %>  
    
    <script>
        var dojoConfig = {
//        	deps: [ "dojox/mobile", "dojox/mobile/parser", "dojox/mobile/compat" ],
// нужно для поддержки мобильности в dgrid'ах,  
// закомменчено поскольку не работали клики в аккордеоне 

            parseOnLoad: false,
            isDebug: false,            
            djeoEngine: 'djeo',
            geKey: '',
            ymapsKey: '<%=UserDataUtils.getGeoMapKey("ymapsKey", request.getServerName())%>',            
            paths: {'course': '../course', 'djeo': '../djeo', 'courseApp': '../..'},
            gfxRenderer: 'svg,silverlight,vml'
        };
     </script>   
     <script src="js/dojo/dojo.js"></script>   
	 <script src="js/jscolor/jscolor.js"></script>     
	
<!-- Google Earth not need keys now! -->
<!-- if you plan to use Yandex Maps on non localhost server - copy your own key; apply for a key at http://api.yandex.ru/maps/form.xml	-->
<!-- for store keys use files in userdata root!  -->

    <script src="js/internalShowcase.js"></script>
    <script src="solutions/<%=userdataId%>/js/solution.js"></script>
    <script>
     var appContextPath="<%=request.getContextPath()%>";
    </script>
    
  <%=//ExternalClientLibrariesUtils.addExternalLinksByStaticMetod(request.getParameter("userdata"))
		  ExternalClientLibrariesUtils.addExternalLinksByStaticMetod(userdataId)
  %>  
	 
</head>
<body class="claro">

<%
String authGifSrc = String.format("%s/authentication.gif?sesid=%s",
		SecurityParamsFactory.getAuthServerUrl(), request.getSession() 
				.getId());
 
authGifSrc = SecurityParamsFactory.correctAuthGifSrcRequestInCaseOfInaccessibility(authGifSrc);
%>


<!--    не удалять!-->
<!--    <div style="float:right;" ><a href="<c:url value="/logout"/>">Выйти</a> </div>-->
<!--	<a href="<c:url value="/j_spring_security_logout" />">Выйти</a>-->
<!--	<br/>-->

<script type="text/javascript" src="xsltforms/xsltforms.js">
	/* */
</script>


<div id="target"></div>
<div id="mainXForm" style="display: none;"></div>


     <!--[if lte IE 7]>
     <p style="margin: 0.2em 0; background: #ccc; color: #000; padding: 0.2em 0;">Ваша текущая версия Internet explorer устарела. Приложение будет работать некорректно. <a href="http://browsehappy.com/">Обновите свой браузер!</a></p>
     <![endif]-->

	<div id="showcaseHeaderContainer"></div>
	<div id="showcaseAppContainer"></div>
	<div id="showcaseBottomContainer"></div>
	
<%if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") != null && "true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim())) {%><img src="<%=authGifSrc%>" alt=" " id="authenticationImage" style="visibility:hidden; width: 0px; height: 0px" /><%}%>
    
</body>
</html>