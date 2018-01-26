package ru.curs.showcase.util;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Специальный класс ошибки для передачи наверх любого исключения в
 * XMLJSONConverter.
 */

public final class XMLJSONConverterException extends BaseException {

	private static final long serialVersionUID = 8443114082185616801L;

	public XMLJSONConverterException(final Throwable aCause) {
		super(ExceptionType.APP, aCause);
	}

}
