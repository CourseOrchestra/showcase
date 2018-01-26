package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.FileUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае отсутствия rootpath userdata.
 * 
 */
public class NoUserDatasException extends BaseException {
	private static final long serialVersionUID = 7395563849883634373L;

	private static final String ERROR_MES =
		"Не задано ни одного каталога для пользовательских данных(userdata): ни в context.xml, ни в "
				+ FileUtils.GENERAL_PROPERTIES;

	public NoUserDatasException() {
		super(ExceptionType.USER, ERROR_MES);
	}

}
