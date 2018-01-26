package ru.curs.showcase.core.plugin;

import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для получения сырых данных плагина.
 * 
 * @author bogatov
 * 
 */
public class PluginSelector extends SourceSelector<HTMLGateway> {
	private final RequestData requestData;

	public PluginSelector(final RequestData oRequestData) {
		super(oRequestData.getElInfo().getProcName());
		this.requestData = oRequestData;
	}

	@Override
	public HTMLGateway getGateway() {
		switch (sourceType()) {
		case JYTHON:
			return new PluginJythonGateway(requestData.getXmlParams());
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
				// return new HtmlMSSQLExecGateway();
			case POSTGRESQL:
				// return new HtmlPostgreSQLExecGateway();
			default:
			}
			break;
		case CELESTA:
			return new HTMLCelestaGateway();
		default:
			return new PluginDBGateway(requestData.getXmlParams());
		}
		throw new NotImplementedYetException();
	}
}
