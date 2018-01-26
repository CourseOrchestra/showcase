package ru.curs.showcase.core.sp;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза для получения настроек элемента инф. панели.
 * 
 * @author den
 * 
 */
public interface ElementSettingsGateway {
	/**
	 * Основной метод для получения настроек.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 */
	RecordSetElementRawData getRawData(CompositeContext context, DataPanelElementInfo elementInfo);

	/**
	 * 
	 * Должен вернуть объект-хранитель активной сессии, в рамках которой были
	 * получены данные.
	 */
	Object getSession();
}
