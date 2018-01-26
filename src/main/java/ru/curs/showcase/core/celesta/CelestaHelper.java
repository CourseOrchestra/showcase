package ru.curs.showcase.core.celesta;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.python.core.PyObject;
import org.xml.sax.SAXException;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.LyraGridContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Класс помощник работы с Celesta.
 * 
 * @author bogatov
 * 
 * @param <T>
 *            тип возвращаемого результата.
 */

public class CelestaHelper<T> {
	private static final int LENGTH_GENERAL_PARAMS = 4;
	private static final int MAIN_CONTEXT_INDEX = 0;
	private static final int ADD_CONTEXT_INDEX = 1;
	private static final int FILTER_CONTEXT_INDEX = 2;
	private static final int SESSION_CONTEXT_INDEX = 3;

	private static final String LYRAPLAYER_GET_FORM_INSTANCE =
		"lyra.lyraplayer.getFormInstance.cl";

	private final CompositeContext contex;
	private final Class<T> resultType;

	/**
	 * 
	 * @param oContex
	 *            контекст.
	 * @param aResultType
	 *            тип возвращаемого результата.
	 */
	public CelestaHelper(final CompositeContext oContex, final Class<T> aResultType) {
		super();
		this.contex = oContex;
		this.resultType = aResultType;
	}

	/**
	 * Класс-получатель сообщений из Челесты.
	 * 
	 */
	private class Receiver implements CelestaMessage.MessageReceiver {
		private CelestaMessage msg;

		@Override
		public void receive(final CelestaMessage aMsg) {
			msg = aMsg;
		}
	}

	private UserMessage getUserMessage(final Receiver receiver) {
		UserMessage um = null;

		CelestaMessage cm = receiver.msg;
		if (cm != null) {
			String mess = cm.getMessage();

			um = new UserMessage(mess, mess, MessageType.INFO, cm.getCaption(), cm.getSubkind());
			UserMessageFactory factory = new UserMessageFactory();
			um = factory.build(um);

			switch (cm.getKind()) {
			case CelestaMessage.WARNING:
				um.setType(MessageType.WARNING);
				break;
			case CelestaMessage.ERROR:
				um.setType(MessageType.ERROR);
				break;
			default:
				break;
			}
		}

		return um;
	}

	/**
	 * Выполнить celesta jython скрипт.
	 * 
	 * @param sProcName
	 *            - имя процедуры, полученной от элемента управления
	 * @return результат выполнения скрипта
	 */
	public T runPython(final String sProcName, final Object... additionalParams) {
		Object[] params = mergeAddAndGeneralParameters(this.contex, additionalParams);
		// String userSID = SessionUtils.getCurrentUserSID();
		String sesID = SessionUtils.getCurrentSessionId();
		String procName = CelestaUtils.getRealProcName(sProcName);
		PyObject result;

		if (!AppInfoSingleton.getAppInfo().getIsCelestaInitialized()) {
			// AppInfoSingleton.getAppInfo().getCelestaInitializationException().logAll(e);
			throw new CelestaWorkerException(
					"Ошибка при запуске jython скрипта celesta '" + procName
							+ "'. Celesta при старте сервера не была инициализированна.",
					AppInfoSingleton.getAppInfo().getCelestaInitializationException());
		}

		boolean messageDone = false;
		Receiver receiver = new Receiver();
		try {

			String elementId = null;
			if (additionalParams != null && additionalParams.length > 0) {
				elementId = additionalParams[0].toString();
			}
			ShowcaseContext sc = generateShowcaseContext(this.contex, elementId);
			result = AppInfoSingleton.getAppInfo().getCelestaInstance().runPython(sesID, receiver,
					sc, procName, params);

			UserMessage um = getUserMessage(receiver);
			if (um != null) {
				messageDone = true;

				if (um.getType() == MessageType.ERROR) {
					throw new ValidateException(um);
				} else {
					contex.setOkMessage(um);
				}
			}

		} catch (CelestaException ex) {

			UserMessage um = getUserMessage(receiver);
			if ((um != null) && (um.getType() == MessageType.ERROR)) {
				messageDone = true;

				throw new ValidateException(um);
			}

			String[] err = handleCelestaExceptionError(ex.getMessage());
			String res = handleCelestaExceptionTraceback(ex.getMessage(), err);

			throw new CelestaWorkerException(
					"Ошибка при выполнении jython скрипта celesta '" + procName + "'",
					// ex);
					new Exception(res));
		}
		if (result == null) {
			return null;
		}
		Object obj = result.__tojava__(Object.class);
		if (obj == null) {
			return null;
		}
		if ((!messageDone) && (obj instanceof UserMessage)) {

			UserMessage um = UserMessage.class.cast(obj);
			UserMessageFactory factory = new UserMessageFactory();
			um = factory.build(um);
			if (um.getType() == MessageType.ERROR) {
				throw new ValidateException(um);
			} else {
				contex.setOkMessage(um);
				return null;
			}

		}
		if (obj.getClass().isAssignableFrom(resultType)) {

			if ((!messageDone) && (obj instanceof JythonDTO)) {
				contex.setOkMessage(((JythonDTO) obj).getUserMessage());
			}

			return resultType.cast(obj);
		} else {
			throw new CelestaWorkerException(
					"Result is not instance of " + this.resultType.getName());
		}

	}

