package ru.curs.showcase.app.server.redirection;

import java.io.Closeable;

public interface RedirectGateway extends Closeable {

	public String getRedirectionUrlForLink(final String initialUrl, final String sesId,
			final String redirectionProc);

	@Override
	void close();

}
