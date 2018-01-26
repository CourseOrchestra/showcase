package ru.curs.showcase.security.logging;

import ru.curs.showcase.core.jython.JythonQuery;

/**
 * Шлюз для обработки события с использованием jython скрипта.
 * 
 * @author bogatov
 * 
 */
public class SecurityLoggingJythonGateway extends JythonQuery<Void> implements
		SecurityLoggingGateway {
	private final String procName;
	private Event event;

	protected SecurityLoggingJythonGateway(final String sProcName) {
		super(Void.class);
		this.procName = sProcName;
	}

	@Override
	public void doLogging(final Event oEvent) throws Exception {
		event = oEvent;
		runTemplateMethod();
	}

	@Override
	protected Object execute() {
		return getProc().logging(event.getContext(), event.getXml(),
				event.getTypeEvent().toString());
	}

	@Override
	protected String getJythonProcName() {
		return this.procName;
	}

}
