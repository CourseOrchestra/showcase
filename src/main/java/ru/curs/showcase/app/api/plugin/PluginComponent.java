package ru.curs.showcase.app.api.plugin;


/**
 * Абстрактный класс компоненты плагина.
 * 
 * @author bogatov
 * 
 */
public interface PluginComponent {
	String PLUGININFO_ID_PREF = "ElementInfo_";
	String ELEMENTPROC_ID_PREF = "PostProc_";

	/**
	 * Отрисовать компонент.
	 */
	void draw();

	/**
	 * Получить параметры плагина.
	 * 
	 * @return PluginParam
	 */
	PluginParam getParam();

	/**
	 * Добавить хендлер.
	 * 
	 * @param handler
	 *            DrawPluginCompleteHandler
	 */
	void addDrawPluginCompleteHandler(DrawPluginCompleteHandler handler);
}
