package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Ошибка при создании объекта или освобождении памяти. Используется для
 * перехвата исключений, возникающих при создании объектов - как из системных
 * библиотек, так и своих, а также при ошибке в момент вызова
 * class.newInstance().
 * 
 * @author den
 * 
 */
public class ServerObjectCreateCloseException extends BaseException {
	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Ошибка создания объекта";

	private static final long serialVersionUID = -7749067251383439818L;

	public ServerObjectCreateCloseException(final Throwable cause) {
		super(ExceptionType.APP, ERROR_MES, cause);
	}

}
