package ru.curs.showcase.core.grid;

import java.sql.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.sp.MSSQLExecGateway;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Шлюз к БД для грида для работы c помощью SQL скриптов.
 * 
 * 
 */
@Description(process = "Загрузка данных для грида из БД c помощью SQL скрипта")
public class GridMSSQLExecGateway extends AbstractGridDBGateway {

	private static final int SORTCOLS_INDEX = 8;
	private static final int OUT_SETTINGS_PARAM = 9;
	private static final int FIRST_RECORD_INDEX = 9;
	private static final int PAGE_SIZE_INDEX = 10;
	private static final int PARENT_ID = 11;
	private static final int MAIN_CONTEXT_INDEX = 3;

	private final MSSQLExecGateway mssql = new MSSQLExecGateway(this) {
		// CHECKSTYLE:OFF
		@Override
		protected String getParamsDeclaration() {
			switch (getTemplateIndex()) {
			case DATA_AND_SETTINS_QUERY:
				return "@main_context varchar(MAX), @add_context varchar(MAX), @filterinfo xml, "
						+ "@session_context xml, @element_id varchar(MAX), @sortcols varchar(MAX), @settings xml output, "
						+ super.getParamsDeclaration();
			case DATA_ONLY_QUERY:
				if (((GridContext) getContext()).getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
					return "@main_context varchar(MAX), @add_context varchar(MAX), @filterinfo xml, "
							+ "@session_context xml, @element_id varchar(MAX), @sortcols varchar(MAX) ,"
							+ " @firstrecord int, @pagesize int, @parent_id varchar(MAX), "
							+ super.getParamsDeclaration();

				} else {
					return "@main_context varchar(MAX), @add_context varchar(MAX), @filterinfo xml, "
							+ "@session_context xml, @element_id varchar(MAX), @sortcols varchar(MAX) ,"
							+ " @firstrecord int, @pagesize int, " + super.getParamsDeclaration();

				}
			default:
				return null;
			}
		}
		// CHECKSTYLE:ON
	};

	public GridMSSQLExecGateway() {
		super();
	}

	public GridMSSQLExecGateway(final Connection aConn) {
		super();
		setConn(aConn);
	}

	@Override
	protected int getSortColsIndex() {
		return SORTCOLS_INDEX;
	}

	@Override
	protected void prepareForGetDataAndSettings() throws SQLException {
		prepareStdStatement();
		getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
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
	protected void prepareForGetData() throws SQLException {
		prepareSQL();
		setupGeneralElementParameters();
		setupRange();
	}

	@Override
	protected int getPageSizeIndex() {
		return PAGE_SIZE_INDEX;
	}

	@Override
	protected int getFirstRecordIndex() {
		return FIRST_RECORD_INDEX;
	}

	@Override
	protected int getParentIdIndex() {
		return PARENT_ID;
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return mssql.getErrorMesIndex(index);
	}

	@Override
	public OutputStreamDataFile downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final ID linkId, final String recordId) {
		throw new NotImplementedYetException();
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
	public GridSaveResult saveData(final GridContext context, final DataPanelElementInfo element) {
		return null;
	}

	@Override
	public GridAddRecordResult addRecord(final GridContext context,
			final DataPanelElementInfo element) {
		return null;
	}

}
