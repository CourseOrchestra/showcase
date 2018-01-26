package ru.curs.showcase.app.server.redirection;

import ru.curs.showcase.core.SourceSelector;

public class RedirectSelector extends SourceSelector<RedirectGateway> {

	@Override
	public RedirectGateway getGateway() {
		RedirectGateway gateway = null;
		switch (sourceType()) {
		case JYTHON:
			gateway = new RedirectJythonGateway();
			break;
		case CELESTA:
			gateway = new RedirectCelestaGateway();
			break;
		default:
			break;
		}
		return gateway;
	}

}
