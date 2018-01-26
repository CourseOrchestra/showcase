package ru.curs.showcase.core.primelements.datapanel;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.primelements.PrimElementsGateway;
import ru.curs.showcase.core.sp.MSSQLExecGateway;
import ru.curs.showcase.util.*;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
@Description(
		process = "Загрузка данных для информационной панели из БД с помощью выполнения sql скрипта")
public class DataPanelMSSQLExecGateway extends MSSQLExecGateway implements PrimElementsGateway {

	private static final int MAIN_CONTEXT_INDEX = 3;
	private static final int SESSION_CONTEXT_INDEX = 4;
	private static final int DP_INDEX = 5;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final String dataPanelId) {
		setProcName(dataPanelId);
		return getRawData(context);
	}

	@Override
	protected String getParamsDeclaration() {
		return "@main_context varchar(MAX), @session_context xml, @data xml output, "
				+ super.getParamsDeclaration();
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context) {
		try {
			setContext(context);
			prepareSQL();
			setStringParam(MAIN_CONTEXT_INDEX, context.getMain());
			setSQLXMLParam(SESSION_CONTEXT_INDEX, context.getSession());
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
}
