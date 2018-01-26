<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 4.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

    <%@page import="ru.curs.showcase.security.SecurityParamsFactory" %>
        <%@page import="ru.curs.showcase.runtime.UserDataUtils" %>

            <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
                <html>

                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                    <title>ExoAtlet.ru — экзоскелет для реабилитации</title>
                    <script type="text/javascript">
                        var djConfig = {
                            parseOnLoad: false,
                            isDebug: false
                        };
                    </script>
                    <script language="javascript">
                        function doPopup(popupPath, mode) {
                            if (mode == 'remind') {
                                window.open(popupPath, 'name',
                                    'width=400,height=170,scrollbars=YES');
                            }
                            if (mode == 'registr') {
                                window.open(popupPath, 'name',
                                    'width=400,height=270,scrollbars=YES');
                            }
                        }
                    </script>
                    <script src="js/dojo/dojo.js"></script>

                    <script type="text/javascript">
                        function checkAuthenticationImageSize() {
                            var w; <%
                            if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") != null && "true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim())) { %>
                                var pic = document.getElementById("authenticationImage");
                                w = pic.offsetWidth; <%
                            } %>
                            <%
                            if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") == null || !("true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim()))) { %>
                                w = 1000; <%
                            } %>

                            if (w == 178) {
                                if (document.getElementById('helloMessage'))
                                    dojo.attr("helloMessage", "innerHTML", "");
                                if (document.getElementById('informationMessage'))
                                    dojo.attr("informationMessage", "innerHTML", "Идет проверка подлинности пользователя...<br>Пожалуйста подождите...");
                                id = setTimeout("checkIsAuthenticatedSession()", 1000);
                            } else {
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

                <body bgcolor="#F2F2F2">
                    <form name="formlogin" method="POST" action="<c:url value='/j_spring_security_check' />">
                        <table width="642" height="100%" cellpadding="0" cellspacing="1" border="0" class="zakladki" bgcolor="#CCCCCC" align="center">
                            <tr>
                                <td bgcolor="#FFFFFF" align="center">
                                    <br>
                                    <table width="620" cellpadding="0" cellspacing="0" border="0" class="zakladki">
                                        <tr>
                                            <td width="10"></td>
                                            <td width="230">
                                                <a href="http://www.exoatlet.ru"><img src="http://static.wixstatic.com/media/f313bb_b75c1038aa5a4f28a5636e1a83dfb5f9.png_850" height="85" alt="ExoAtlet" border="0" hspace="10" IMAGE></a>
                                            </td>

                                            <td width="220" align="center"></td>
                                            <td width="160" align="right">
                                                <a href="http://rsmu.ru"><img src="http://static.wixstatic.com/media/f313bb_40a97b17d72246f482d2031f925c8ad8.png_650" height="55" alt="ExoAtlet" border="0" hspace="10" IMAGE></a>
                                                <br>
                                            </td>
                                        </tr>
                                    </table>
                                    <table width="600"  cellpadding="0" cellspacing="0" border="0" align="center">
                                        <tr>
                                            <td width="40" rowspan="5"></td>
                                            <td><font size="5" color="#00b0ee" face="Arial, Helvetica, sans-serif"><br />
<br /><a style="color:#00aeef" href="javascript:doPopup('resources/login_content/registr.html', 'registr');">Зарегистрироваться в системе ЭкзоАтлет</a>
<br></font>
                                                <br>
                                                <font size="1" face="Arial, Helvetica, sans-serif">


<span id="informationMessage" style="font-family: sans-serif;"> </span>
	<table align="left" >
	<tr > <td><font size="4" color="#878787" face="Arial, Helvetica, sans-serif">Вход в систему:</font></td></tr>
		<tr>

			<td>		
				
				<table>
					<tr>
						<td>
							
						</td>
						</tr>
					<tr>
						<td align="right" style="text-align: right; font-size: small">Имя пользователя</td>
						<td><input id="j_username" type="text" name="j_username" /></td>
						
                                    </tr>
                                    <tr>
                                        <td align="right" style=" text-align: right; font-size: small">Пароль</td>
                                        <td>
                                            <input id="j_password" type="password" name="j_password" />
                                        </td>
                                    </tr>
                                    <tr style="display: none;">
                                        <td align="right" style=" text-align: right; font-size: small">Запомнить меня</td>
                                        <td>
                                            <input type="checkbox" name="_spring_security_remember_me" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td align="left">
                                            <input type="submit" value="Войти" />
                                            <input type="reset" value="Сбросить" />
                                            </br>
                                            </br>

                                            <a href="javascript:doPopup('resources/login_content/remind.html','remind');">Забыли пароль?</a>

                                        </td>

                                    </tr>
                                </table>
								<c:if test="${not empty param.error}"><span id="accessDenied" style="font-family: sans-serif;">
								  <font color="red">
								  <b>Ошибка!</b>
								  Имя пользователя и/или пароль неверны!<br/>
								  Отказано в доступе.
								  Ответ сервера: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} 
								  <br/>
								  </font>
                                                </span>

                                                </c:if>


                                            </td>

                                        </tr>
                                        <tr>
                                        </tr>
                                    </table>
                                    <br />
                                    <br />

                                    </font>
                                </td>
                            </tr>
                        </table>


                        <table width="600" cellpadding="0" cellspacing="0" border="0" align="center">
                            <tr>

                                <td>
                                    <img src="http://static.wixstatic.com/media/f313bb_ec0e6579e2124ac1b62dded50981c338.jpg_650" width="600
