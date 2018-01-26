package ru.curs.showcase.core.plugin;

import java.sql.SQLException;

import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.sp.SPQuery;

/**
 * Шлюз для получения данных, источником которых являются хранимые процедуры.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginDBGateway extends SPQuery implements GetDataPluginGateway {
	private static final int PARAMS_INDEX = 6;
	private static final int OTPUTDATA_INDEX = 7;

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?,?,?,?,?,?)}";
	}

	@Override
	public ResultPluginData getData(final RequestData request) throws Exception {
		ResultPluginData result = new ResultPluginData();
		setProcName(request.getElInfo().getGetDataProcName());
		setContext(request.getContext());
		try {
			try {
				prepareSQL();
				setupGeneralParameters();
				setSQLXMLParam(PARAMS_INDEX, request.getXmlParams());
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
