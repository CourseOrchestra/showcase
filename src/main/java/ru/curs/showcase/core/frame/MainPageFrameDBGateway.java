package ru.curs.showcase.core.frame;

import java.sql.SQLException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к БД для получения фреймов главной страницы.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для фрейма на главной странице из БД")
public class MainPageFrameDBGateway extends SPQuery implements MainPageFrameGateway {

	private static final int SESSION_CONTEXT_INDEX = 2;
	private static final int FRAME_DATA_INDEX = 3;
	private static final int ERROR_MES_INDEX = 4;

	@Override
	public String getRawData(final CompositeContext context, final String frameSource) {
		setProcName(frameSource);
		return getRawData(context);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s (?, ?, ?)}";
	}

	@Override
	public String getRawData(final CompositeContext context) {
		try (SPQuery query = this) {
			try {
				prepareStatementWithErrorMes();
				setSQLXMLParam(SESSION_CONTEXT_INDEX, context.getSession());
				getStatement().registerOutParameter(FRAME_DATA_INDEX, java.sql.Types.VARCHAR);
				setContext(context);
				execute();

				String result = getStatement().getString(FRAME_DATA_INDEX);
				return result;
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	@Override
	public void setSourceName(final String aName) {
		setProcName(aName);
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

}
