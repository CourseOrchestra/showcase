package ru.curs.showcase.core.sp;

import java.sql.SQLException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к БД для получения настроек css (имен файлов css).
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка имен файлов css для Showcase из БД")
public class StyleDBGateway extends SPQuery implements StyleGateway {

	private static final int ERROR_MES_INDEX = 5;

	private static final int FIRST_OUT_PARAM_INDEX = 3;

	private static final int SECOND_OU_PARAM_INDEX = 4;

	@Override
	public String[] getRawData(final CompositeContext context, final String procName) {
		// init(context, elementInfo);
		setContext(context);

		setProcName(procName);

		try {

			prepareStatementWithErrorMes();
			// getStatement().registerOutParameter(getOutSettingsParam(),
			// java.sql.Types.SQLXML);

			setSQLXMLParam(2, "");
			if (context != null) {

				if (context.getSession() != null) {
					setSQLXMLParam(2, context.getSession());
				}
			}

			getStatement().registerOutParameter(FIRST_OUT_PARAM_INDEX, java.sql.Types.VARCHAR);
			getStatement().registerOutParameter(SECOND_OU_PARAM_INDEX, java.sql.Types.VARCHAR);
			execute();

			String[] str = new String[2];
			str[0] = getStatement().getString(FIRST_OUT_PARAM_INDEX);
			str[1] = getStatement().getString(SECOND_OU_PARAM_INDEX);
			// getStatement().getst
			return str; // new RecordSetElementRawData(this, elementInfo,
						// context);
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	// @Override
	// public String getProcName() {
	// return getElementInfo().getMetadataProc().getName();
	// }

	// @Override
	// public int getOutSettingsParam() {
	// return OUT_SETTINGS_PARAM;
	// }

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s (?, ?, ?, ?)}";
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

}
