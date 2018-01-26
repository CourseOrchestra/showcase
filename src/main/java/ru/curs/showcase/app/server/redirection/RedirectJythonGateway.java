package ru.curs.showcase.app.server.redirection;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.core.jython.JythonProc;
import ru.curs.showcase.runtime.JythonIterpretatorFactory;
import ru.curs.showcase.util.TextUtils;

public class RedirectJythonGateway implements RedirectGateway {

	@Override
	public String getRedirectionUrlForLink(final String initialUrl, final String sesId,
			final String redirectionProc) {
		PythonInterpreter interpreter = JythonIterpretatorFactory.getInstance().acquire();
		String parent = redirectionProc.replaceAll("([.]\\w+)$", "");
		parent = parent.replace('/', '.');
		boolean isLoaded = false;
		String className = TextUtils.extractFileName(redirectionProc);
		String cmd =
			String.format(
					"from org.python.core import codecs; codecs.setDefaultEncoding('utf-8'); from %s import %s",
					parent, className);

		try {
			interpreter.exec(cmd);
			isLoaded = true;

			PyObject pyClass = interpreter.get(className);
			PyObject pyObj = pyClass.__call__();
			JythonProc proc = (JythonProc) pyObj.__tojava__(JythonProc.class);
			String result = (String) proc.getRedirectURL(initialUrl);
			return result;

		} catch (PyException e) {
			throw new RedirectException(ExceptionType.SOLUTION,
					"При запуске скрипта Jython произошла ошибка: " + e.getMessage());

		} finally {
			if (isLoaded) {
				interpreter.exec("import sys; del sys.modules['" + parent + "']");
			}
			JythonIterpretatorFactory.getInstance().release(interpreter);
		}
	}

	@Override
	public void close() {
	}
}
