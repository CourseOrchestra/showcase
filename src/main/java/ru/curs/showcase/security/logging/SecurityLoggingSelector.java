package ru.curs.showcase.security.logging;

import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для логирования события.
 * 
 * @author bogatov
 * 
 */
public class SecurityLoggingSelector extends SourceSelector<SecurityLoggingGateway> {

	public SecurityLoggingSelector(final String name) {
		super(name);
	}

	@Override
	public SecurityLoggingGateway getGateway() {
		switch (sourceType()) {
		case SQL:
			switch (ConnectionFactory.getSQLServerType()) {
			case MSSQL:
			case POSTGRESQL:
			default:
			}
		case SP:
			return new SecurityLoggingDBGateway(getSourceName());
		case JYTHON:
			return new SecurityLoggingJythonGateway(getSourceName());
		case CELESTA:
			return new SecurityLoggingCelestaGateway(getSourceName());
		default:
		}

		throw new NotImplementedYetException();
	}
}
