package ru.curs.showcase.core.external;

import java.util.Random;

import org.python.core.PyObject;

import ru.curs.celesta.CelestaException;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Celesta шлюз для трансформации данных или выполнения команд, полученных из
 * WebServices или сервлета.
 * 
 * @author anlug
 * 
 */
public class CelestaExternalCommandGateway implements ExternalCommandGateway {

	private String request;
	private String source;

	private class MyException extends BaseException {
		private static final long serialVersionUID = 6725288887082284411L;

		MyException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	@Override
	public String handle(final String aRequest, final String aSource) {
		request = aRequest;
		source = aSource;
		String result = null;

		String tempSesId = String.format("WebService%08X", (new Random()).nextInt());
		try {
			AppInfoSingleton.getAppInfo().getCelestaInstance().login(tempSesId, "userCelestaSid");
			AppInfoSingleton.getAppInfo().getSessionSidsMap().put(tempSesId, "userCelestaSid");
			PyObject pObj =
				AppInfoSingleton.getAppInfo().getCelestaInstance()
						.runPython(tempSesId, source, request);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(String.class)) {
				return (String) obj;
			}

		} catch (CelestaException e) {
			throw new MyException(ExceptionType.SOLUTION,
					"При запуске процедуры Celesta произошла ошибка: " + e.getMessage());

		} finally {
			try {
				AppInfoSingleton.getAppInfo().getCelestaInstance().logout(tempSesId, false);
				AppInfoSingleton.getAppInfo().getSessionSidsMap().remove(tempSesId);
			} catch (Exception e) {
				throw new MyException(ExceptionType.SOLUTION,
						"При запуске процедуры Celesta произошла ошибка: " + e.getMessage());
			}
		}

		return result;

	}
}
