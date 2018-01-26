package ru.curs.showcase.core.selector;

import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Фабрика создания источника получения данных для компонента селектор.
 * 
 * @author bogatov
 * 
 */
public class SelectorGatewayFactory extends SourceSelector<SelectorGateway> {

	public SelectorGatewayFactory(final String procName) {
		super(procName);
	}

	@Override
	public SelectorGateway getGateway() {
		SelectorGateway result = null;
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
			result = new SelectorDBGateway();
			break;
		case JYTHON:
			result = new SelectorJythonGateway();
			break;
		case CELESTA:
			result = new SelectorCelestaGateway();
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