" alt="РНИМУ им. Н. И. Пирогова" border="0" hspace="10" IMAGE>
                                    <br />
                                </td>
                            </tr>
                        </table>

                        <table width="600" cellpadding="0" cellspacing="0" border="0" align="center">

                            <tr>
                                <td width="40" rowspan="5"></td>

                            </tr>

                        </table>

                        <table width="100%" cellpadding="0" cellspacing="0" border="0" align="center">
                            <tr>
                                <td width="40" rowspan="5"></td>
                                <td>
                                    <SPAN STYLE="BACKGROUND-COLOR:#00aeef"><font size="3" color="#FFFFFF" face="Arial, Helvetica, sans-serif"><strong><br />
<br />
ЭкзоАтлет </strong>— экзоскелет для сильных духом людей. <br />
Для тех, кто после травмы или инсульта мечтает вновь начать ходить.<br></font></SPAN>
                                    <br />


                                    <SPAN STYLE="BACKGROUND-COLOR:#00aeef"><font size="3" color="#FFFFFF" face="Arial, Helvetica, sans-serif"><strong>ЭкзоАтлет </strong>— это возможность реабилитации в клинике и в домашних условиях.
 Также для многих пациентов это повышение мобильности и улучшение качества жизни.<br></font></SPAN>
                                    <br />


                                </td>
                            </tr>
                        </table>


                        <table width="100%" cellpadding="0" cellspacing="0" border="0" align="center" bgcolor="#a0a1a1">
                            <tr>
                                <td width="40" rowspan="5"></td>
                                <td><font size="1" face="Arial, Helvetica, sans-serif"><br />+7 (495) 374-85-30<br />
<br />
</font></td>
                                <td><font size="1" face="Arial, Helvetica, sans-serif"><br /><a href="mailto:info@exoatlet.ru">info@exoatlet.ru</a><br />
<br />
</font></td>

                                <td><font size="1" face="Arial, Helvetica, sans-serif"><br /><a href="../www.exoatlet.ru">www.exoatlet.ru</a><br />
<br />
</font></td>
                            </tr>

                            <tr>&nbsp;
                                <br />
                            </tr>
                        </table>
                        <!-- end of content ------------------------------------------------ -->

                        </td>
                        </tr>
                        </table>
                    </form>
                </body>

                </html>