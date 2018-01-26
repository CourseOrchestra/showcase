package ru.curs.showcase.core.grid.toolbar;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.SPQuery;

/**
 * Шлюз для получения данных, источником которых являются хранимые процедуры.
 * 
 * @author bogatov
 * 
 */
public class GridToolBarDBGateway extends SPQuery implements GridToolBarGateway {
	private static final int ELEMENTID_INDEX = 6;
	private static final int OTPUTDATA_INDEX = 7;

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?,?,?,?,?,?)}";
	}

	@Override
	public GridToolBarRawData getGridToolBarRawData(
			final CompositeContext context, final DataPanelElementInfo elInfo) {
		setProcName(elInfo.getToolBarProc().getName());
		setContext(context);
		try {
			try {
				prepareSQL();
				setupGeneralParameters();
				setStringParam(ELEMENTID_INDEX, elInfo.getId().getString());
				getStatement().registerOutParameter(OTPUTDATA_INDEX,
						java.sql.Types.SQLXML);
				execute();
				String data = getStringForXMLParam(OTPUTDATA_INDEX);
				return new GridToolBarRawData(data);
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		} finally {
			close();
		}
	}

}