	/**
	 * Выполнить celesta jython скрипт. ID сессии выступает в качестве аргумента
	 * метода.
	 * 
	 * @param sesID
	 *            - ID сессии
	 * @param sProcName
	 *            - имя процедуры, полученной от элемента управления
	 * @return результат выполнения скрипта
	 */
	public T runPythonWithSessionSet(final String sesID, final String sProcName,
			final Object... additionalParams) {
		Object[] params = mergeAddAndGeneralParameters(this.contex, additionalParams);
		// String userSID = SessionUtils.getCurrentUserSID();
		String procName = CelestaUtils.getRealProcName(sProcName);
		PyObject result;

		if (!AppInfoSingleton.getAppInfo().getIsCelestaInitialized()) {
			// AppInfoSingleton.getAppInfo().getCelestaInitializationException().logAll(e);
			throw new CelestaWorkerException(
					"Ошибка при запуске jython скрипта celesta '" + procName
							+ "'. Celesta при старте сервера не была инициализированна.",
					AppInfoSingleton.getAppInfo().getCelestaInitializationException());
		}

		boolean messageDone = false;
		Receiver receiver = new Receiver();
		try {

			String elementId = null;
			if (additionalParams != null && additionalParams.length > 0) {
				elementId = additionalParams[0].toString();
			}
			ShowcaseContext sc = generateShowcaseContext(this.contex, elementId);
			result = AppInfoSingleton.getAppInfo().getCelestaInstance().runPython(sesID, receiver,
					sc, procName, params);

			UserMessage um = getUserMessage(receiver);
			if (um != null) {
				messageDone = true;

				if (um.getType() == MessageType.ERROR) {
					throw new ValidateException(um);
				} else {
					contex.setOkMessage(um);
				}
			}

		} catch (CelestaException ex) {

			UserMessage um = getUserMessage(receiver);
			if (um != null) {
				messageDone = true;

				throw new ValidateException(um);
			}

			String[] err = handleCelestaExceptionError(ex.getMessage());
			String res = handleCelestaExceptionTraceback(ex.getMessage(), err);

			throw new CelestaWorkerException(
					"Ошибка при выполнении jython скрипта celesta '" + procName + "'",
					// ex);
					new Exception(res));
		}
		if (result == null) {
			return null;
		}
		Object obj = result.__tojava__(Object.class);
		if (obj == null) {
			return null;
		}
		if ((!messageDone) && (obj instanceof UserMessage)) {

			UserMessage um = UserMessage.class.cast(obj);
			UserMessageFactory factory = new UserMessageFactory();
			um = factory.build(um);
			if (um.getType() == MessageType.ERROR) {
				throw new ValidateException(um);
			} else {
				contex.setOkMessage(um);
				return null;
			}

		}
		if (obj.getClass().isAssignableFrom(resultType)) {

			if ((!messageDone) && (obj instanceof JythonDTO)) {
				contex.setOkMessage(((JythonDTO) obj).getUserMessage());
			}

			return resultType.cast(obj);
		} else {
			throw new CelestaWorkerException(
					"Result is not instance of " + this.resultType.getName());
		}

	}

