package ru.curs.showcase.core.primelements.navigator;

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
@Description(process = "Загрузка данных для навигатора из БД")
public class NavigatorDBGateway extends SPQuery implements PrimElementsGateway {

	private static final int SESSION_CONTEXT_INDEX = 2;
	private static final int NAVIGATOR_INDEX = 3;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context) {
		setContext(context);
		try {
			prepareSQL();
			setSQLXMLParam(SESSION_CONTEXT_INDEX, context.getSession());
			getStatement().registerOutParameter(NAVIGATOR_INDEX, java.sql.Types.SQLXML);
			execute();

			InputStream stream = getInputStreamForXMLParam(NAVIGATOR_INDEX);
			return new DataFile<InputStream>(stream, getProcName());
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?, ?)}";
	}

	@Override
	public void setSourceName(final String aSourceName) {
		setProcName(aSourceName);

	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final String aSourceName) {
		setProcName(aSourceName);
		return getRawData(aContext);
	}

}
