package ru.curs.showcase.core.chart;

import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.Description;

/**
 * Шлюз для получения данных для графика из БД.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для графика из БД")
public class ChartDBGateway extends CompBasedElementSPQuery implements
		RecordSetElementGateway<CompositeContext> {

	private static final int OUT_SETTINGS_PARAM = 7;

	private static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS = 8;

	@Override
	public RecordSetElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		setRetriveResultSets(true);

		RecordSetElementRawData raw = stdGetData(context, elementInfo);
		raw.prepareXmlDS();
		return raw;
	}

	@Override
	public int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?)}";
		} else {
			return "{? = call %s (?, ?, ?, ?, ?, ?)}";
		}
	}

	@Override
	protected void registerOutParameterCursor() throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS,
					OracleTypes.CURSOR);
		}
	}

}
