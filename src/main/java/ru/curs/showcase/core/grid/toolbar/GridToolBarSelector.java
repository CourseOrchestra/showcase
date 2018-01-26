package ru.curs.showcase.core.grid.toolbar;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника получения данных для построения панели инструментов грида.
 * 
 * @author bogatov
 * 
 */
public class GridToolBarSelector extends SourceSelector<GridToolBarGateway> {

	public GridToolBarSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getToolBarProc().getName());
	}

	@Override
	public GridToolBarGateway getGateway() {
		switch (sourceType()) {
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
			case POSTGRESQL:
			default:
			}
		case SP:
			return new GridToolBarDBGateway();
		case JYTHON:
			return new GridToolBarJythonGateway();
		case CELESTA:
			return new GridToolBarCelestaGateway();
		default:
		}

		throw new NotImplementedYetException();
	}

}
