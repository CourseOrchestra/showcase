package ru.curs.showcase.core.primelements.datapanel;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.primelements.PrimElementsGateway;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.util.*;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для информационной панели из БД")
public class DataPanelDBGateway extends SPQuery implements PrimElementsGateway {

	private static final int SESSION_CONTEXT_INDEX = 3;
	private static final int DP_INDEX = 4;
	private static final int ERROR_MES_INDEX = 5;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final String dataPanelId) {
		setProcName(dataPanelId);
		return getRawData(context);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s (?, ?, ?, ?)}";
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context) {
		try {
			setContext(context);
			prepareStatementWithErrorMes();
			setSQLXMLParam(SESSION_CONTEXT_INDEX, context.getSession());
			setStringParam(getMainContextIndex(), context.getMain());
			getStatement().registerOutParameter(DP_INDEX, java.sql.Types.SQLXML);
			execute();
			InputStream stream = getInputStreamForXMLParam(DP_INDEX);
			return new DataFile<InputStream>(stream, getProcName());
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
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
