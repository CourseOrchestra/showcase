package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае обращения к несуществующей userdata.
 * 
 */
public class NoSuchUserDataException extends BaseException {
	private static final long serialVersionUID = -6928633456458398538L;

	private static final String ERROR_MES =
		"Perspective named '%s' is not available in user's data folder";

	/**
	 * Идентификатор userdata.
	 */
	private final String userDataId;

	public String getUserDataId() {
		return userDataId;
	}

	public NoSuchUserDataException(final String aUserDataId) {
		super(ExceptionType.USER, String.format(ERROR_MES, aUserDataId));
		userDataId = aUserDataId;

	}
}
