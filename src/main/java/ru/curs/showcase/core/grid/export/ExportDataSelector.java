package ru.curs.showcase.core.grid.export;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника выгрузки данных в Excel.
 * 
 * @author bogatov
 * 
 */
public class ExportDataSelector extends SourceSelector<ExportDataGateway> {

	public ExportDataSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getExportDataProc().getName());
	}

	@Override
	public ExportDataGateway getGateway() {
		switch (sourceType()) {
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
			case POSTGRESQL:
			default:
			}
		case SP:
			return new ExportDataDBGateway();
		case JYTHON:
		default:
		}

		throw new NotImplementedYetException();
	}

}
