package ru.curs.showcase.util;

import java.text.DateFormat;
import java.util.Date;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Класс, содержащий общие функции для логирования.
 * 
 */
public final class LoggerHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger("profileLog");

	private LoggerHelper() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Вывод в лог.
	 */
	public static void profileToLog(final String elementId, final Date dtBegin, final Date dtEnd,
			final String elementType, final String elementSubType) {

		// Формат
		// [id элемента] [time begin] [time end] [duration in ms] [element type]
		// [elementsbtype]

		String mess =
			"["
					+ elementId
					+ "] ["
					+ DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT)
							.format(dtBegin)
					+ "] ["
					+ DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT)
							.format(dtEnd) + "] "
					+ String.valueOf(dtEnd.getTime() - dtBegin.getTime()) + " " + elementType
					+ " " + elementSubType;

		if (AppInfoSingleton.getAppInfo().isEnableLogLevelDebug()) {
			LOGGER.debug(mess);
		}
	}

}
