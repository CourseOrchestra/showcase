package ru.curs.showcase.core.geomap;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.util.exception.BaseException;

import com.google.gson.JsonSyntaxException;

/**
 * Класс исключения, вызываемого при проверке шаблона карты. При обработке
 * исключения происходит "облагораживание" сообщения об ошибке из GSON. Поэтому
 * при обновлении GSON данную обработку нужно проверять на корректность.
 * 
 * @author den
 * 
 */
public class GeoMapWrongTemplateException extends BaseException {

	private static final String CHECK_ERROR = "Шаблон карты в %s задан ошибочно: %s";
	private static final long serialVersionUID = 2875251476124978122L;
	private static final String NUM_CHECK_ERROR =
		"В шаблоне карты в %s вместо числа задана строка: %s";

	public GeoMapWrongTemplateException(final String aMessage) {
		super(ExceptionType.SOLUTION, aMessage);
	}

	public GeoMapWrongTemplateException(final JsonSyntaxException e,
			final DataPanelElementInfo elInfo) {
		super(ExceptionType.SOLUTION, getStdMessage(e, elInfo), e);
	}

	private static String getStdMessage(final JsonSyntaxException e,
			final DataPanelElementInfo elInfo) {
		if ((e.getCause() != null) && (e.getCause().getClass() == NumberFormatException.class)) {
			return String.format(NUM_CHECK_ERROR, elInfo.getProcName(), e.getLocalizedMessage()
					.replace("java.lang.NumberFormatException: For input string: ", ""));
		}
		return String.format(
				CHECK_ERROR,
				elInfo.getProcName(),
				e.getLocalizedMessage().replace("com.google.gson.stream.MalformedJsonException: ",
						""));
	}
}
