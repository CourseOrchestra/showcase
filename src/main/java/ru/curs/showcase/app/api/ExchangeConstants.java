package ru.curs.showcase.app.api;

/**
 * Константы, используемые при обмене информацией между клиентом и сервером.
 * 
 * @author den
 * 
 */
public final class ExchangeConstants implements SerializableElement {

	private ExchangeConstants() {
		throw new UnsupportedOperationException();
	}

	public static final String SUBMIT_SERVLET = "submit";

	/**
	 * Префикс сервлетов, используемых для передачи или получения
	 * пользовательских данных.
	 */
	public static final String SECURED_SERVLET_PREFIX = "secured";

	/**
	 * Название параметра userdata в URL.
	 */
	public static final String URL_PARAM_USERDATA = "userdata";
	public static final String URL_PARAM_PERSPECTIVE = "perspective";

	/**
	 * Идентификатор userdata по-умолчанию.
	 */
	public static final String DEFAULT_USERDATA = "default";

	/**
	 * Разделитель строк для сообщений, показываемых пользователю (используется
	 * разделитель Windows). System.getProperty("line.separator"); - не работает
	 * в gwt
	 */
	public static final String LINE_SEPARATOR = "\r\n";

	private static final long serialVersionUID = 6722656736605297948L;

	/**
	 * Префикс для параметров сервлета, содержащих данные файла.
	 */
	public static final String FILE_DATA_PARAM_PREFIX = "@@filedata@@";

	public static final String SESSION_NOT_AUTH_SIGN = "SessionNotAuthenticated";

	public static final String OK_MESSAGE_TEXT_BEGIN = "okMessageTextBegin";
	public static final String OK_MESSAGE_TEXT_END = "okMessageTextEnd";
	public static final String OK_MESSAGE_TYPE_BEGIN = "okMessageTypeBegin";
	public static final String OK_MESSAGE_TYPE_END = "okMessageTypeEnd";
	public static final String OK_MESSAGE_CAPTION_BEGIN = "okMessageCaptionBegin";
	public static final String OK_MESSAGE_CAPTION_END = "okMessageCaptionEnd";
	public static final String OK_MESSAGE_SUBTYPE_BEGIN = "okMessageSubtypeBegin";
	public static final String OK_MESSAGE_SUBTYPE_END = "okMessageSubtypeEnd";

	public static final String NOT_CLEAR_UPLOAD_TAG = "notClearUpload";

	public static final String CONTEXT_BEGIN = "ContextECE12FBDBegin";
	public static final String CONTEXT_END = "ContextECE12FBDEnd";

	public static final String OK_MESSAGE_BEGIN = "okMessageECE12FBDBegin";
	public static final String OK_MESSAGE_END = "okMessageECE12FBDEnd";

	public static final String OK_MESSAGE_X = "F7C3F7E2";
	public static final String OK_MESSAGE_QUOT = "A97A03D8";

}
