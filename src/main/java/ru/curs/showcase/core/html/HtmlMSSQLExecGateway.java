package ru.curs.showcase.core.html;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.sp.MSSQLExecGateway;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Реализация шлюза к БД с sql скриптами для получения данных для элементов инф.
 * панели типа вебтекст, xform и UI плагин.
 * 
 * @author den
 * 
 */
@Description(
		process = "Загрузка данных для вебтекста, xform или UI плагина из БД с помощью sql скрипта")
public class HtmlMSSQLExecGateway extends HTMLBasedElementQuery {

	private static final int OUTPUT_SUBMISSION_INDEX = 4;
	private static final int INPUT_SUBMISSION_INDEX = 3;
	private static final int MAIN_CONTEXT_INDEX = 3;
	private static final int OUT_SETTINGS_PARAM_INDEX = 9;
	private static final int DATA_PARAM_INDEX = 8;

	private final MSSQLExecGateway mssql = new MSSQLExecGateway(this) {
		@Override
		protected String getParamsDeclaration() {
			switch (templateIndex()) {
			case GET_DATA_TEMPALTE_IND:
				return "@main_context varchar(MAX), @add_context varchar(MAX), @filterinfo xml, "
						+ "@session_context xml, @element_Id varchar(MAX), @data xml output, @settings xml output, "
						+ super.getParamsDeclaration();
			case SAVE_TEMPLATE_IND:
				return "@main_context varchar(MAX), @add_context varchar(MAX), @filterinfo xml, "
						+ "@session_context xml, @element_Id varchar(MAX), @data xml, "
						+ super.getParamsDeclaration();
			case SUBMISSION_TEMPLATE_IND:
				return "@inputdata xml, @outputdata xml output," + super.getParamsDeclaration();
			default:
				throw new NotImplementedYetException();
			}
		}
	};

	private int templateIndex() {
		return getTemplateIndex();
	}

	@Override
	protected void prepareSQL() throws SQLException {
		mssql.prepareSQL();
	}

	@Override
	protected String getSqlTemplate(final int aIndex) {
		return mssql.getSqlTemplate(aIndex);
	}

	@Override
	protected int getReturnParamIndex() {
		return mssql.getReturnParamIndex();
	}

	@Override
	protected int getErrorMesIndex(final int aIndex) {
		return mssql.getErrorMesIndex(aIndex);
	}

	@Override
	public int getDataParam() {
		return DATA_PARAM_INDEX;
	}

	@Override
	protected int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM_INDEX;
	}

	@Override
	protected int getMainContextIndex() {
		return MAIN_CONTEXT_INDEX;
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final ID linkId) {
		throw new NotImplementedYetException();
	}

	@Override
	public void uploadFile(final XFormContext context, final DataPanelElementInfo elementInfo,
			final ID linkId, final DataFile<InputStream> file) {
		throw new NotImplementedYetException();
	}

	@Override
	protected int getOutputSubmissionIndex() {
		return OUTPUT_SUBMISSION_INDEX;
	}

	@Override
	protected int getInputSubmissionIndex() {
		return INPUT_SUBMISSION_INDEX;
	}
}
