package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для получения сырых метаданных грида.
 * 
 * @author den
 * 
 */
public class GridSettingsSelector extends
		SourceSelector<ElementSettingsGateway> {

	public GridSettingsSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getMetadataProc().getName());
	}

	@Override
	public ElementSettingsGateway getGateway() {
		ElementSettingsGateway res;
		switch (sourceType()) {
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
				res = new ElementSettingsMSSQLExecGateway();
				break;
			case POSTGRESQL:
				res = new ElementSettingsPostgreSQLExecGateway();
				break;
			default:
				throw new NotImplementedYetException();
			}
			break;
		case SP:
			res = new ElementSettingsDBGateway();
			break;
		case JYTHON:
			res = new GridJythonSettingsGateway();
			break;
		case CELESTA:
			res = new GridCelestaSettingsGateway();
			break;
		default:
			throw new NotImplementedYetException();
		}
		return res;
	}
}
