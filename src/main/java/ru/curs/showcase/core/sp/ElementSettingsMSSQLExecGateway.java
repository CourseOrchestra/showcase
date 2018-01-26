package ru.curs.showcase.core.sp;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к БД для получения настроек элементов c помощью выполнения SQL скрипта.
 * 
 * @author den
 * 
 */
@Description(
		process = "Загрузка метаданных для элемента инф. панели из БД c помощью выполнения SQL скрипта")
public class ElementSettingsMSSQLExecGateway extends ElementSPQuery implements
		ElementSettingsGateway {

	private static final int OUT_SETTINGS_PARAM = 8;

	private static final int MAIN_CONTEXT_INDEX = 3;

	private final MSSQLExecGateway mssql = new MSSQLExecGateway(this) {
		@Override
		protected String getParamsDeclaration() {
			return "@main_context varchar(MAX), @add_context varchar(MAX), @filterinfo xml,"
					+ " @session_context xml, @element_Id varchar(MAX), @settings xml output, "
					+ super.getParamsDeclaration();
		}
	};

	@Override
	public RecordSetElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setProcName(getProcName());

		try {
			prepareStdStatement();
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
		return mssql.getSqlTemplate(index);
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return mssql.getErrorMesIndex(index);
	}

	@Override
	protected void prepareSQL() throws SQLException {
		mssql.prepareSQL();
	}

	@Override
	protected int getReturnParamIndex() {
		return mssql.getReturnParamIndex();
	}

	@Override
	protected int getMainContextIndex() {
		return MAIN_CONTEXT_INDEX;
	}

	@Override
	public Object getSession() {
		return getConn();
	}

}
