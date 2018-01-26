<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="ru.curs.showcase.util.UserAndSessionDetails"%>
<%@page import="ru.curs.showcase.runtime.UserDataUtils"%>
<%@page import="ru.curs.showcase.runtime.ServerStateFactory"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.InputStream"%>
<%@page import="ru.curs.showcase.util.FileUtils"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="ru.curs.showcase.util.TextUtils"%>
<%@page import="java.io.IOException"%>
<%@page import="ru.curs.showcase.util.exception.SettingsFileType"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="ru.curs.showcase.util.exception.SettingsFileOpenException"%>
<%@page import="ru.curs.showcase.runtime.ConnectionFactory"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="ru.curs.showcase.runtime.SQLServerType"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>
<%@page import="javax.servlet.ServletException"%>
<%@page import="ru.curs.showcase.security.AuthServerUtils"%>	
<%@page import="ru.curs.showcase.app.api.UserInfo"%>
<%@page import="ru.curs.showcase.app.api.BrowserType"%>
	
<html>
<head>
<title>О программе...</title>
<link rel="shortcut icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
<link rel="icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />

	
<%
String sqlVersion = "";

String fileName = String.format("%s/version_%s.sql", UserDataUtils.SCRIPTSDIR, 
		ConnectionFactory.getSQLServerTypeForDefaultUserdata().toString().toLowerCase());

String sql = "";
try {
sql = TextUtils.streamToString(FileUtils.loadClassPathResToStream(fileName));
} catch (IOException e) {
throw new SettingsFileOpenException(e, fileName, SettingsFileType.SQLSCRIPT);
}
if (sql.trim().isEmpty()) {
throw new SettingsFileOpenException(fileName, SettingsFileType.SQLSCRIPT);
}

//Connection conn = ConnectionFactory.getInstance().acquire();
Connection conn = DriverManager.getConnection(UserDataUtils.getGeneralRequiredProp("rdbms.connection.url"),
		UserDataUtils.getGeneralRequiredProp("rdbms.connection.username"),
		UserDataUtils.getGeneralRequiredProp("rdbms.connection.password"));

try {
	PreparedStatement stat = null;
	try {
	stat = conn.prepareStatement(sql);
	boolean hasResult = stat.execute();
	if (hasResult) {
		ResultSet rs = stat.getResultSet();
		if (rs.next()) {
			String fullVersion = rs.getString("Version");
			if (fullVersion != null) {
				sqlVersion = fullVersion.split("\t")[0];
			}
		}
	}
}finally{
	if(stat != null)
		stat.close();
}
} finally {
//ConnectionFactory.getInstance().release(conn);
conn.close();
}
if("".equals(sqlVersion))
	sqlVersion = "не определена";
%>

<%
String dojoVersion = "";		

File file = new File(AppInfoSingleton.getAppInfo().getWebAppPath() + "/js/dojo/package.json");
		if (!file.exists()) {
			dojoVersion = "не определена";
		}

		String data = "";
		try {
			InputStream is = new FileInputStream(file);
			data = TextUtils.streamToString(is);
		} catch (IOException e) {
			dojoVersion = "не определена";
		}
		Pattern pattern = Pattern.compile("\"version\":\"([a-z0-9.]+)\"");
		Matcher matcher = pattern.matcher(data);
		matcher.find();
		if (matcher.groupCount() > 0) {
			dojoVersion = matcher.group(1);
		}
		
		if ("".equals(dojoVersion)) {
			dojoVersion = "не определена";
		}
%>

<% 
String url = null;
try {
	url = SecurityParamsFactory.getLocalAuthServerUrl();
} catch (SettingsFileOpenException e) {
	throw new ServletException(SecurityParamsFactory.APP_PROP_READ_ERROR, e);
}
AuthServerUtils.init(url);

	UserInfo ud =
		AuthServerUtils.getTheAuthServerAlias().isAuthenticated(
				request.getSession().getId());
%>

<%     
		final String br = "<br />";		

		boolean isNativeUser;
// 		if (SecurityContextHolder.getContext().getAuthentication() == null) {
// 			isNativeUser = false;
// 		} else {
// 			isNativeUser = !((UserAndSessionDetails) SecurityContextHolder.getContext()
// 					.getAuthentication().getDetails()).isAuthViaAuthServer();
// 		}
		
		if(AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(
				request.getSession().getId()))
		{
			isNativeUser = false;
		} else {
			isNativeUser = true;
		}
		String fff = isNativeUser ? "внутренним" : "внешним";
		
		boolean getCaseSensivityIDs = Boolean.valueOf(UserDataUtils.getGeneralOptionalProp("id.casesensitive"));
		String caseSensivityIDsSummaryPrefix = getCaseSensivityIDs ? ""	: " не";
		
		String caseSensivityIDsSummary =
				"Идентификаторы" + caseSensivityIDsSummaryPrefix + " чувствительны к регистру.";

			String serverInfo =
					"Версия SQL сервера: "
							+ sqlVersion
							+ br
							+ "Версия JAVA на сервере: "
							+ System.getProperty("java.version")
							+ br
							+ "Версия сервлет контейнера: "
							+ AppInfoSingleton.getAppInfo().getServletContainerVersion()
							+ br
							+ "Версия dojo: "
							+ dojoVersion;
