package ru.curs.showcase.util;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.AppInfoSingleton;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;

/**
 * Вспомогательные функции для работы с сервлетами.
 * 
 * @author den
 * 
 */
public final class ServletUtils {

	private static final String CANT_WRITE_RESPONSE_ERROR =
		"Невозможно вернуть ошибку в HTTP response";

	/**
	 * Идентификатор сессии для модульных тестов.
	 */
	public static final String TEST_SESSION = "testSession";

	private static final Logger LOGGER = LoggerFactory.getLogger(ServletUtils.class);

	private ServletUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Подготавливает карту с параметрами URL. При подготовке учитывается то,
	 * что русские параметры URL считываются сервером в кодировке ISO-8859-1 при
	 * том, что в реальности они приходят либо в UTF-8 либо в СP1251, а также
	 * тот факт, что установка req.setCharacterEncoding("ISO-8859-1") не
	 * помогает для параметров из URL (хотя помогает для параметров HTML формы).
	 * 
	 * @param req
	 *            - http запрос.
	 */
	public static SortedMap<String, List<String>>
			prepareURLParamsMap(final HttpServletRequest req) throws UnsupportedEncodingException {
		SortedMap<String, List<String>> result = new TreeMap<>();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = req.getParameterMap().keySet().iterator();
		while (iterator.hasNext()) {
			String oldKey = iterator.next();
			String key = checkAndRecodeURLParam(oldKey);
			String[] oldValues = (String[]) req.getParameterMap().get(oldKey);
			ArrayList<String> values = new ArrayList<>();
			for (int i = 0; i < oldValues.length; i++) {
				values.add(checkAndRecodeURLParam(oldValues[i]));
			}
			result.put(key, values);
		}
		return result;
	}

	public static CompositeContext prepareURLParamsContext(final HttpServletRequest request)
			throws UnsupportedEncodingException {
		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(request);
		return new CompositeContext(params);
	}

	/**
	 * Стандартный обработчик ошибки в сервлете.
	 * 
	 * @param response
	 *            - ответ.
	 * @param message
	 *            - сообщение об ошибке.
	 * @param needOKStatus
	 *            - указание на то, что статус ответа должен быть OK, несмотря
	 *            на ошибку.
	 * @throws IOException
	 */
	public static void fillErrorResponce(final HttpServletResponse response, final String message,
			final boolean needOKStatus) throws IOException {
		if (!response.isCommitted()) {
			response.reset();
			doNoCasheResponse(response);
			if (needOKStatus) {
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			makeResponseFromString(response, message);
		} else {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
				LOGGER.warn(CANT_WRITE_RESPONSE_ERROR);
			}
		}
	}

	/**
	 * Стандартная функция для записи ответа сервера из переданной строки. Код
	 * статуса должен быть установлен до вызова этой функции, т.к. она закрывает
	 * поток записи ответа.
	 * 
	 * @param response
	 *            - объект ответа сервера.
	 * @param message
	 *            - текст для записи в тело ответа.
	 * @throws IOException
	 */
	public static void makeResponseFromString(final HttpServletResponse response,
			final String message) throws IOException {
		response.setContentType("text/html");
		makeStdResponse(response, message);
	}

	public static void makeXMLResponseFromString(final HttpServletResponse response,
			final String message) throws IOException {
		response.setContentType("text/xml");
		makeStdResponse(response, message);
	}

	protected static void
			makeStdResponse(final HttpServletResponse response, final String message)
					throws IOException {
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);
		try (PrintWriter writer = response.getWriter()) {
			writer.append(message);
		}
	}

	/**
	 * Определяет, является ли браузер пользователя "старой версией IE".
	 * "Старыми" считаются 6 и 7 IE.
	 * 
	 * @param request
	 *            - HttpServletRequest.
	 * @return - результат проверки.
	 */
	public static boolean isOldIE(final HttpServletRequest request) {
		String userAgent = getUserAgent(request);
		boolean isOldIE =
			(userAgent.indexOf("msie 6.0") != -1) || (userAgent.indexOf("msie 7.0") != -1);
		return isOldIE;
	}

	public static String getUserAgent(final HttpServletRequest request) {
		return request.getHeader("User-Agent").toLowerCase();
	}

	/**
	 * Возвращает содержимое реквеста в виде строки. Применяется для обработки
	 * submission с X-форм.
	 * 
	 * @param request
	 *            реквест
	 * @return - строку с содержимым.
	 */
	public static String getRequestAsString(final HttpServletRequest request)
			throws java.io.IOException {
		InputStream is = request.getInputStream();

		return TextUtils.streamToString(is);

		// String line;
		//
		// try (BufferedReader requestData =
		// new BufferedReader(new InputStreamReader(is,
		// TextUtils.DEF_ENCODING))) {
		// StringBuffer stringBuffer = new StringBuffer();
		// while ((line = requestData.readLine()) != null) {
		// stringBuffer.append(line);
		// }
		// line = stringBuffer.toString();
		// }
		// return line;
	}

	/**
	 * Функция устанавливает у response сервера атрибуты, предотвращающие
	 * кэширование результатов запроса в различных браузерах и прокси-серверах.
	 * 
	 * @param resp
	 *            - response (ответ сервера).
	 */
	public static void doNoCasheResponse(final HttpServletResponse resp) {
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "must-revalidate,no-store,no-cache");
		resp.setDateHeader("Expires", 0);
	}

	/**
	 * Проверяет параметр на неверную кодировку ANSI\ISO вместо UTF8 и
	 * исправляет ее при необходимости.
	 * 
	 * @param param
	 *            - параметр.
	 * @return - правильно кодированный параметр.
	 * @throws UnsupportedEncodingException
	 */
	public static String checkAndRecodeURLParam(final String param)
			throws UnsupportedEncodingException {
		String enc = UTF8Checker.getRealEncoding(param);
		if (!TextUtils.DEF_ENCODING.equals(enc)) {
			return TextUtils.recode(param, enc, TextUtils.DEF_ENCODING);
		}
		return param;
	}

	/**
	 * Функция десериализации объекта, переданного в теле запроса.
	 * 
	 * @param data
	 *            - строка с urlencoded объектом.
	 * @return - объект.
	 * @throws SerializationException
	 */
	public static Object deserializeObject(final String data) throws SerializationException {
		ServerSerializationStreamReader streamReader =
			new ServerSerializationStreamReader(Thread.currentThread().getContextClassLoader(),
					null);
		streamReader.prepareToRead(data);
		return streamReader.readObject();
	}

}
