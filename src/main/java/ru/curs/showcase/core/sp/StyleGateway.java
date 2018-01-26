package ru.curs.showcase.core.sp;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза для получения настроек элемента инф. панели.
 * 
 * @author den
 * 
 */
public interface StyleGateway {
	/**
	 * Основной метод для получения настроек.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 */
	String[] getRawData(CompositeContext context, final String procName);

}
