package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.*;

/**
 * Фильтр для настройки базовых параметров запросов к серверу и ответов сервера.
 * Должен обрабатывать все запросы к серверу.
 * 
 * @author den
 * 
 */
public class PreProcessFilter implements Filter {
	private static final String GEO_JS = "geo.js";

	/**
	 * Префикс сервлетов, используемых в механизме аутентификации.
	 */
	// private static final String AUTH_DATA_SERVLET_PREFIX = "auth";
	/**
	 * Имя основной страницы приложения.
	 */
	public static final String INDEX_PAGE = "index.jsp";

	/**
	 * Имя страницы логина приложения.
	 */
	public static final String LOGIN_PAGE = "login.jsp";

	private String geoCheckFile = GEO_JS;

	/**
	 * Выставляем кодировку UTF-8 у всех вызовов. Раньше на этом сыпался GWT,
	 * сейчас все ок. Обязательно перехватываем Throwable, чтобы на клиента не
	 * шел HTML.
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;

			if (isMainPage(httpRequest)) {
				initSession(httpRequest); // TODO нужно ли
			}
			if (isDynamicDataServlet(httpRequest)) {
				skipServletCaching(response);
			} else {
				HttpServletResponse resp = (HttpServletResponse) response;
				// resp.setHeader("Pragma", "no-cache");

				// resp.setHeader("Cache-Control", "max-age = 31536000");
				// Date d = new Date();
				// resp.setHeader("Last-Modified", d.toString());

				// resp.setDateHeader("Expires", 0);
				// resp.setDateHeader("Expires", 0);
			}
			httpRequest.setCharacterEncoding(TextUtils.DEF_ENCODING);

			if (handleGeoModule(httpRequest, httpResponse)) {
				return;
			}
		}

		chain.doFilter(request, response);
		resetThread();
	}

	private boolean handleGeoModule(final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse) throws IOException {
		if (httpRequest.getRequestURI().endsWith("js/course/" + geoCheckFile)) {
			File file =
				new File(httpRequest.getSession().getServletContext()
						.getRealPath("js/course/" + geoCheckFile));
			if (!file.exists()) {
				httpResponse.setContentType("text/javascript");
				httpResponse.setStatus(HttpServletResponse.SC_OK);
				InputStream is =
					httpRequest.getSession().getServletContext()
							.getResourceAsStream("js/course/geo_fake.js");
				try {
					try (PrintWriter writer = httpResponse.getWriter()) {
						writer.append(TextUtils.streamToString(is));
					}
				} finally {
					is.close();
				}
				return true;
			}
		}
		return false;
	}

	private void resetThread() {
		AppInfoSingleton.getAppInfo().setCurUserDataId((String) null);

	}

	private void skipServletCaching(final ServletResponse resp) {
		HttpServletResponse httpresp = (HttpServletResponse) resp;
		ServletUtils.doNoCasheResponse(httpresp);
	}

	private void initSession(final HttpServletRequest httpreq) {
		HttpSession session = httpreq.getSession(true);
		AppInfoSingleton.getAppInfo().addSession(session.getId());
	}

	private boolean isMainPage(final HttpServletRequest httpreq) {
		return httpreq.getServletPath().contains("/" + INDEX_PAGE);
	}

	private boolean isDynamicDataServlet(final HttpServletRequest httpreq) {
		// String servletPath = httpreq.getServletPath();
		// return servletPath.startsWith("/" +
		// ExchangeConstants.SECURED_SERVLET_PREFIX)
		// || servletPath.startsWith("/" + AUTH_DATA_SERVLET_PREFIX)
		// || servletPath.startsWith("/" + INDEX_PAGE)
		// || servletPath.startsWith("/" + LOGIN_PAGE);

		String tempUrl = httpreq.getRequestURL().toString();
		// System.out.println(tempUrl); "secured.nocache.js"

		if (tempUrl.contains("secured.nocache.js")) {
			return true;
		}

		if (tempUrl.endsWith("js") || tempUrl.endsWith("css") || tempUrl.endsWith("png")
				|| tempUrl.endsWith("gif") || tempUrl.endsWith("jpg")) {
			return false;
		}

		return true;
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	public void setGeoCheckFile(final String aGeoCheckFile) {
		geoCheckFile = aGeoCheckFile;
	}

}
