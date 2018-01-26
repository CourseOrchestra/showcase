package ru.curs.showcase.core.html;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Шлюз для получения данных, необходимых для построения элемента панели HTML
 * типа (например, WebText или XForms).
 * 
 * @author den
 * 
 */
public interface HTMLGateway {

	/**
	 * Основная функция получения данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - xml данные.
	 */
	HTMLBasedElementRawData getRawData(CompositeContext context, DataPanelElementInfo element);
}
