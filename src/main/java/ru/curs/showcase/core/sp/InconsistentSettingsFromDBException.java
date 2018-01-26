package ru.curs.showcase.core.sp;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, возникающее при несогласованных настройках элементов, переданных
 * из БД.
 * 
 * @author den
 * 
 */
public final class InconsistentSettingsFromDBException extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Из БД переданы некорректные настройки: ";

	private static final long serialVersionUID = 5664191473699810949L;

	public InconsistentSettingsFromDBException(final String aString) {
		super(ExceptionType.SOLUTION, ERROR_MES + aString);
	}

}
