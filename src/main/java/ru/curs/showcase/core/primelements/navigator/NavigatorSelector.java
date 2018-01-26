package ru.curs.showcase.core.primelements.navigator;

import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.*;

/**
 * Селектор для источника данных о навигаторе.
 * 
 * @author den
 * 
 */
public class NavigatorSelector extends SourceSelector<PrimElementsGateway> {
	public static final String NAVIGATOR_PROCNAME_PARAM = "navigator.proc.name";

	public NavigatorSelector() {
		super(UserDataUtils.getRequiredProp(NAVIGATOR_PROCNAME_PARAM));
	}

	@Override
	public PrimElementsGateway getGateway() {
		PrimElementsGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new PrimElementsJythonGateway();
			break;
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
				res = new NavigatorMSSQLExecGateway();
				break;
			case POSTGRESQL:
				res = new NavigatorPostgreSQLExecGateway();
				break;
			default:
				throw new NotImplementedYetException();
			}
			break;
		case FILE:
			res = new PrimElementsFileGateway(SettingsFileType.NAVIGATOR);
			break;
		case CELESTA:
			res = new NavigatorCelestaGataway();
			break;
		default:
			res = new NavigatorDBGateway();
		}
		res.setSourceName(getSourceName());
		return res;
	}
}
