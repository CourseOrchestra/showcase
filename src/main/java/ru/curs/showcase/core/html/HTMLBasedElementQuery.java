package ru.curs.showcase.core.html;

import java.io.InputStream;
import java.sql.SQLException;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.sp.*;

/**
 * Вспомогательный класс для работы с хранимыми процедурами получения данных для
 * построения основанных на HTML элементах.
 * 
 * @author den
 * 
 */
public abstract class HTMLBasedElementQuery extends ElementSPQuery implements HTMLAdvGateway {

	protected static final int GET_DATA_TEMPALTE_IND = 0;
	protected static final int SAVE_TEMPLATE_IND = 1;
	protected static final int SUBMISSION_TEMPLATE_IND = 2;
	protected static final int DOWNLOAD_FILE_TEMPLATE_IND = 3;
	protected static final int UPLOAD_FILE_TEMPLATE_IND = 4;
	private static final String NO_SAVE_PROC_ERROR = "Не задана процедура для сохранения XForms";

	/**
	 * Возвращает индекс OUT параметра с данными элемента. Необходим только для
	 * HTML-based элементов.
	 * 
	 * @return - индекс параметра.
	 */
	public abstract int getDataParam();

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();
		getStatement().registerOutParameter(getDataParam(), java.sql.Types.SQLXML);
	}

	/**
	 * Стандартный метод возврата данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - инф. об элементе.
	 */
	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		if (getProcName() == null) {
			return new HTMLBasedElementRawData(getElementInfo(), getContext());
		}

		try (SPQuery query = this) {
			try {
				prepareStdStatement();
				execute();
				Document data = getDocumentForXMLParam(getDataParam());
				InputStream validatedSettings = getValidatedSettings();
				return new HTMLBasedElementRawData(data, validatedSettings, getElementInfo(),
						getContext());
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String data) {
		setTemplateIndex(SAVE_TEMPLATE_IND);
		init(context, elementInfo);
		DataPanelElementProc proc = elementInfo.getSaveProc();
		if (proc == null) {
			throw new IncorrectElementException(NO_SAVE_PROC_ERROR);
		}
		setProcName(proc.getName());

		try (SPQuery query = this) {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(), data);
				execute();
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	@Override
	public String scriptTransform(final String procName, final XFormContext context) {
		setTemplateIndex(SUBMISSION_TEMPLATE_IND);
		setProcName(procName);
		setContext(context);

		try (SPQuery query = this) {
			try {
				prepareStatementWithErrorMes();
				setSQLXMLParam(getInputSubmissionIndex(), context.getFormData());
				getStatement().registerOutParameter(getOutputSubmissionIndex(),
						java.sql.Types.SQLXML);
				execute();
				return getStringForXMLParam(getOutputSubmissionIndex());
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	protected abstract int getOutputSubmissionIndex();

	protected abstract int getInputSubmissionIndex();

}
