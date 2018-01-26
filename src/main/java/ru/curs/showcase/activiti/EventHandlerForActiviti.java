package ru.curs.showcase.activiti;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.activiti.engine.delegate.event.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.slf4j.*;

import ru.curs.celesta.CelestaException;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * 
 * @author s.borodanev
 * 
 *         Класс-обработчик событий Activiti.
 * 
 */
public class EventHandlerForActiviti implements ActivitiEventListener {
	public static final String JYTHON_MARKER = "jython";
	private static final String JYTHON_ERROR =
		"При вызове Jython процедуры '%s' произошла ошибка: %s";

	private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerForActiviti.class);

	private class MyException extends BaseException {
		private static final long serialVersionUID = 6725288887082284411L;

		MyException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	public EventHandlerForActiviti() {
		LOGGER.info("Using Activiti Event Handler...");
	}

	@Override
	public void onEvent(final ActivitiEvent event) {
		LOGGER.info("Event received: " + event.getType());
		LOGGER.info("ExecutionId = " + event.getExecutionId());
		LOGGER.info("ProcessDefinitionId = " + event.getProcessDefinitionId());
		LOGGER.info("ProcessInstanceId = " + event.getProcessInstanceId());

		if (AppInfoSingleton.getAppInfo().getActivitiEventScriptDictionary().isEmpty()) {
			return;
		}

		for (ActivitiEventType aet : AppInfoSingleton.getAppInfo()
				.getActivitiEventScriptDictionary().keySet()) {
			if (aet == event.getType()) {
				List<String> procNameList =
					AppInfoSingleton.getAppInfo().getActivitiEventScriptDictionary().get(aet);

				if (!procNameList.isEmpty()) {
					for (String procName : procNameList) {
						if (procName.endsWith(".cl") || procName.endsWith(".celesta")) {
							LOGGER.info("Выполняется celesta-скрипт " + procName);
							handleCelestaScript(procName, event);
						} else if (procName.endsWith(".py")) {
							LOGGER.info("Выполняется jython-скрипт " + procName);
							handlePythonScript(procName, event);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

	private void handleCelestaScript(String procName, ActivitiEvent event) {
		final int i3 = 3;
		final int i8 = 8;
		if (procName.endsWith(".cl")) {
			procName = procName.substring(0, procName.length() - i3);

		}
		if (procName.endsWith(".celesta")) {
			procName = procName.substring(0, procName.length() - i8);
		}

		String tempSesId = String.format("Celesta%08X", (new Random()).nextInt());
		try {
			AppInfoSingleton.getAppInfo().getCelestaInstance().login(tempSesId, "super");
			PyObject pObj = AppInfoSingleton.getAppInfo().getCelestaInstance().runPython(tempSesId,
					procName, event);
			Object obj = pObj.__tojava__(Object.class);
		} catch (CelestaException e) {
			if (e.getMessage().contains("Traceback")) {
				int ind = e.getMessage().indexOf("Traceback");
				String ex = e.getMessage().substring(0, ind - 1).trim();
				throw new MyException(ExceptionType.SOLUTION,
						"При запуске процедуры Celesta произошла ошибка: " + ex);
			} else {
				throw new MyException(ExceptionType.SOLUTION,
						"При запуске процедуры Celesta произошла ошибка: " + e.getMessage());
			}

		} finally {
			try {
				AppInfoSingleton.getAppInfo().getCelestaInstance().logout(tempSesId, false);
			} catch (Exception e) {
				if (e.getMessage().contains("Traceback")) {
					int ind = e.getMessage().indexOf("Traceback");
					String ex = e.getMessage().substring(0, ind - 1).trim();
					throw new MyException(ExceptionType.SOLUTION,
							"При запуске процедуры Celesta произошла ошибка: " + ex);
				} else {
					throw new MyException(ExceptionType.SOLUTION,
							"При запуске процедуры Celesta произошла ошибка: " + e.getMessage());
				}
			}
		}
	}

	private void handlePythonScript(String procName, ActivitiEvent event) {
		PythonInterpreter interpreter = JythonIterpretatorFactory.getInstance().acquire();
		String parent = procName.replaceAll("([.]\\w+)$", "");
		parent = parent.replace('/', '.');
		boolean isLoaded = false;
		try {
			String className = TextUtils.extractFileName(procName);
			File script =
				new File(JythonIterpretatorFactory.getUserDataScriptDir() + "/" + procName);
			if (!script.exists()) {
				script =
					new File(JythonIterpretatorFactory.getGeneralScriptDir() + "/" + procName);
				if (!script.exists()) {
					throw new SettingsFileOpenException(procName, SettingsFileType.JYTHON);
				}
			}
			String cmd = String.format(
					"from org.python.core import codecs; codecs.setDefaultEncoding('utf-8'); from %s import %s",
					parent, className);
			try {
				setupJythonLogging(interpreter);
				interpreter.exec(cmd);
				isLoaded = true;

				interpreter.set("event", event);
				PyObject pyClass = interpreter.get(className);
				PyObject pyObj = pyClass.__call__();
			} catch (PyException e) {
				JythonWrongClassException exc =
					JythonWrongClassException.checkForImportError(e.toString(), className);
				if (exc != null) {
					throw exc;
				}

				String error = handleJythonException(e.value.toString());
				throw new JythonException(String.format(JYTHON_ERROR, procName, error), e);
			}
		} finally {
			if (isLoaded) {
				interpreter.exec("import sys; del sys.modules['" + parent + "']");
			}
			JythonIterpretatorFactory.getInstance().release(interpreter);
		}

	}

	private void setupJythonLogging(final PythonInterpreter interpreter) {
		interpreter.setOut(new Writer() {

			@Override
			public void write(final char[] data, final int offset, final int count)
					throws IOException {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					String value = String.valueOf(data, offset, count);
					if (!value.trim().isEmpty()) {
						Marker marker = MarkerFactory.getDetachedMarker(JYTHON_MARKER);
						LOGGER.info(marker, value);
					}
				}
			}

			@Override
			public void flush() throws IOException {
				// ничего не делаем
			}

			@Override
			public void close() throws IOException {
				// ничего не делаем
			}
		});
	}

	private String handleJythonException(final String value) {
		String error = StringEscapeUtils.unescapeJava(value);
		Pattern regex = Pattern.compile("^Exception\\(u'(.+)*',\\)$", Pattern.MULTILINE);
		Matcher regexMatcher = regex.matcher(error);
		if (regexMatcher.matches()) {
			return regexMatcher.group(1);
		}
		return error;
	}
}
