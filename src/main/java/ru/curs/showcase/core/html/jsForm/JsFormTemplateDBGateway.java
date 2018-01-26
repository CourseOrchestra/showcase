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
public class JsFormTemplateDBGateway extends SPQuery implements JsFormTemplateGateway {
	private static final int ELEMENTID_INDEX = 6;
	private static final int OUTDATA_INDEX = 7;
	private static final int OUTSETTING_INDEX = 8;

	public JsFormTemplateDBGateway(final String procName) {
		super();
		setProcName(procName);
	}

	@Override
	public JsFormData getData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		setContext(context);
		try (SPQuery query = this) {
			try {
				prepareSQL();
				setupGeneralParameters();
				setStringParam(ELEMENTID_INDEX, elementInfo.getId().getString());
				getStatement().registerOutParameter(OUTDATA_INDEX, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(OUTSETTING_INDEX, java.sql.Types.SQLXML);
				execute();
				String data = getStatement().getString(OUTDATA_INDEX);
				String setting = getStatement().getString(OUTSETTING_INDEX);
				return new JsFormData(data, setting);
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
