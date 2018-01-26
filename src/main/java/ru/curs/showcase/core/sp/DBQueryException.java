package ru.curs.showcase.core.sp;

import java.sql.SQLException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.util.ReflectionUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, возникающее при запросе к БД.
 * 
 * @author den
 * 
 */
public class DBQueryException extends BaseException {

	private static final String ERROR_MES_TEXT = "Подробности";

	private static final String ERROR_HEADER =
		"Произошла ошибка при выполнении хранимой процедуры";

	private static final long serialVersionUID = 4849562484767586377L;

	public DBQueryException(final SQLException cause, final String aProcName,
			final Class<? extends SPQuery> gatewayClass) {
		super(ExceptionType.SOLUTION, String.format("Процесс: %s. %s %s.",
				ReflectionUtils.getProcessDescForClass(gatewayClass), ERROR_HEADER, aProcName),
				cause);
	}

	public DBQueryException(final DataPanelElementInfo aElementInfo, final String aErrorText) {
		super(ExceptionType.SOLUTION, String.format("%s %s. %s: %s.", ERROR_HEADER,
				aElementInfo.getProcName(), ERROR_MES_TEXT, aErrorText));
	}

}
