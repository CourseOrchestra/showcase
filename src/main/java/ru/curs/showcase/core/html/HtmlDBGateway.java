package ru.curs.showcase.core.html;

import java.io.*;
import java.sql.SQLException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.ServerObjectCreateCloseException;

/**
 * Шлюз к БД для получения XForms, вебтеста и UI плагина из БД.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для XForm, вебтеста и плагина из БД")
public class HtmlDBGateway extends HTMLBasedElementQuery {

	private static final String NO_UPLOAD_PROC_ERROR =
		"Не задана процедура для загрузки файлов на сервер для linkId=";
	private static final String NO_DOWNLOAD_PROC_ERROR =
		"Не задана процедура для скачивания файлов из сервера для linkId=";

	private static final int XFORMSDATA_INDEX = 7;

	private static final int OUTPUT_INDEX = 8;

	private static final int ERROR_MES_INDEX_SAVE = 8;
	private static final int ERROR_MES_INDEX_SUBMISSION = 4;
	private static final int ERROR_MES_INDEX_FILE = 10;

	private static final int FILENAME_INDEX = 8;
	private static final int FILE_INDEX = 9;

	private static final int INPUTDATA_INDEX = 2;
	private static final int OUTPUTDATA_INDEX = 3;

	@Override
	public int getOutSettingsParam() {
		return OUTPUT_INDEX;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		switch (index) {
		case GET_DATA_TEMPALTE_IND:
		case SAVE_TEMPLATE_IND:
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?)}";
		case SUBMISSION_TEMPLATE_IND:
			return "{? = call %s (?, ?, ?)}";
		case DOWNLOAD_FILE_TEMPLATE_IND:
		case UPLOAD_FILE_TEMPLATE_IND:
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	@Override
	public int getDataParam() {
		return XFORMSDATA_INDEX;
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final ID linkId) {
		init(context, elementInfo);
		setTemplateIndex(DOWNLOAD_FILE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR + linkId);
		}
		setProcName(proc.getName());

		try (SPQuery query = this) {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(), context.getFormData());
				getStatement().registerOutParameter(FILENAME_INDEX, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(FILE_INDEX, getBinarySQLType());
				execute();
				OutputStreamDataFile result = getFileForBinaryStream(FILE_INDEX, FILENAME_INDEX);
				return result;
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	@Override
	public void uploadFile(final XFormContext context, final DataPanelElementInfo elementInfo,
			final ID linkId, final DataFile<InputStream> file) {
		init(context, elementInfo);
		setTemplateIndex(UPLOAD_FILE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_UPLOAD_PROC_ERROR + linkId);
		}
		setProcName(proc.getName());

		try (SPQuery query = this) {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(), context.getFormData());
				setStringParam(FILENAME_INDEX, file.getName());
				setBinaryStream(FILE_INDEX, file);
				execute();
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			} catch (IOException e2) {
				throw new ServerObjectCreateCloseException(e2);
			}
		}
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		switch (index) {
		case SAVE_TEMPLATE_IND:
			return ERROR_MES_INDEX_SAVE;
		case SUBMISSION_TEMPLATE_IND:
			return ERROR_MES_INDEX_SUBMISSION;
		case DOWNLOAD_FILE_TEMPLATE_IND:
		case UPLOAD_FILE_TEMPLATE_IND:
			return ERROR_MES_INDEX_FILE;
		default:
			return -1;
		}
	}

	@Override
	protected int getOutputSubmissionIndex() {
		return OUTPUTDATA_INDEX;
	}

	@Override
	protected int getInputSubmissionIndex() {
		return INPUTDATA_INDEX;
	}

}
