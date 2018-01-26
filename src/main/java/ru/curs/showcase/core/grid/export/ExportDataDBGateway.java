package ru.curs.showcase.core.grid.export;

import java.sql.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.runtime.ConnectionFactory;

/**
 * Шлюз для получения данных, источником которых являются хранимые процедуры.
 * 
 * @author bogatov
 * 
 */
public class ExportDataDBGateway extends SPQuery implements ExportDataGateway {

	@Override
	protected int getMainContextIndex() {
		return 1;
	}

	@Override
	protected void prepareSQL() throws SQLException {
		Connection conn = ConnectionFactory.getInstance().acquire();
		setConn(conn);
		setStatement(conn.prepareCall(getSqlText()));
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{call %s(?,?,?,?)}";
	}

	@Override
	public void getExportData(final CompositeContext context, final DataPanelElementInfo elInfo,
			final ResultSetHandler handler) {
		setProcName(elInfo.getExportDataProc().getName());
		setContext(context);
		try {
			try {
				prepareSQL();
				setupGeneralParameters();
				execute();
				ResultSet rs = getStatement().getResultSet();
				try {
					handler.onProcess(rs);
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} finally {
			close();
		}
	}
}
