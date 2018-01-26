package ru.curs.showcase.core.external;

import ru.curs.showcase.core.jython.JythonQuery;

/**
 * Jython шлюз для трансформации данных или выполнения команд, полученных из
 * WebServices или сервлета.
 * 
 * @author den
 * 
 */
public class JythonExternalCommandGateway extends JythonQuery<String> implements ExternalCommandGateway {

	private String request;
	private String source;

	@Override
	public String handle(final String aRequest, final String aSource) {
		request = aRequest;
		source = aSource;
		runTemplateMethod();
		return getResult();
	}

	@Override
	protected Object execute() {
		return getProc().handle(request);
	}

	@Override
	protected String getJythonProcName() {
		return source;
	}

	public JythonExternalCommandGateway() {
		super(String.class);
	}

}
