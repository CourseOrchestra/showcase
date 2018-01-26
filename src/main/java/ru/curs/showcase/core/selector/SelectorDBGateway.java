package ru.curs.showcase.core.selector;

import java.sql.*;
import java.util.ArrayList;

import oracle.jdbc.OracleTypes;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.runtime.*;

/**
 * Шлюз для получения данных, источником которых являются хранимые процедуры.
 * 
 * @author bogatov
 * 
 */
// TODO: Имеет смысл для реализации выполнения запросов к БД использовать
// класс SPQuery
public class SelectorDBGateway implements SelectorGateway {

	private static final String COLUMN_ID = "ID";

	private static final String COLUMN_NAME = "NAME";

	private static final String POSTGRESQL_TEMP_SCHEMA = "pg_temp";

	private static final int NUM1 = 1;
	private static final int NUM2 = 2;
	private static final int NUM3 = 3;
	private static final int NUM4 = 4;
	private static final int NUM5 = 5;
	private static final int NUM6 = 6;
	private static final int NUM7 = 7;
	private static final int NUM8 = 8;
	private static final int NUM9 = 9;
	private static final int NUM10 = 10;

	private static final int BY_ONE_PROC = 1;
	private static final int BY_TWO_PROC = 2;

	@Override
	public ResultSelectorData getData(final DataRequest req) throws Exception {

		String curValue = req.getCurValue();
		curValue = curValue.replace("&", "&amp;");
		curValue = curValue.replace("<", "&lt;");
		curValue = curValue.replace(">", "&gt;");
		req.setCurValue(curValue);

		if (req.getProcName().indexOf(Constants.PROCNAME_SEPARATOR) > -1) {
			String procCount = req.getProcName().substring(0,
					req.getProcName().indexOf(Constants.PROCNAME_SEPARATOR));

			String procList =
				req.getProcName().substring(req.getProcName().indexOf(Constants.PROCNAME_SEPARATOR)
						+ Constants.PROCNAME_SEPARATOR.length());

			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				procCount = "\"" + procCount + "\"";
				procList = "\"" + procList + "\"";
				if (!procCount.toLowerCase().contains(POSTGRESQL_TEMP_SCHEMA)) {
					procCount = procCount.replace(".", "\".\"");
				}
				if (!procList.toLowerCase().contains(POSTGRESQL_TEMP_SCHEMA)) {
					procList = procList.replace(".", "\".\"");
				}
			}

			return getDataByTwoProc(req, procCount, procList);
		} else {
			String procList = req.getProcName();
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				procList = "\"" + procList + "\"";
				if (!procList.toLowerCase().contains(POSTGRESQL_TEMP_SCHEMA)) {
					procList = procList.replace(".", "\".\"");
				}
			}
			return getDataByOneProc(req, procList);
		}
	}

	private ResultSelectorData getDataByTwoProc(final DataRequest req, final String procCount,
			final String procList) throws SQLException {
		Connection conn = ConnectionFactory.getInstance().acquire();
		// ЭТАП 1. Подсчёт общего количества записей
		String stmt = String.format("{call %s(?,?,?,?,?,?,?,?)}", procCount);
		CallableStatement cs = conn.prepareCall(stmt);
		try {
			int c;

			setupGeneralParameters(cs, req, true);
			cs.setString(NUM5, req.getParams());
			cs.setString(NUM6, req.getCurValue());
			cs.setBoolean(NUM7, req.isStartsWith());
			cs.registerOutParameter(NUM8, Types.INTEGER);
			cs.execute();
			AppInfoSingleton.getAppInfo().addExecutedProc(procCount);
			c = cs.getInt(NUM8);
			cs.close();

			// ЭТАП 2. Возврат записей.
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				conn.setAutoCommit(false);
			}
			stmt = String.format(getSqlTemplate(BY_TWO_PROC), procList);
			cs = conn.prepareCall(stmt);

			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				cs.registerOutParameter(NUM10, Types.OTHER);
			}
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				cs.registerOutParameter(NUM10, OracleTypes.CURSOR);
			}
			setupGeneralParameters(cs, req, false);
			cs.setString(getParamsIndex(), req.getParams());
			cs.setString(getCurValueIndex(), req.getCurValue());
			cs.setBoolean(getIsStartsWithIndex(), req.isStartsWith());
			cs.setInt(getFirstRecordIndex(), req.getFirstRecord());
			cs.setInt(getRecordCountIndex(), req.getRecordCount());

			ResultSet rs = getResultSet(cs);
			AppInfoSingleton.getAppInfo().addExecutedProc(procList);

			// Мы заранее примерно знаем размер, так что используем
			// ArrayList.
			ArrayList<DataRecord> l = new ArrayList<DataRecord>(req.getRecordCount());
			fillArrayListOfDataRecord(rs, l);
			return new ResultSelectorData(l, c);
		} finally {
			cs.close();
			ConnectionFactory.getInstance().release(conn);
		}
	}

	private ResultSelectorData getDataByOneProc(final DataRequest req,
			final String procListAndCount) throws SQLException {
		Connection conn = ConnectionFactory.getInstance().acquire();
		String stmt = String.format(getSqlTemplate(BY_ONE_PROC), procListAndCount);
		CallableStatement cs = conn.prepareCall(stmt);

		try {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				cs.registerOutParameter(NUM10, OracleTypes.CURSOR);
			}
			setupGeneralParameters(cs, req, false);
			cs.setString(getParamsIndex(), req.getParams());
			cs.setString(getCurValueIndex(), req.getCurValue());
			cs.setBoolean(getIsStartsWithIndex(), req.isStartsWith());
			cs.setInt(getFirstRecordIndex(), req.getFirstRecord());
			cs.setInt(getRecordCountIndex(), req.getRecordCount());
			cs.registerOutParameter(getCountAllRecordsIndex(), Types.INTEGER);

			ResultSet rs = getResultSet(cs);
			AppInfoSingleton.getAppInfo().addExecutedProc(procListAndCount);

			// Мы заранее примерно знаем размер, так что используем
			// ArrayList.
			ArrayList<DataRecord> l = new ArrayList<DataRecord>(req.getRecordCount());
			fillArrayListOfDataRecord(rs, l);

			int c = cs.getInt(getCountAllRecordsIndex());

			return new ResultSelectorData(l, c);
		} finally {
			cs.close();
			ConnectionFactory.getInstance().release(conn);
		}
	}

	private String getSqlTemplate(final int index) {
		if (index == BY_TWO_PROC) {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				return "{call %s(?,?,?,?,?,?,?,?,?)}";
			} else {
				return "{? = call %s(?,?,?,?,?,?,?,?,?)}";
			}
		} else {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				return "{call %s(?,?,?,?,?,?,?,?,?,?)}";
			} else {
				return "{? = call %s(?,?,?,?,?,?,?,?,?,?)}";
			}
		}
	}

	private ResultSet getResultSet(final CallableStatement cs) throws SQLException {
		cs.execute();
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return cs.getResultSet();
		} else {
			return (ResultSet) cs.getObject(NUM10);
		}
	}

	private void fillArrayListOfDataRecord(final ResultSet rs, final ArrayList<DataRecord> l)
			throws SQLException {
		ResultSetMetaData m = rs.getMetaData();
		int aliasId = -1;
		int aliasName = -1;
		for (int i = NUM1; i <= m.getColumnCount(); i++) {
			if (COLUMN_ID.equalsIgnoreCase(m.getColumnName(i))) {
				aliasId = i;
			}
			if (COLUMN_NAME.equalsIgnoreCase(m.getColumnName(i))) {
				aliasName = i;
			}
		}
		if ((aliasId == -1) || (aliasName == -1)) {
			aliasId = 1;
			aliasName = 2;
		}
		while (rs.next()) {
			DataRecord r = new DataRecord();
			r.setId(rs.getString(aliasId));

			String name = rs.getString(aliasName);
			name = name.replace("&lt;", "<");
			name = name.replace("&gt;", ">");
			name = name.replace("&amp;", "&");
			r.setName(name);

			for (int i = NUM1; i <= m.getColumnCount(); i++) {
				if ((i != aliasId) && (i != aliasName)) {
					r.addParameter(m.getColumnName(i), rs.getString(i));
				}
			}
			l.add(r);
		}
	}

	private int getMainContextIndex() {
		return NUM1;
	}

	private int getAdditionalContextIndex() {
		return NUM2;
	}

	private int getFilterContextIndex() {
		return NUM3;
	}

	private int getSessionContextIndex() {
		return NUM4;
	}

	private int getParamsIndex() {
		return NUM5;
	}

	private int getCurValueIndex() {
		return NUM6;
	}

	private int getIsStartsWithIndex() {
		return NUM7;
	}

	private int getFirstRecordIndex() {
		return NUM8;
	}

	private int getRecordCountIndex() {
		return NUM9;
	}

	private int getCountAllRecordsIndex() {
		return NUM10;
	}

	private void setStringParam(final CallableStatement cs, final int index, final String value)
			throws SQLException {
		cs.setString(index, value);
	}

	private void setSQLXMLParam(final CallableStatement cs, final int index, final String value)
			throws SQLException {
		String realValue = correctValueForXML(value);

		SQLXML sqlxml = cs.getConnection().createSQLXML();
		sqlxml.setString(realValue);
		cs.setSQLXML(index, sqlxml);
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

	private void setupGeneralParameters(final CallableStatement cs, final DataRequest req,
			final boolean isForRecordCount) throws SQLException {
		setStringParam(cs, getMainContextIndex(), "");
		setStringParam(cs, getAdditionalContextIndex(), "");
		setSQLXMLParam(cs, getFilterContextIndex(), "");
		setSQLXMLParam(cs, getSessionContextIndex(), "");

		CompositeContext context = (CompositeContext) req.getAddData().getContext();

		if (context.getMain() != null) {
			setStringParam(cs, getMainContextIndex(), context.getMain());
		}
		if (context.getAdditional() != null) {
			setStringParam(cs, getAdditionalContextIndex(), context.getAdditional());
		}
		if (context.getFilter() != null) {
			setSQLXMLParam(cs, getFilterContextIndex(), context.getFilter());
		}

		// DataPanelElementInfo elInfo = (DataPanelElementInfo)
		// req.getAddData().getElementInfo();
		// XMLSessionContextGenerator generator = new
		// XMLSessionContextGenerator(context, elInfo);
		// String sessionContext = generator.generate();
		// if (sessionContext != null) {
		// setSQLXMLParam(cs, getSessionContextIndex(), sessionContext);
		// }
		if (context.getSession() != null) {
			setSQLXMLParam(cs, getSessionContextIndex(), context.getSession());
		}

	}

}
