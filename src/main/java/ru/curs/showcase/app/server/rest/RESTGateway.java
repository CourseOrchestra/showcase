package ru.curs.showcase.app.server.rest;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.celesta.CelestaException;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.core.jython.JythonProc;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * @author a.lugovtsov
 *
 */

public class RESTGateway {

	static private class ShowcaseRESTException extends BaseException {

		private static final long serialVersionUID = 6725288887092284411L;

		ShowcaseRESTException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	static public class ShowcaseRESTUnauthorizedException extends BaseException {

		private static final long serialVersionUID = 6725288887092284411L;

		ShowcaseRESTUnauthorizedException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	static public JythonRestResult executeRESTcommand(final String requestType,
			final String requestUrl, final String requestData, final String requestHeaders,
			final String urlParams, final String sesId, final String restProc,
			final String clientIP) {
		String correctedRESTProc = restProc.trim();
		final int tri = 3;
		final int vosem = 8;
		if (restProc.endsWith(".cl")) {
			correctedRESTProc = restProc.substring(0, restProc.length() - tri);
		}

		if (restProc.endsWith(".celesta")) {
			correctedRESTProc = restProc.substring(0, restProc.length() - vosem);
		}

		Boolean isRestWithCelestaAuthentication =
			("celesta".equals(UserDataUtils.getGeneralOptionalProp("rest.authentication.type"))) ? true
					: false;

		String tempSesId = isRestWithCelestaAuthentication ? sesId : "RESTful" + sesId;
		try {
			if (!isRestWithCelestaAuthentication) {
				AppInfoSingleton.getAppInfo().getCelestaInstance()
						.login(tempSesId, "userCelestaSid");
				AppInfoSingleton.getAppInfo().getSessionSidsMap().put(tempSesId, "userCelestaSid");
			}

			PyObject pObj =
				AppInfoSingleton
						.getAppInfo()
						.getCelestaInstance()
						.runPython(tempSesId, correctedRESTProc, requestType, requestUrl,
								requestData, requestHeaders, urlParams, clientIP);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(JythonRestResult.class)) {
				return (JythonRestResult) obj;
			}

		} catch (CelestaException e) {
			if (e.getMessage().contains("Session") & e.getMessage().contains("is not logged in"))
				throw new ShowcaseRESTUnauthorizedException(ExceptionType.SOLUTION,
						"При запуске процедуры Celesta для выполнения REST запроса произошла ошибка: "
								+ e.getMessage());

			throw new ShowcaseRESTException(ExceptionType.SOLUTION,
					"При запуске процедуры Celesta для выполнения REST запроса произошла ошибка: "
							+ e.getMessage());

		} finally {
			try {
				if (!isRestWithCelestaAuthentication) {
					AppInfoSingleton.getAppInfo().getCelestaInstance().logout(tempSesId, false);
					AppInfoSingleton.getAppInfo().getSessionSidsMap().remove(tempSesId);
				}
			} catch (Exception e) {
				throw new ShowcaseRESTException(ExceptionType.SOLUTION,
						"Пля выполнении REST запроса произошла ошибка при попытке выйти из сессии в celesta: "
								+ e.getMessage());
			}
		}

		return null;

	}

	static public JythonRestResult executeRESTcommandFromJythonProc(final String requestType,
			final String requestUrl, final String requestData, final String requestHeaders,
			final String urlParams, final String restProc, final String clientIP) {
		PythonInterpreter interpreter = JythonIterpretatorFactory.getInstance().acquire();
		String parent = restProc.replaceAll("([.]\\w+)$", "");
		parent = parent.replace('/', '.');
		boolean isLoaded = false;
		String className = TextUtils.extractFileName(restProc);
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
			JythonRestResult result =
				(JythonRestResult) proc.getRestResponcseData(requestType, requestUrl, requestData,
						requestHeaders, urlParams, clientIP);
			return result;

		} catch (PyException e) {
			throw new ShowcaseRESTException(ExceptionType.SOLUTION,
					"При запуске скрипта Jython для выполнения REST запроса произошла ошибка: "
							+ e.getMessage());
		} finally {
			if (isLoaded) {
				interpreter.exec("import sys; del sys.modules['" + parent + "']");
			}
			JythonIterpretatorFactory.getInstance().release(interpreter);
		}

	}

}
