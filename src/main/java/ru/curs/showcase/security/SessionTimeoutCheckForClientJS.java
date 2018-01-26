package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * Сервлет, служащий для проверки таймаута сессии в селекторах.
 * 
 * @author s.borodanev
 */
public class SessionTimeoutCheckForClientJS extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SessionTimeoutCheckForClientJS() {
		super();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");

		// Т.к. при таймауте сессии все её атрибуты уничтожаются, то в случае
		// равенства null значения атрибута, можно утвеждать, что произошёл
		// таймаут сессии.
		String username = (String) request.getSession(false).getAttribute("username");
		if (username == null)
			response.getWriter().append(String.format("{value:'%s'}", "true"));
		else
			response.getWriter().append(String.format("{value:'%s'}", "false"));
	}

}
