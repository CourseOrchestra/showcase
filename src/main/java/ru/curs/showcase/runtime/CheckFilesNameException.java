package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае если при проверке имен файлов возникло
 * исключение.
 * 
 */
public class CheckFilesNameException extends BaseException {
	private static final long serialVersionUID = -6263986164364158799L;

	private static final String ERROR_MES = "Ошибка при проверке названий файлов: '%s'.";

	public CheckFilesNameException(final String file) {
		super(ExceptionType.USER, String.format(ERROR_MES, file));

	}
}
