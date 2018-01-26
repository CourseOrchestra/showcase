package ru.curs.showcase.core.frame;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза для получения кода фрейма для главной страницы приложения.
 * 
 * @author den
 * 
 */
public interface MainPageFrameGateway {

	/**
	 * Основная функция получения данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param frameSource
	 *            - источник данных.
	 */
	String getRawData(CompositeContext context, String frameSource);

	String getRawData(CompositeContext context);

	void setSourceName(String name);

}
