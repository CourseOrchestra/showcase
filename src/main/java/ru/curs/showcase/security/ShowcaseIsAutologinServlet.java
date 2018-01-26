package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Servlet implementation class ShowcaseIsAutologinServlet.
 */
public class ShowcaseIsAutologinServlet extends HttpServlet {

	private static final long serialVersionUID = 9152046062107154321L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String autologin = request.getParameter("autologin");

		String username = "";
		String password = "";
		String submit = "";

		if ("true".equals(autologin)) {
			username = UserDataUtils.getGeneralOptionalProp("autologin.username").trim();
			password = UserDataUtils.getGeneralOptionalProp("autologin.password").trim();
			submit = "true";
		}

		response.getWriter().append(
				String.format("{login:'%s', pwd:'%s', submit:'%s'}", username, password, submit));
		response.getWriter().close();
	}

}