%>
		
		<script type="text/javascript">
			//var userAgent = window.navigator.userAgent.toLowerCase();
			
			function get_browser_info(){
			    var ua=navigator.userAgent,tem,M=ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || []; 
			    if(/trident/i.test(M[1])){
			        tem=/\brv[ :]+(\d+)/g.exec(ua) || []; 
			        return {name:'IE',version:(tem[1]||'')};
			        }   
			    if(M[1]==='Chrome'){
			        tem=ua.match(/\bOPR\/(\d+)/)
			        if(tem!=null)   {return {name:'Opera', version:tem[1]};}
			        }   
			    M=M[2]? [M[1], M[2]]: [navigator.appName, navigator.appVersion, '-?'];
			    if((tem=ua.match(/version\/(\d+)/i))!=null) {M.splice(1,1,tem[1]);}
			    return {
			      name: M[0],
			      version: M[1]
			    };
			 }
			
		</script>
		<script src="solutions/<%=UserDataUtils.getUserDataId()%>/js/solution.js"></script>
<%
String appVersion = UserDataUtils.getAppVersion();
%>

<% 
String userAgent = request.getHeader("User-Agent"); 
//request.getParameter("userAgent");

BrowserType browserType = null;
String browserVersion = null;
String browserTypeString = null;

if (userAgent != null) {
	browserVersion = ru.curs.showcase.app.api.BrowserType.detectVersion(userAgent);
	browserType = ru.curs.showcase.app.api.BrowserType.detect(userAgent);

	browserTypeString = (browserType != null) ? browserType.getName() : null;

}
%>

</head>
<body>
	<table border="0">
	<tr>
	<td rowspan="2">
	<p><img src='resources/internal/logo.gif' alt='КУРС' /></p>
	<img src='resources/internal/favicon32.png' alt='' />&nbsp;Showcase&nbsp;
					<%= appVersion +
					 br
					+ br
					%>

					Copyright ООО 'КУРС-ИТ', 1998-2016 
					<%=br%>
					Тел/факс: +7(495)640-2772
					<%=br%>
					E-mail: <a href='mailto://info@mail.ru'>info@curs.ru</a>
					<br/> <a href='http://www.curs.ru' target='_blank'>http://www.curs.ru</a>
					<%=br%>
					<%=br
					+ serverInfo
					+br%>

					Тип браузера: 
					<%=((browserTypeString != null && browserType != ru.curs.showcase.app.api.BrowserType.UNDEFINED) ? browserTypeString
							: "не удалось определить")
					+ br%>

					
					<script type="text/javascript">
// 					var browser=get_browser_info();
// 					var browserType = browser.name; 
// 					var browserTypeString = (browserType != null) ? browserType : null;
// 					browserTypeString = (browserType != null) ? browserType : null;
// 					((browserTypeString != null) ? browserTypeString
// 							: "не удалось определить");
// 					document.write(browserTypeString);
					</script>
					
					Версия браузера: 
					<%=((browserVersion != null) ? browserVersion : "не удалось определить")
					+ br%>
					
					<script type="text/javascript">
// 					var browser1=get_browser_info();
// 					var browserVersion = browser1.version;
// 					((browserVersion != null) ? browserVersion : "не удалось определить");
// 					document.write(browserVersion);
					</script>
					
					Текущий пользователь '
					<%
						String login = "";
					
					if(isNativeUser)
					{
						login = "master";
					}
					
					if(ud != null)
					{
						login = ud.getCaption();
					}
// 					(SecurityContextHolder.getContext().getAuthentication() != null) ?
// 					((UserAndSessionDetails) SecurityContextHolder.getContext()
// 							.getAuthentication().getDetails()).getUserInfo()
// 							.getCaption() : "не залогинен" 
						%>	
					<%=!"".equals(login) ? login : "не залогинен"
							%>
					'
					(
					
					<%
						String fullName = "";
					
					if(isNativeUser)
					{
						fullName = "master";
					}
					
					if(ud != null)
					{
						fullName = ud.getFullName();
					}
// 					(SecurityContextHolder.getContext().getAuthentication() != null) ? 
// 							((UserAndSessionDetails) SecurityContextHolder.getContext()
// 									.getAuthentication().getDetails()).getUserInfo()
// 									.getFullName() : "не залогинен"							 
					
					%>
					<%=!"".equals(fullName) ? fullName : "не залогинен"
							%>
					)
					является 
					<%= fff
					+ br
					+ caseSensivityIDsSummary
					+ br%>
						
					<script type="text/javascript">
						var output = addAdditionalAboutInfo();
							document.write(output);	
					</script>
					<%= br + br
					%>
					
					
	</td>
	<td></td>
	</tr>
	<tr>
	<td valign="bottom" align="center">
	<input type="button" value="OK" onClick="window.close()" />
	</td>
	</tr>
</table>
</body>
</html>