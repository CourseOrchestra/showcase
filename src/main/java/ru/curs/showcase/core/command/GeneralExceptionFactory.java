package ru.curs.showcase.core.command;

import org.slf4j.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Фабрика по созданию GeneralException на основе серверного Exception.
 * 
 * @author den
 * 
 */
public final class GeneralExceptionFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseException.class);

	private static final String ERROR_CAPTION = "Сообщение об ошибке";

	private GeneralExceptionFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Выводит в лог полную информацию об исключении.
	 * 
	 * @param e
	 *            - исключение.
	 */
	private static void logAll(final Throwable e) {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
			LOGGER.error(ERROR_CAPTION, e);
		}
	}

	/**
	 * Основная функция фабрики.
	 * 
	 * @return - исключение.
	 * @param original
	 *            - оригинальное исключение.
	 * @param aCurrentContext
	 *            - текущий контекст.
	 */
	public static GeneralException build(final Throwable original,
			final DataPanelElementContext aCurrentContext) {
		log(original);
		GeneralException res = new GeneralException(original, getUserMessage(original));
		res.setOriginalExceptionClass(original.getClass().getName());
		res.setOriginalMessage(getOriginalMessage(original));
		res.setType(getType(original));
		res.setContext(aCurrentContext);
		res.setMessageType(getMessageType(original));
		res.setMessageCaption(getMessageCaption(original));
		res.setMessageSubtype(getMessageSubtype(original));
		res.setNeedDatailedInfo(ExceptionConfig.needDatailedInfoForException(original));
		return res;
	}

	public static GeneralException build(final Throwable original) {
		return build(original, null);

	}

	private static void log(final Throwable original) {
		if (!(original instanceof BaseException)) {
			logAll(original);
		}
	}

	private static MessageType getMessageType(final Throwable exc) {
		if (exc instanceof ValidateException) {
			return ((ValidateException) exc).getUserMessage().getType();
		}
		return MessageType.ERROR;
	}

	private static String getMessageCaption(final Throwable exc) {
		if (exc instanceof ValidateException) {
			return ((ValidateException) exc).getUserMessage().getCaption();
		}
		return null;
	}

	private static String getMessageSubtype(final Throwable exc) {
		if (exc instanceof ValidateException) {
			return ((ValidateException) exc).getUserMessage().getSubtype();
		}
		return null;
	}

	private static ExceptionType getType(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getType();
		}
		return ExceptionType.JAVA;
	}

	private static String getUserMessage(final Throwable exc) {
		if (exc instanceof ValidateException) {
			return ((ValidateException) exc).getUserMessage().getText();
		}
		if (exc.getLocalizedMessage() != null) {
			return exc.getLocalizedMessage();
		} else {
			return exc.getClass().getName();
		}
	}

	private static String getOriginalMessage(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getOriginalMessage();
		}
		return null;
	}
}
