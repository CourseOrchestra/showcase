package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае обращения к несуществующей директории
 * rootpath для userdata.
 * 
 */
public class NoSuchRootPathUserDataException extends BaseException {
	private static final long serialVersionUID = 5437984959709470025L;

	private static final String ERROR_MES =
		"Корневой каталог пользовательских данных(rootpath userdata) '%s' не существует";

	private final String rootpath;

	public String getRootpath() {
		return rootpath;
	}

	public NoSuchRootPathUserDataException(final String aRootpath) {
		super(ExceptionType.USER, String.format(ERROR_MES, aRootpath));
		rootpath = aRootpath;
	}
}
