package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае если имя файла содержит кириллический
 * символ.
 * 
 */
public class FileNameContainsCyrillicSymbolException extends BaseException {
	private static final long serialVersionUID = -7700293358971811722L;

	private static final String ERROR_MES = "Название файла '%s' содержит кириллический символ.";

	public FileNameContainsCyrillicSymbolException(final String file) {
		super(ExceptionType.USER, String.format(ERROR_MES, file));

	}
}
