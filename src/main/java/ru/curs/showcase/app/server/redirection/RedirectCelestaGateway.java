package ru.curs.showcase.app.server.redirection;

import org.python.core.PyObject;

import ru.curs.celesta.CelestaException;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.AppInfoSingleton;

public class RedirectCelestaGateway implements RedirectGateway {

	@Override
	public String getRedirectionUrlForLink(final String initialUrl, final String sesId,
			final String redirectionProc) {
		String correctedRedirectionProc = redirectionProc.trim();
		final int tri = 3;
		final int vosem = 8;
		if (redirectionProc.endsWith(".cl")) {
			correctedRedirectionProc =
				redirectionProc.substring(0, redirectionProc.length() - tri);
		}

		if (redirectionProc.endsWith(".celesta")) {
			correctedRedirectionProc =
				redirectionProc.substring(0, redirectionProc.length() - vosem);
		}

		try {
			PyObject pObj = AppInfoSingleton.getAppInfo().getCelestaInstance().runPython(sesId,
					correctedRedirectionProc, initialUrl);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(String.class)) {
				return (String) obj;
			}

		} catch (CelestaException e) {
			throw new RedirectException(ExceptionType.SOLUTION,
					"При запуске процедуры Celesta произошла ошибка: " + e.getMessage());

		}

		return null;

	}

	@Override
	public void close() {
	}
}
