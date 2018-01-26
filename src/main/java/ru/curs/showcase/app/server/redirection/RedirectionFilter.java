package ru.curs.showcase.app.server.redirection;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class RedirectionFilter implements Filter {

	// private class MyException extends BaseException {
	// private static final long serialVersionUID = 6725288887082284411L;
	//
	// MyException(final ExceptionType aType, final String aMessage) {
	// super(aType, aMessage);
	// }
	// }

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {

		if (RedirectionUserdataProp.getPathToRedirect().isEmpty()
				|| RedirectionUserdataProp.getExtensionToRedirect().isEmpty()
				|| RedirectionUserdataProp.getRedirectionProc().isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;

		String sesId = httpReq.getSession().getId();

		String initialUrl = getFullURL(httpReq);

		// ===
		Boolean isNeedRedirect = false;
		for (String redirrectionPath : RedirectionUserdataProp.getPathToRedirect()) {

			if (initialUrl.contains(redirrectionPath)) {

				for (String redirrectionExt : RedirectionUserdataProp.getExtensionToRedirect()) {
					if (initialUrl.contains("." + redirrectionExt)) {
						isNeedRedirect = true;
						break;
					}
				}
				break;
			}

		}
		// ==

		if (!isNeedRedirect) {
			filterChain.doFilter(request, response);
			return;
		}
		// String redirectToUrl =
		// getRedirectionUrlForLink(initialUrl, sesId,
		// RedirectionUserdataProp.getRedirectionProc());

		RedirectSelector rs = new RedirectSelector();
		String redirectionProc = RedirectionUserdataProp.getRedirectionProc();
		rs.setSourceName(redirectionProc);

		String redirectToUrl =
			rs.getGateway().getRedirectionUrlForLink(initialUrl, sesId, redirectionProc);

		if (redirectToUrl != null && !redirectToUrl.isEmpty() && !redirectToUrl.equals(initialUrl)) {
			httpRes.sendRedirect(redirectToUrl);

		} else {
			filterChain.doFilter(request, response);
		}

	}

	private String getFullURL(final HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
	}
}
