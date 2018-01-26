package ru.curs.showcase.app.server.redirection;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

public class RedirectException extends BaseException {
	private static final long serialVersionUID = 6725288887082284411L;

	RedirectException(final ExceptionType aType, final String aMessage) {
		super(aType, aMessage);
	}

}