	/**
	 * Выполнить celesta jython скрипт для лиры.
	 * 
	 * @param lyraClass
	 *            - квалифицированное имя класса
	 * @return результат выполнения скрипта
	 */
	public T runLyraPython(final String lyraClass, final Object... additionalParams) {

		String elementId = additionalParams[0].toString();
		ShowcaseContext sc = generateShowcaseContext(this.contex, elementId);
		sc.setOrderBy(((LyraGridContext) this.contex).getOrderBy());

		Object[] params = new Object[2];
		params[0] = lyraClass;
		params[1] = elementId;

		String sesID;
		if (additionalParams.length == 2) {
			sesID = (String) additionalParams[1];
		} else {
			sesID = SessionUtils.getCurrentSessionId();
		}

		String procName = CelestaUtils.getRealProcName(LYRAPLAYER_GET_FORM_INSTANCE);
		PyObject result;

		if (!AppInfoSingleton.getAppInfo().getIsCelestaInitialized()) {
			throw new CelestaWorkerException(
					"Ошибка при запуске jython скрипта celesta '" + procName
							+ "'. Celesta при старте сервера не была инициализированна.",
					AppInfoSingleton.getAppInfo().getCelestaInitializationException());
		}

		Receiver receiver = new Receiver();
		try {
			result = AppInfoSingleton.getAppInfo().getCelestaInstance().runPython(sesID, receiver,
					sc, procName, params);

			UserMessage um = getUserMessage(receiver);
			if (um != null) {
				if (um.getType() == MessageType.ERROR) {
					throw new ValidateException(um);
				} else {
					contex.setOkMessage(um);
				}
			}

		} catch (CelestaException ex) {

			UserMessage um = getUserMessage(receiver);
			if (um != null) {
				throw new ValidateException(um);
			}

			String[] err = handleCelestaExceptionError(ex.getMessage());
			String res = handleCelestaExceptionTraceback(ex.getMessage(), err);

			throw new CelestaWorkerException(
					"Ошибка при выполнении jython скрипта celesta '" + procName + "'",
					new Exception(res));
		}
		if (result == null) {
			return null;
		}

		Object obj = result.__tojava__(Object.class);
		if (obj == null) {
			return null;
		}

		try {
			return resultType.cast(obj);
		} catch (Exception e) {
			throw new CelestaWorkerException(
					"Result is not instance of " + this.resultType.getName());
		}

	}

	protected Object[] mergeAddAndGeneralParameters(final CompositeContext context,
			final Object[] additionalParams) {
		Object[] resultParams;
		if (additionalParams != null && additionalParams.length > 0) {
			resultParams = new Object[additionalParams.length + LENGTH_GENERAL_PARAMS];
			System.arraycopy(additionalParams, 0, resultParams, LENGTH_GENERAL_PARAMS,
					additionalParams.length);
		} else {
			resultParams = new Object[LENGTH_GENERAL_PARAMS];
		}
		resultParams[MAIN_CONTEXT_INDEX] = context.getMain();
		resultParams[ADD_CONTEXT_INDEX] = context.getAdditional();
		// resultParams[FILTER_CONTEXT_INDEX] =
		// XMLUtils.convertXmlToJson(context.getFilter());
		// resultParams[SESSION_CONTEXT_INDEX] =
		// XMLUtils.convertXmlToJson(context.getSession());
		try {
			resultParams[FILTER_CONTEXT_INDEX] = XMLJSONConverter.xmlToJson(context.getFilter());
			resultParams[SESSION_CONTEXT_INDEX] = XMLJSONConverter.xmlToJson(
					XMLUtils.xmlServiceSymbolsToNormalWithoutDoubleQuotes(context.getSession()));
		} catch (SAXException | IOException e) {
			throw new XMLJSONConverterException(e);
		}

		return resultParams;
	}

	protected ShowcaseContext generateShowcaseContext(final CompositeContext context,
			final String elementId) {
		ShowcaseContext sc = null;
		String fltr_context = null;
		String ses_context = null;

		try {
			fltr_context = XMLJSONConverter.xmlToJson(context.getFilter());
			ses_context = XMLJSONConverter.xmlToJson(
					XMLUtils.xmlServiceSymbolsToNormalWithoutDoubleQuotes(context.getSession()));
		} catch (SAXException | IOException e) {
			throw new XMLJSONConverterException(e);
		}

		sc = new ShowcaseContext(context.getMain(), context.getAdditional(), fltr_context,
				ses_context, elementId);

		return sc;

	}

