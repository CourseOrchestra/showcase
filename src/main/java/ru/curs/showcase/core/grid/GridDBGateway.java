package ru.curs.showcase.core.grid;

import java.io.InputStream;
import java.sql.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import oracle.jdbc.OracleTypes;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Шлюз к БД для грида.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для грида из БД")
public class GridDBGateway extends AbstractGridDBGateway {

	private static final String NO_DOWNLOAD_PROC_ERROR =
		"Не задана процедура для скачивания файлов из сервера для linkId=";

	private static final int SORTCOLS_INDEX = 7;
	private static final int OUT_SETTINGS_PARAM = 8;
	private static final int ERROR_MES_INDEX_DATA_AND_SETTINGS = 9;

	private static final int FIRST_RECORD_INDEX = 8;
	private static final int PAGE_SIZE_INDEX = 9;

	private static final int PARENT_ID = 10;

	private static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS = 10;

	private static final int RECORD_ID_INDEX = 7;
	private static final int FILENAME_INDEX = 8;
	private static final int FILE_INDEX = 9;
	private static final int ERROR_MES_INDEX_FILE_DOWNLOAD = 10;

	private static final int SAVE_DATA_INDEX = 7;
	private static final int OUT_SAVE_DATA_RESULT = 8;
	private static final int ERROR_MES_INDEX_SAVE_DATA = 9;

	private static final int ADD_RECORD_INDEX = 7;
	private static final int OUT_ADD_RECORD_RESULT = 8;
	private static final int ERROR_MES_INDEX_ADD_RECORD = 9;

	public GridDBGateway() {
		super();
	}

	public GridDBGateway(final Connection aConn) {
		super();
		setConn(aConn);
	}

	@Override
	protected int getSortColsIndex() {
		return SORTCOLS_INDEX;
	}

	@Override
	protected void prepareForGetDataAndSettings() throws SQLException {
		prepareElementStatementWithErrorMes();
		if (getContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
			setStringParam(PARENT_ID, getContext().getParentId());
		}
		getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS,
					OracleTypes.CURSOR);
		}
	}

	@Override
	public int getOutSettingsParam() {
		if (getContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
			return OUT_SETTINGS_PARAM + 1;
		} else {
			return OUT_SETTINGS_PARAM;
		}
	}

	@Override
	// CHECKSTYLE:OFF
	protected String getSqlTemplate(final int index) {
		switch (index) {
		case DATA_AND_SETTINS_QUERY:
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				if (getContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
					return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?, ? )}";
				} else {
					return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?)}";
				}
			}
		case DATA_ONLY_QUERY:
			if (getContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
				return "{? = call  %s (?, ?, ?, ?, ?, ?, ?, ?, ?)  }";
			} else {
				return "{? = call  %s (?, ?, ?, ?, ?, ?, ?, ?)  }";
			}
		case FILE_DOWNLOAD:
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		case SAVE_DATA:
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?)}";
		case ADD_RECORD:
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	// CHECKSTYLE:ON

	@Override
	protected void prepareForGetData() throws SQLException {
		prepareSQL();
		setupGeneralElementParameters();

		setupRange();

		if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
			getStatement().registerOutParameter(1, Types.OTHER);
		}
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			getStatement().registerOutParameter(1, OracleTypes.CURSOR);
		}
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
		switch (index) {
		case DATA_AND_SETTINS_QUERY:
			if (getContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
				return ERROR_MES_INDEX_DATA_AND_SETTINGS + 1;
			} else {
				return ERROR_MES_INDEX_DATA_AND_SETTINGS;
			}
		case FILE_DOWNLOAD:
			return ERROR_MES_INDEX_FILE_DOWNLOAD;
		case SAVE_DATA:
			return ERROR_MES_INDEX_SAVE_DATA;
		case ADD_RECORD:
			return ERROR_MES_INDEX_ADD_RECORD;
		default:
			return -1;
		}
	}

	@Override
	public OutputStreamDataFile downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final ID linkId, final String recordId) {
		init(context, elementInfo);
		setTemplateIndex(FILE_DOWNLOAD);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR + linkId);
		}
		setProcName(proc.getName());

		try (SPQuery query = this) {
			try {
				prepareElementStatementWithErrorMes();
				setStringParam(RECORD_ID_INDEX, recordId);
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
	public GridSaveResult saveData(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setTemplateIndex(SAVE_DATA);
		DataPanelElementProc proc = elementInfo.getProcByType(DataPanelElementProcType.SAVE);
		setProcName(proc.getName());

		try (SPQuery query = this) {
			try {
				prepareElementStatementWithErrorMes();

				String xml = null;
				try {
					xml = XMLJSONConverter.jsonToXml(context.getEditorData());
				} catch (JSONException | TransformerException | ParserConfigurationException e) {
					throw new XMLJSONConverterException(e);
				}
				if (xml.toLowerCase()
						.contains(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8.toLowerCase())) {
					xml = xml.substring(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8.length());
				}
				xml = xml.trim();

				setSQLXMLParam(SAVE_DATA_INDEX, xml);

				getStatement().registerOutParameter(OUT_SAVE_DATA_RESULT, java.sql.Types.SQLXML);

				execute();

				final GridSaveResult gridSaveResult = new GridSaveResult();
				gridSaveResult.setOkMessage(context.getOkMessage());

				InputStream is = getInputStreamForXMLParam(OUT_SAVE_DATA_RESULT);
				if (is != null) {
					SimpleSAX sax = new SimpleSAX(is, new DefaultHandler() {
						@Override
						public void startElement(final String namespaceURI, final String lname,
								final String qname, final Attributes attrs) {
							if ("properties".equalsIgnoreCase(qname)) {
								String s = attrs.getValue("refreshAfterSave");
								if (s != null) {
									gridSaveResult.setRefreshAfterSave(Boolean.valueOf(s));
								}
							}
						}
					}, "результаты сохранения данных грида");
					sax.parse();
				}

				return gridSaveResult;

			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}

	}

	@Override
	public GridAddRecordResult addRecord(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setTemplateIndex(ADD_RECORD);
		DataPanelElementProc proc = elementInfo.getProcByType(DataPanelElementProcType.ADDRECORD);
		setProcName(proc.getName());

		try (SPQuery query = this) {
			try {
				prepareElementStatementWithErrorMes();

				String xml = null;
				try {
					xml = XMLJSONConverter.jsonToXml(context.getAddRecordData());
				} catch (JSONException | TransformerException | ParserConfigurationException e) {
					throw new XMLJSONConverterException(e);
				}
				if (xml.toLowerCase()
						.contains(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8.toLowerCase())) {
					xml = xml.substring(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8.length());
				}
				xml = xml.trim();

				setSQLXMLParam(ADD_RECORD_INDEX, xml);

				getStatement().registerOutParameter(OUT_ADD_RECORD_RESULT, java.sql.Types.SQLXML);

				execute();

				GridAddRecordResult gridAddRecordResult = new GridAddRecordResult();
				gridAddRecordResult.setOkMessage(context.getOkMessage());

				InputStream is = getInputStreamForXMLParam(OUT_ADD_RECORD_RESULT);
				if (is != null) {
					SimpleSAX sax = new SimpleSAX(is, new DefaultHandler() {
						@Override
						public void startElement(final String namespaceURI, final String lname,
								final String qname, final Attributes attrs) {
						}
					}, "результаты добавления записи в гриде");
					sax.parse();
				}

				return gridAddRecordResult;

			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

}
