package ru.curs.showcase.app.api.plugin;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Интерфейс определяет завершение отрисовки плагина.
 * 
 * @author bogatov
 * 
 */
public interface DrawPluginCompleteHandler {
	/**
	 * Отрисовка завершена.
	 * 
	 * @param o
	 *            объет полученый из адаптера плагина
	 */
	void onDrawComplete(JavaScriptObject o);
}
