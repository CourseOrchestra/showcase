package ru.curs.showcase.core;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае отсутствия файла в БД.
 * 
 */
public class FileIsAbsentInDBException extends BaseException {
	private static final long serialVersionUID = -6928633456458398538L;

	private static final String ERROR_MES = "Файл отсутствует";

	public FileIsAbsentInDBException() {
		super(ExceptionType.USER, ERROR_MES);
	}
}
