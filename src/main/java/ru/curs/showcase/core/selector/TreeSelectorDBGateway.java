package ru.curs.showcase.core.selector;

import java.sql.SQLException;

import ru.curs.showcase.app.api.selector.TreeDataRequest;
import ru.curs.showcase.core.sp.SPQuery;

/**
 * Шлюз для получения данных, источником которых являются хранимые процедуры.
 * 
 */
public class TreeSelectorDBGateway extends SPQuery implements TreeSelectorGateway {
	// private static final int PARAMS_INDEX = 6;
	private static final int OTPUTDATA_INDEX = 7;

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?,?,?,?,?,?)}";
	}

	@Override
	public ResultTreeSelectorData getData(final TreeDataRequest request) throws Exception {
		ResultTreeSelectorData result = new ResultTreeSelectorData();
		setProcName(request.getElInfo().getGetDataProcName());
		setContext(request.getContext());
		try {
			try {
				prepareSQL();
				setupGeneralParameters();
				// setSQLXMLParam(PARAMS_INDEX, request.getXmlParams());
				getStatement().registerOutParameter(OTPUTDATA_INDEX, java.sql.Types.SQLXML);
				execute();
				String data = getStringForXMLParam(OTPUTDATA_INDEX);
				result.setData(data);
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		} finally {
			close();
		}
		return result;
	}

}
