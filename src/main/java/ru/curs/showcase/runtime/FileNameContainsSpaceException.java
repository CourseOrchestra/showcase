package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае если имя файла содержит пробел.
 * 
 */
public class FileNameContainsSpaceException extends BaseException {
	private static final long serialVersionUID = -3139296392237635258L;

	private static final String ERROR_MES = "Название файла '%s' содержит пробел.";

	public FileNameContainsSpaceException(final String file) {
		super(ExceptionType.USER, String.format(ERROR_MES, file));

	}
}
