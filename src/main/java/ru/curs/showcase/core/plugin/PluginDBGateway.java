package ru.curs.showcase.core.plugin;

import java.io.InputStream;
import java.sql.SQLException;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.sp.ElementSPQuery;

/**
 * Шлюз для получения данных, источником которых являются хранимые процедуры.
 * 
 * @author bogatov
 * 
 */
public class PluginDBGateway extends ElementSPQuery implements HTMLGateway {
	private static final int PARAMS_INDEX = 7;
	private static final int DATA = 8;
	private static final int SETTINGS = 9;

	private final String xmlParams;

	public PluginDBGateway(final String sXmlParams) {
		this.xmlParams = sXmlParams;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?,?,?,?,?,?,?,?)}";
	}

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();
		setSQLXMLParam(PARAMS_INDEX, xmlParams);
		getStatement().registerOutParameter(DATA, java.sql.Types.SQLXML);
	}

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo element) {
		init(context, element);
		try {
			try {
				prepareStdStatement();
				execute();
				Document data = getDocumentForXMLParam(DATA);
				InputStream validatedSettings = getValidatedSettings();
				return new HTMLBasedElementRawData(data, validatedSettings, getElementInfo(),
						getContext());

			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		} finally {
			close();
		}
	}

	@Override
	protected int getOutSettingsParam() {
		return SETTINGS;
	}

}
