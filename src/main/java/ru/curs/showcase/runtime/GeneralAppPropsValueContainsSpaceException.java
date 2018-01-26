package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае если значение из общего файла
 * app.properties содержит пробел.
 * 
 */
public class GeneralAppPropsValueContainsSpaceException extends BaseException {
	private static final long serialVersionUID = -5590364619289647117L;

	private static final String ERROR_MES =
		"Общий файл app.properties содержит параметр %s, в значении которого (%s) присутствует пробел.";

	public GeneralAppPropsValueContainsSpaceException(final String param, final String value) {
		super(ExceptionType.USER, String.format(ERROR_MES, param, value));

	}
}
