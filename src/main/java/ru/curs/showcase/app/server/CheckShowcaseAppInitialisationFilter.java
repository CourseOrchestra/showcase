package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.TextUtils;

public class CheckShowcaseAppInitialisationFilter implements Filter {

	public CheckShowcaseAppInitialisationFilter() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;

		if (httpReq.getRequestURL().toString().contains("/api/")) {
			chain.doFilter(request, response);
		} else if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage() != null
				&& !AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage().isEmpty()) {
			String exceptionText = AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage();

			httpResp.setContentType("text/html");
			httpResp.setCharacterEncoding(TextUtils.DEF_ENCODING);
			httpResp.getWriter().println("<html>");
			httpResp.getWriter().println("<head><title>Error</title></head>");
			httpResp.getWriter().println("<body>");
			httpResp.getWriter().println("<h1>An error has occurred</h1>");
			httpResp.getWriter().println(
					"<div><strong>Error Text:</strong> " + exceptionText + "</div>");
			httpResp.getWriter().println("</body>");
			httpResp.getWriter().println("</html>");

		} else {
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