	private String[] handleCelestaExceptionError(final String value) {
		String[] arr = { "", "", "", "", "", "", "" };
		String error = "";
		// Pattern regex = Pattern.compile("^((\n|.)+u'(.+)'(\n|.)*)$");
		// Matcher regexMatcher = regex.matcher(value);
		// if (regexMatcher.matches()) {
		// error = StringEscapeUtils.unescapeJava(regexMatcher.group(4));
		// }
		// return error;
		if (value.contains("(u'''") && value.contains("''')")) {
			int ind1;
			int ind2;
			if (value.contains("',)")) {
				ind1 = value.indexOf("(u'");
				ind2 = value.indexOf("',)");
				error = value.substring(ind1 + 3, ind2);
				arr[0] = error;
				arr[1] = StringEscapeUtils.unescapeJava(error);
			}
			ind1 = value.lastIndexOf("(u'''");
			ind2 = value.lastIndexOf("''')");
			error = value.substring(ind1 + 5, ind2);
			arr[6] = error;
		}

		if (value.contains("(u'") && value.contains("')")) {
			int ind1;
			int ind2;
			if (value.contains("',)")) {
				ind1 = value.indexOf("(u'");
				ind2 = value.indexOf("',)");
				error = value.substring(ind1 + 3, ind2);
				arr[0] = error;
				arr[1] = StringEscapeUtils.unescapeJava(error);
			}
			ind1 = value.lastIndexOf("(u'");
			ind2 = value.lastIndexOf("')");
			error = value.substring(ind1 + 3, ind2);
			arr[2] = error;
		}

		if (value.contains("(u'") && value.contains("')")) {
			int ind1;
			int ind2;
			if (value.contains("\",)")) {
				ind1 = value.indexOf("(u\"");
				ind2 = value.indexOf("\",)");
				error = value.substring(ind1 + 3, ind2);
				arr[0] = error;
				arr[1] = StringEscapeUtils.unescapeJava(error);
			}
			ind1 = value.lastIndexOf("(u'");
			ind2 = value.lastIndexOf("')");
			error = value.substring(ind1 + 3, ind2);
			arr[4] = error;
		}

		if (value.contains("(u\"") && value.contains("\")")) {
			int ind1;
			int ind2;
			if (value.contains("',)")) {
				ind1 = value.indexOf("(u'");
				ind2 = value.indexOf("',)");
				error = value.substring(ind1 + 3, ind2);
				arr[0] = error;
				arr[1] = StringEscapeUtils.unescapeJava(error);
			}
			ind1 = value.lastIndexOf("(u\"");
			ind2 = value.lastIndexOf("\")");
			error = value.substring(ind1 + 3, ind2);
			arr[3] = error;
		}

		if (value.contains("(u\"") && value.contains("\")")) {
			int ind1;
			int ind2;
			if (value.contains("\",)")) {
				ind1 = value.indexOf("(u\"");
				ind2 = value.indexOf("\",)");
				error = value.substring(ind1 + 3, ind2);
				arr[0] = error;
				arr[1] = StringEscapeUtils.unescapeJava(error);
			}
			ind1 = value.lastIndexOf("(u\"");
			ind2 = value.lastIndexOf("\")");
			error = value.substring(ind1 + 3, ind2);
			arr[5] = error;
		}

		return arr;
	}

	private String handleCelestaExceptionTraceback(final String value, final String[] arr) {
		String sumValue = value;
		// sumValue = sumValue.replaceFirst("u'(.)+'", "u'" + error + "'");
		// sumValue = sumValue.replaceFirst("u\"(.)+\"", "u\"" + error + "\"");
		if (!"".equals(arr[1])) {
			sumValue = sumValue.replace(arr[0], arr[1]);
			if (!"".equals(arr[6]))
				sumValue = sumValue.replace(arr[6], arr[1]);
			if (!"".equals(arr[2]))
				sumValue = sumValue.replace(arr[2], arr[1]);
			if (!"".equals(arr[3]))
				sumValue = sumValue.replace(arr[3], arr[1]);
			if (!"".equals(arr[4]))
				sumValue = sumValue.replace(arr[4], arr[1]);
			if (!"".equals(arr[5]))
				sumValue = sumValue.replace(arr[5], arr[1]);
		}
		return sumValue;
	}

}
