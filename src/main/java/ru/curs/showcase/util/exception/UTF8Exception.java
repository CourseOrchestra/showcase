package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Исключение при невозможности использованию кодировки по умолчанию Showcase -
 * UTF-8.
 * 
 * @author den
 * 
 */
public class UTF8Exception extends BaseException {

	private static final long serialVersionUID = -7749675333712026960L;

	public UTF8Exception() {
		super(ExceptionType.JAVA,
				"Среда выполнения не поддерживает кодировку UTF-8, необходимую для работы приложения");
	}

}
