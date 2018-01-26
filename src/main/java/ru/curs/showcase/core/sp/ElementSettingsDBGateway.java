package ru.curs.showcase.core.sp;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к БД для получения настроек элементов.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка метаданных для элемента инф. панели из БД")
public class ElementSettingsDBGateway extends ElementSPQuery implements ElementSettingsGateway {

	private static final int OUT_SETTINGS_PARAM = 7;
	private static final int ERROR_MES_INDEX = 8;

	@Override
	public RecordSetElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setProcName(getProcName());

		try {
			prepareElementStatementWithErrorMes();
			getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
			execute();
			return new RecordSetElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	@Override
	public String getProcName() {
		return getElementInfo().getMetadataProc().getName();
	}

	@Override
	public int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s (?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

	@Override
	public Object getSession() {
		return getConn();
	}

}
