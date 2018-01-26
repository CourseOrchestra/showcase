package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.*;
import org.springframework.stereotype.Component;

@Component
public class ShowcaseAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		DefaultSavedRequest defaultSavedRequest =
			(DefaultSavedRequest) (new HttpSessionRequestCache().getRequest(request, response));

		String requestUrl = "";

		if (defaultSavedRequest != null)
			requestUrl = defaultSavedRequest.getRequestURL().toString();
		else
			requestUrl = request.getContextPath() + "/";

		String queryString = "";
		String port = "" + request.getServerPort();

		if (defaultSavedRequest != null)
			queryString = defaultSavedRequest.getQueryString();

		String webAppName = request.getContextPath();
		if (webAppName.contains("/")) {
			webAppName = webAppName.replace("/", "");
		}

		if (queryString == null || "".equals(queryString)) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("queryString" + port + webAppName)
							&& cookie.getValue() != null && !"".equals(cookie.getValue())) {
						queryString = cookie.getValue();
					}
				}
			}
		}

		if (queryString != null && !"".equals(queryString))
			requestUrl = requestUrl + "?" + queryString;

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(requestUrl);
	}

}
