package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Ошибка соединения с БД.
 * 
 * @author den
 * 
 */
public class DBConnectException extends BaseException {

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "Ошибка при соединении с БД (userdata '%s')";

	private static final long serialVersionUID = 7586686198028153113L;

	public DBConnectException(final Throwable cause) {
		super(ExceptionType.SOLUTION, String.format(ERROR_MES, AppInfoSingleton.getAppInfo()
				.getCurUserDataId()), cause);
	}

}
