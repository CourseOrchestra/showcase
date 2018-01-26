package ru.curs.showcase.core.primelements.navigator;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.primelements.PrimElementsGateway;
import ru.curs.showcase.core.sp.MSSQLExecGateway;
import ru.curs.showcase.util.*;

/**
 * Шлюз к SQL коду, хранящемуся в файловой системе, который нужно выполнить,
 * чтобы получить данные для навигатора.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для навигатора из БД с помощью выполнения SQL файла")
public class NavigatorMSSQLExecGateway extends MSSQLExecGateway implements PrimElementsGateway {

	private static final int SESSION_CONTEXT_INDEX = 3;
	private static final int NAVIGATOR_INDEX = 4;

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
	protected String getParamsDeclaration() {
		return "@session_context xml, @data xml output, " + super.getParamsDeclaration();
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
