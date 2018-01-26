package ru.curs.showcase.security.logging;

import javax.servlet.http.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.command.ServiceLayerCommand;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.security.logging.Event.TypeEvent;
import ru.curs.showcase.util.ServletUtils;

/**
 * Команда для обработки события с использованием хранимой процедуры.
 * 
 * @author bogatov
 * 
 */
public class SecurityLoggingCommand extends ServiceLayerCommand<Void> {
	public static final String IS_CLICK_LOGOUT = "IS_CLICK_LOGOUT";

	private final HttpServletRequest request;
	private final HttpSession session;
	private final Event event;

	public SecurityLoggingCommand(final CompositeContext context,
			final HttpServletRequest oRequest, final HttpSession oSession,
			final TypeEvent typeEvent) {
		super(context);
		this.request = oRequest;
		this.session = oSession;
		this.event = new Event(typeEvent, context);
	}

	public SecurityLoggingCommand(final CompositeContext context,
			final HttpServletRequest oRequest, final HttpSession oSession, final Event oEvent) {
		super(context);
		this.request = oRequest;
		this.session = oSession;
		this.event = oEvent;
	}

	@Override
	protected void mainProc() throws Exception {
		// UserDataUtils.getOptionalProp("security.logging.proc");
		String procName = UserDataUtils.getGeneralOptionalProp("security.logging.proc");
		if (procName != null && !procName.isEmpty() && this.event != null) {
			HttpSession httpSession = null;
			if (this.request != null) {
				// httpSession = this.request.getSession();
				httpSession =
					(HttpSession) this.request.getSession(false).getAttribute("newSession");
				event.add("IP", this.request.getRemoteAddr());
				event.add("Host", this.request.getRemoteHost());
				String userAgent = ServletUtils.getUserAgent(this.request);
				BrowserType browserType = BrowserType.detect(userAgent);
				event.add("Browser", browserType != null ? browserType.toString() : "");
				event.add("BrowserVersion", BrowserType.detectVersion(userAgent));
				event.add("OS", browserType != null ? OSType.detect(userAgent).toString() : "");
				event.add("OSVersion", OSType.detectVersion(userAgent));
				event.add("UserAgent", userAgent);
				Exception securityLastExeption =
					(Exception) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
				if (securityLastExeption != null)
					event.add("SecurityLastExeption", securityLastExeption.getMessage());
			} else if (this.session != null) {
				httpSession = this.session;
			}
			if (httpSession != null) {
				event.add("HttpSessionId", httpSession.getId());
				if (event.getTypeEvent() == TypeEvent.SESSIONTIMEOUT
						|| event.getTypeEvent() == TypeEvent.LOGIN) {
					String userName = (String) httpSession.getAttribute("username");
					if (userName != null)
						event.add("UserName", userName);
				}
			}

			SecurityLoggingSelector selector = new SecurityLoggingSelector(procName);
			SecurityLoggingGateway gateway = selector.getGateway();
			gateway.doLogging(this.event);
		}
	}
}
