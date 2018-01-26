package ru.curs.showcase.core;

import java.util.List;

/**
 * Класс, содержащий информацию, относящуюся к настройке исключений Showcase.
 * Например, нужно ли для конкретного исключения отображать пользователю
 * подробную информацию.
 * 
 * @author den
 * 
 */
public class ExceptionConfig {

	private List<Class<? extends Exception>> noDatailedInfoExceptions;

	public List<Class<? extends Exception>> getNoDatailedInfoExceptions() {
		return noDatailedInfoExceptions;
	}

	public void setNoDatailedInfoExceptions(
			final List<Class<? extends Exception>> aNoDatailedInfoExceptions) {
		noDatailedInfoExceptions = aNoDatailedInfoExceptions;
	}

	public static Boolean needDatailedInfoForException(final Throwable e) {
		ExceptionConfig config = AppRegistry.getExceptionConfig();
		return !config.noDatailedInfoExceptions.contains(e.getClass());
	}
}
