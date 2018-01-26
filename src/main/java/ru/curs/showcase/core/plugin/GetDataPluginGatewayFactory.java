package ru.curs.showcase.core.plugin;

import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Фабрика создания источника получения данных для компонента селектор.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginGatewayFactory extends
		SourceSelector<GetDataPluginGateway> {

	public GetDataPluginGatewayFactory(final String procName) {
		super(procName);
	}

	@Override
	public GetDataPluginGateway getGateway() {
		GetDataPluginGateway result = null;
		switch (sourceType()) {
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
				break;
			case POSTGRESQL:
				break;
			default:
			}
			break;
		case SP:
			result = new GetDataPluginDBGateway();
			break;
		case JYTHON:
			result = new GetDataPluginJythonGateway();
			break;
		case CELESTA:
			result = new GetDataPluginCelestaGateway();
			break;
		default:
		}
		if (result == null) {
			throw new NotImplementedYetException();
		} else {
			return result;
		}

	}
}
