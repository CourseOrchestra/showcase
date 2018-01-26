package ru.curs.showcase.core.html.jsForm;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.util.Description;

/**
 * Шлюз получения данных для jsForms из БД.
 * 
 * @author bogatov
 * 
 */
@Description(process = "Загрузка данных для jsForm из БД")
public class JsFormDBGateway extends SPQuery implements JsFormSubmitGateway {
	private static final int ELEMENTID_INDEX = 6;
	private static final int INDATA_INDEX = 7;
	private static final int OUTDATA_INDEX = 8;

	public JsFormDBGateway(final String procName) {
		super();
		setProcName(procName);
	}

	@Override
	public String getData(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String inData) {
		setContext(context);
		try (SPQuery query = this) {
			try {
				prepareSQL();
				setupGeneralParameters();
				setStringParam(ELEMENTID_INDEX, elementInfo.getId().getString());
				setStringParam(INDATA_INDEX, inData);
				getStatement().registerOutParameter(OUTDATA_INDEX, java.sql.Types.VARCHAR);
				execute();
				String outData = getStatement().getString(OUTDATA_INDEX);
				return outData;
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s (?, ?, ?, ?, ?, ?, ?)}";
	}

}
