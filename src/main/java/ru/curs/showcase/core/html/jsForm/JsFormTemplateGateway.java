package ru.curs.showcase.core.html.jsForm;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Шлюз для получения данных шаблона, необходимых для построения элемента панели
 * JsForm.
 * 
 * @author bogatov
 *
 */

public interface JsFormTemplateGateway {
	/**
	 * Основная функция получения данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - элемент.
	 * @return - данные.
	 */
	JsFormData getData(CompositeContext context, DataPanelElementInfo elementInfo);
}
