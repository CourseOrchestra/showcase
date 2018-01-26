package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.transform.TransformerException;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Servlet implementation class ShowcaseIsAuthenticatedServlet.
 */
public class ShowcaseIsAuthenticatedServlet extends HttpServlet {

	private static final long serialVersionUID = 9152046062107176349L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			throw new ServletException(SecurityParamsFactory.APP_PROP_READ_ERROR, e);
		}

		String sesid = request.getParameter("sesid");

		if (url != null) {
			UserInfo ud = connectToAuthServer(url, sesid);
			if (ud != null) {
				prepareGoodResponce(response, ud, sesid);
			}
		}
	}

	private UserInfo connectToAuthServer(final String url, final String sesid)
			throws IOException, ServletException {
		HttpURLConnection c = null;
		try {
			URL server = new URL(url + String.format("/isauthenticated?sesid=%s", sesid));
			c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			c.connect();
			UserInfo ud = null;
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				try {
					List<UserInfo> l = UserInfoUtils.parseStream(c.getInputStream());
					ud = l.get(0);
					ud.setResponseCode(c.getResponseCode());
				} catch (TransformerException e) {
					throw new ServletException(
							AuthServerUtils.AUTH_SERVER_DATA_ERROR + e.getMessage(), e);
				}
			}
			return ud;
		} finally {
			if (c != null) {
				c.disconnect();
			}

		}

	}

	private void prepareGoodResponce(final HttpServletResponse response, final UserInfo ud,
			final String sesid) throws IOException {
		response.reset();
		response.setStatus(ud.getResponseCode());
		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);

		final Integer n1 = 10000;
		final Integer n2 = 100;
		final Integer n3 = 754658923;
		Random r = new Random();
		String pwd = "default_value" + r.nextInt(n3) + "AXCVGTEREW" + r.nextInt(n1) + "nbgfredsc"
				+ r.nextInt(n2);

		if (AppInfoSingleton.getAppInfo().getSessionInfoMap().get(sesid) == null) {
			SessionInfo sesInfo = new SessionInfo();
			sesInfo.setAuthServerCrossAppPassword(pwd);
			AppInfoSingleton.getAppInfo().getSessionInfoMap().put(sesid, sesInfo);
		} else {
			AppInfoSingleton.getAppInfo().getSessionInfoMap().get(sesid)
					.setAuthServerCrossAppPassword(pwd);
		}

		response.getWriter().append(String.format("{login:'%s', pwd:'%s'}", ud.getCaption(), pwd));

		response.getWriter().close();
	}

}
