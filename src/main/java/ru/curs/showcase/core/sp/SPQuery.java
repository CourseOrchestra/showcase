package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.transform.dom.DOMSource;

import org.slf4j.*;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Абстрактный класс, содержащий базовые константы и функции для вызова хранимых
 * процедур с данными.
 * 
 * @author den
 * 
 */
public abstract class SPQuery extends GeneralXMLHelper implements Closeable {

	protected static final String SCRIPTS_SQL_DIR = "scripts/sql/";

	protected static final String POSTGRESQL_TEMP_SCHEMA = "pg_temp";

	public static final String SQL_MARKER = "SQL";
	private static final int MAIN_CONTEXT_INDEX = 2;

	private static final int ERROR_MES_INDEX = -1;

	protected static final Logger LOGGER = LoggerFactory.getLogger(SPQuery.class);

	private final Map<Integer, Object> params = new TreeMap<>();
	/**
	 * Соединение с БД.
	 */
	private Connection conn = null;

	/**
	 * Интерфейс вызова хранимых процедур JDBC.
	 */
	private CallableStatement statement = null;

	/**
	 * Контекст вызова хранимой процедуры.
	 */
	private CompositeContext context = null;

	/**
	 * Имя хранимой процедуры, которую нужно вызвать.
	 */
	private String procName;

	/**
	 * Номер используемого шаблоны запроса. По умолчанию используется первый.
	 */
	private int templateIndex = 0;

	private boolean retriveResultSets = false;

	public boolean isRetriveResultSets() {
		return retriveResultSets;
	}

	public void setRetriveResultSets(final boolean aRetriveResultSets) {
		retriveResultSets = aRetriveResultSets;
	}

	public String getProcName() {
		return procName;
	}

	public void setProcName(final String aProcName) {
		procName = aProcName;
	}

	protected static final String SESSION_CONTEXT_PARAM = "session_context";

	private int getAddContextIndex() {
		return getMainContextIndex() + 1;
	}

	private int getFilterContextIndex() {
		return getMainContextIndex() + 2;
	}

	/**
	 * Функция для настройки общих параметров запроса: контекста и фильтров.
	 * 
	 * @throws SQLException
	 */
	protected void setupGeneralParameters() throws SQLException {
		setStringParam(getMainContextIndex(), "");
		setStringParam(getAddContextIndex(), "");
		setSQLXMLParam(getFilterContextIndex(), "");
		setSQLXMLParam(getSessionContextIndex(), "");
		if (context != null) {
			if (context.getMain() != null) {
				setStringParam(getMainContextIndex(), context.getMain());
			}
			if (context.getAdditional() != null) {
				setStringParam(getAddContextIndex(), context.getAdditional());
			}
			if (context.getFilter() != null) {
				setSQLXMLParam(getFilterContextIndex(), context.getFilter());
			}
			if (context.getSession() != null) {
				setSQLXMLParam(getSessionContextIndex(), context.getSession());
			}
		}
	}

	protected void setStringParam(final int index, final String value) throws SQLException {
		getStatement().setString(index, value);
		storeParamValue(index, value);
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(final Connection aConn) {
		conn = aConn;
	}

	public CallableStatement getStatement() {
		return statement;
	}

	public void setStatement(final CallableStatement aCs) {
		statement = aCs;
	}

	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}

	/**
	 * Возвращает шаблон для запуска SQL процедуры.
	 * 
	 * @param index
	 *            - номер шаблона.
	 */
	protected abstract String getSqlTemplate(final int index);

