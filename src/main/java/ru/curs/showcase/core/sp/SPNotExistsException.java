package ru.curs.showcase.core.sp;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.ReflectionUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, возникающее при отсутствии в БД требуемой хранимой процедуры.
 * Является по сути частным случаем DBQueryException.
 * 
 * @author den
 * 
 */
public class SPNotExistsException extends BaseException {

	private static final String ERROR_MES = "Процесс: %s. Процедура '%s' отсутствует в БД";

	private static final long serialVersionUID = -1310610425002788976L;

	public SPNotExistsException(final String procName, final Class<? extends SPQuery> gatewayClass) {
		super(ExceptionType.SOLUTION, String.format(ERROR_MES,
				ReflectionUtils.getProcessDescForClass(gatewayClass), procName));
	}
}
