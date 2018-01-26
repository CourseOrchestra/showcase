package ru.curs.showcase.core.selector;

import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Фабрика создания источника получения данных для нового триселектора.
 * 
 */
public class TreeSelectorGatewayFactory extends SourceSelector<TreeSelectorGateway> {

	public TreeSelectorGatewayFactory(final String procName) {
		super(procName);
	}

	@Override
	public TreeSelectorGateway getGateway() {
		TreeSelectorGateway result = null;
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
			result = new TreeSelectorDBGateway();
			break;
		case JYTHON:
			result = new TreeSelectorJythonGateway();
			break;
		case CELESTA:
			result = new TreeSelectorCelestaGateway();
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
