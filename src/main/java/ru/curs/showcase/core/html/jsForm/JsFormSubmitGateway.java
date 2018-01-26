package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Шлюз для отправки данных элемента панели JsForm.
 * 
 * @author bogatov
 *
 */

public interface JsFormSubmitGateway {
	/**
	 * Основная функция получения данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - элемент.
	 * @param inputData
	 *            - входные данные.
	 * @return - данные.
	 */
	String getData(CompositeContext context, DataPanelElementInfo elementInfo, String inputData);
}
