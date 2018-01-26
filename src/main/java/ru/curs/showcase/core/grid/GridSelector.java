package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для получения сырых данных для построения грида.
 * 
 * @author den
 * 
 */
public class GridSelector extends SourceSelector<GridGateway> {

	public GridSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getProcName());
	}

	public GridSelector(final String procName) {
		super(procName);
	}

	@Override
	public GridGateway getGateway() {
		GridGateway res;
		switch (sourceType()) {
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
				res = new GridMSSQLExecGateway();
				break;
			case POSTGRESQL:
				res = new GridPostgreSQLExecGateway();
				break;
			default:
				throw new NotImplementedYetException();
			}
			break;
		case SP:
			res = new GridDBGateway();
			break;
		case JYTHON:
			res = new GridJythonGateway();
			break;
		case CELESTA:
			res = new GridCelestaGateway();
			break;
		default:
			throw new NotImplementedYetException();
		}
		return res;
	}
}
