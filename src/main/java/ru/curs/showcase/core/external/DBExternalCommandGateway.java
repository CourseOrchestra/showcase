package ru.curs.showcase.core.external;

import java.sql.SQLException;

import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к БД для выполнения внешней команды (например, из веб-сервисов).
 * 
 * @author den
 * 
 */
@Description(process = "Выполнение внешней команды")
public class DBExternalCommandGateway extends SPQuery implements ExternalCommandGateway {
	private static final int ERROR_MES_PARAM = 4;
	private static final int OUTPUTDATA_PARAM = 3;

	@Override
	public String handle(final String aRequest, final String aSource) {
		setProcName(aSource);
		try (SPQuery query = this) {
			try {
				prepareStatementWithErrorMes();
				setStringParam(2, aRequest);
				getStatement().registerOutParameter(OUTPUTDATA_PARAM, java.sql.Types.LONGNVARCHAR);
				execute();
				String out = getStatement().getString(OUTPUTDATA_PARAM);
				return out;
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	@Override
	protected String getSqlTemplate(final int aIndex) {
		return "{? = call %s (?, ?, ?)}";
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_PARAM;
	}

}
