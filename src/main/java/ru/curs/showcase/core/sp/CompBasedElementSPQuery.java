package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.StreamConvertor;
import ru.curs.showcase.util.xml.*;

/**
 * Вспомогательный класс для получения данных элементов инф. панели, основанных
 * на компонентах.
 * 
 * @author den
 * 
 */
public abstract class CompBasedElementSPQuery extends ElementSPQuery {

	/**
	 * Стандартная функция выполнения запроса с проверкой на возврат результата.
	 */
	protected void stdGetResults() throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
			getConn().setAutoCommit(false);
		}

		execute();
	}

	public static final String NO_RESULTSET_ERROR = "хранимая процедура не возвратила данные";

	/**
	 * Стандартный метод возврата данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - инф. об элементе.
	 */
	protected RecordSetElementRawData stdGetData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		try {
			prepareStdStatement();
			stdGetResults();
			return new RecordSetElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();

		registerOutParameterCursor();
	}

	protected abstract void registerOutParameterCursor() throws SQLException;

	private static final String SAX_ERROR_MES = "обобщенные настройки (настройки плюс данные)";

	@Override
	protected void fillValidatedSettings() throws SQLException {
		InputStream settings = getInputStreamForXMLParam(getOutSettingsParam());
		if (settings != null) {
			ByteArrayOutputStream osSettings = new ByteArrayOutputStream();
			ByteArrayOutputStream osDS = new ByteArrayOutputStream();

			SimpleSAX sax =
				new SimpleSAX(settings, new StreamDivider(osSettings, osDS), SAX_ERROR_MES);
			sax.parse();

			InputStream isSettings = StreamConvertor.outputToInputStream(osSettings);
			if (getSettingsSchema() != null) {
				setValidatedSettings(XMLUtils.xsdValidateAppDataSafe(isSettings,
						getSettingsSchema()));
			} else {
				setValidatedSettings(isSettings);
			}

			if (osDS.size() == 0) {
				setXmlDS(null);
			} else {
				setXmlDS(StreamConvertor.outputToInputStream(osDS));
			}

		} else {
			setValidatedSettings(null);
			setXmlDS(null);
		}
	}

}