	/**
	 * Функция освобождения ресурсов, необходимых для создания объекта.
	 * 
	 */
	@Override
	public void close() {
		if (conn != null) {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new DBConnectException(e);
				}
			}
			ConnectionFactory.getInstance().release(conn);
			conn = null;
		}
	}

	/**
	 * Стандартный обработчик исключений, включающий в себя особую обработку
	 * SolutionDBException.
	 * 
	 * @param e
	 *            - исходное исключение.
	 */
	protected final BaseException dbExceptionHandler(final SQLException e) {
		if (UserMessageFactory.isExplicitRaised(e)) {
			UserMessageFactory factory = new UserMessageFactory();
			return new ValidateException(factory.build(e));
		} else {
			if (isStoredProc() && !checkProcExists()) {
				return new SPNotExistsException(getProcName(), getClass());
			}
			return new DBQueryException(e, getProcName(), getClass());
		}
	}

	private boolean isStoredProc() {
		return !procName.endsWith(".sql");
	}

	/**
	 * Проверяет наличие хранимой процедуры в БД.
	 */
	private boolean checkProcExists() {
		if ((ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL)
				&& (procName.toLowerCase().contains(POSTGRESQL_TEMP_SCHEMA))) {
			return true;
		}

		String fileName =
			String.format("%s/checkProcExists_%s.sql", UserDataUtils.SCRIPTSDIR, ConnectionFactory
					.getSQLServerType().toString().toLowerCase());

		String sql = "";
		try {
			sql = TextUtils.streamToString(FileUtils.loadClassPathResToStream(fileName));
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, fileName, SettingsFileType.SQLSCRIPT);
		}
		if (sql.trim().isEmpty()) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.SQLSCRIPT);
		}

		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			int index = procName.indexOf(".");
			if (index > -1) {
				String schema = procName.substring(0, index).trim();
				sql = sql.replace("[dbo]", "[" + schema + "]");
			}
		}

		String proc = procName;
		proc = proc.substring(proc.indexOf(".") + 1);

		sql = String.format(sql, proc);

		try {
			setStatement(conn.prepareCall(sql));
			ResultSet rs = getStatement().executeQuery();
			while (rs.next()) {
				return rs.getInt("num") > 0;
			}
		} catch (SQLException e) {
			return true;
		}
		return true;

	}

	/**
	 * Подготавливает SQL запрос.
	 */
	protected void prepareSQL() throws SQLException {
		if (conn == null) {
			conn = ConnectionFactory.getInstance().acquire();
		}
		setStatement(conn.prepareCall(getSqlText()));
		getStatement().registerOutParameter(1, java.sql.Types.INTEGER);
	}

	protected final String getSqlText() {
		if (getSqlTemplate(templateIndex).contains("%s")) {
			String proc = getProcName();
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				if (!proc.toLowerCase().contains(POSTGRESQL_TEMP_SCHEMA)) {
					proc = "\"" + proc + "\"";
					proc = proc.replace(".", "\".\""); // строка, которая
														// добавляет кавычки в
														// выражение, где
														// обозначается схема
														// например
														// "tasks.navigator"
														// заменяется на
														// "tasks"."navigator"

				}
			}
			return String.format(getSqlTemplate(templateIndex), proc);
		} else {
			return getSqlTemplate(templateIndex);
		}
	}

	/**
	 * Новая функция подготовки CallableStatement - с возвратом сообщения из БД.
	 */
	protected void prepareStatementWithErrorMes() throws SQLException {
		prepareSQL();
		getStatement().registerOutParameter(getErrorMesIndex(templateIndex),
				java.sql.Types.VARCHAR);
	}

	/**
	 * Функция проверки кода ошибки, который вернула процедура. Проверка
	 * происходит только в том случае, если первым параметром функции является
	 * код возврата. В MSSQL это происходит автоматически.
	 */
	public void checkErrorCode() {
		int errorCode;
		try {
			errorCode = getStatement().getInt(getReturnParamIndex());
		} catch (SQLException e) {
			// проверка через metadata почему-то глючит с MSSQL JDBC драйвером
			return;
		}
		if (errorCode != 0) {
			String errMess = "";
			if (getErrorMesIndex(templateIndex) != ERROR_MES_INDEX) {
				try {
					errMess = getStatement().getString(getErrorMesIndex(templateIndex));
				} catch (SQLException e) {
					errMess = "";
				}
			}

			UserMessageFactory factory = new UserMessageFactory();
			UserMessage um = factory.build(errorCode, errMess);
			if (um.getType() == MessageType.ERROR) {
				throw new ValidateException(um);
			} else {
				context.setOkMessage(um);
			}

		}
	}

	protected int getReturnParamIndex() {
		return 1;
	}

	public int getTemplateIndex() {
		return templateIndex;
	}

	public void setTemplateIndex(final int aTemplateIndex) {
		templateIndex = aTemplateIndex;
	}

	protected void setSQLXMLParam(final int index, final String value) throws SQLException {
		String realValue = correctValueForXML(value);

		SQLXML sqlxml = getConn().createSQLXML();
		sqlxml.setString(realValue);
		getStatement().setSQLXML(index, sqlxml);

		storeParamValue(index, realValue);
	}

	private void storeParamValue(final int index, final Object value) {
		if (LOGGER.isInfoEnabled() && AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			if (value instanceof String) {
				params.put(index, value);
			} else if (value instanceof InputStream) {
				try {
					params.put(index, TextUtils.streamToString((InputStream) value));
				} catch (IOException e) {
					throw new ServerLogicError(e);
				}
			} else if (value instanceof Integer) {
				params.put(index, value);
			}
		}
	}

	private String correctValueForXML(final String value) {
		String realValue = value;
		if (realValue == null) {
			if (ConnectionFactory.getSQLServerType() != SQLServerType.POSTGRESQL) {
				realValue = "";
			}
		} else {
			if (realValue.isEmpty()
					&& ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				realValue = null;
			}
		}
		return realValue;
	}

	protected int getMainContextIndex() {
		return MAIN_CONTEXT_INDEX;
	}

	private int getSessionContextIndex() {
		return getMainContextIndex() + 2 + 1;
	}

	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

	protected boolean execute() throws SQLException {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			String value = SQLUtils.addParamsToSQLTemplate(getSqlText(), params);
			Marker marker = MarkerFactory.getDetachedMarker(SQL_MARKER);
			marker.add(HandlingDirection.INPUT.getMarker());
			LOGGER.info(marker, value);
		}
		boolean res = getStatement().execute();
		AppInfoSingleton.getAppInfo().addExecutedProc(getProcName());
		if (!retriveResultSets) {
			checkErrorCode();
		}
		return res;
	}

	protected void setBinaryStream(final int parameterIndex, final DataFile<InputStream> file)
			throws SQLException, IOException {

		StreamConvertor sc = new StreamConvertor(file.getData());
		InputStream copyData = sc.getCopy();
		file.setData(sc.getCopy());

		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			getStatement().setBinaryStream(parameterIndex, file.getData());
		} else {
			StreamConvertor dup = new StreamConvertor(file.getData());
			ByteArrayOutputStream os = dup.getOutputStream();
			getStatement().setBytes(parameterIndex, os.toByteArray());
		}
		if (file.isTextFile()) {
			storeParamValue(parameterIndex, copyData);
		}
	}

	protected void setIntParam(final int index, final int value) throws SQLException {
		getStatement().setInt(index, value);
		storeParamValue(index, value);
	}

	protected InputStream getInputStreamForXMLParam(final int index) throws SQLException {
		SQLXML sqlXml = getStatement().getSQLXML(index);
		if (sqlXml != null) {
			InputStream is = sqlXml.getBinaryStream();
			is = logOutputXMLStream(is);
			return is;
		}
		return null;
	}

	protected Document getDocumentForXMLParam(final int index) throws SQLException {
		SQLXML sqlXml = getStatement().getSQLXML(index);
		if (sqlXml != null) {
			DOMSource domSource = sqlXml.getSource(DOMSource.class);
			Document doc = (Document) domSource.getNode();
			logOutputXMLDocument(doc);
			return doc;
		}
		return null;
	}

	protected String getStringForXMLParam(final int index) throws SQLException {
		SQLXML sqlXml = getStatement().getSQLXML(index);
		if (sqlXml != null) {
			String result = sqlXml.getString();
			logOutputXMLString(result);
			return result;
		}
		return null;
	}

	private InputStream logOutputXMLStream(final InputStream is) {
		if (LOGGER.isInfoEnabled() && AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			try {
				StreamConvertor convertor = new StreamConvertor(is);
				String value = XMLUtils.streamToString(convertor.getCopy());
				logOutputXMLString(value);
				return convertor.getCopy();
			} catch (IOException e) {
				throw new ServerObjectCreateCloseException(e);
			}
		}
		return is;
	}

	private InputStream logOutputTextStream(final InputStream is) {
		if (LOGGER.isInfoEnabled() && AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			try {
				StreamConvertor convertor = new StreamConvertor(is);
				String value =
					StreamConvertor.inputToOutputStream(is).toString(TextUtils.DEF_ENCODING);
				logOutputXMLString(value);
				return convertor.getCopy();
			} catch (IOException e) {
				throw new ServerObjectCreateCloseException(e);
			}
		}
		return is;
	}

	private void logOutputXMLDocument(final Document doc) {
		if (LOGGER.isInfoEnabled() && AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			String value = XMLUtils.documentToString(doc);
			logOutputXMLString(value);
		}
	}

	private void logOutputXMLString(final String value) {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			Marker marker = MarkerFactory.getDetachedMarker(SQL_MARKER);
			marker.add(HandlingDirection.OUTPUT.getMarker());
			LOGGER.info(marker, value);
		}
	}

	protected OutputStreamDataFile
			getFileForBinaryStream(final int dataIndex, final int nameIndex) throws SQLException {
		InputStream is = getBinaryStream(dataIndex);
		if (is == null) {
			throw new FileIsAbsentInDBException();
		}
		String fileName = getStatement().getString(nameIndex);
		StreamConvertor dup;
		try {
			dup = new StreamConvertor(is);
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		ByteArrayOutputStream os = dup.getOutputStream();
		OutputStreamDataFile result = new OutputStreamDataFile(os, fileName);
		result.setEncoding(TextUtils.JDBC_ENCODING);
		if (result.isXMLFile()) {
			logOutputXMLStream(dup.getCopy());
		} else if (result.isTextFile()) {
			logOutputTextStream(dup.getCopy());
		}
		return result;
	}

	/**
	 * Функция getBytes подходит и для BINARY, и для BLOB.
	 * 
	 * @param dataIndex
	 * @return
	 * @throws SQLException
	 */
	private InputStream getBinaryStream(final int dataIndex) throws SQLException {
		byte[] bt = getStatement().getBytes(dataIndex);
		if (bt == null) {
			return null;
		}
		InputStream is = new ByteArrayInputStream(bt);
		return is;
	}

	protected int getBinarySQLType() {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return java.sql.Types.BLOB;
		} else {
			return java.sql.Types.BINARY;
		}
	}

}