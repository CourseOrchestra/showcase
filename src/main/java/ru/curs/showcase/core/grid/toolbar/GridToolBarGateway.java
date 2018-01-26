package ru.curs.showcase.core.grid.toolbar;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;


/**
 * Интерфейс шлюза получения данных панели инструментов грида.
 * 
 * @author bogatov
 * 
 */
public interface GridToolBarGateway {

	/**
	 * Получить необработанные данные панели инструментов грида.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elInfo
	 *            - описание элемента.
	 * @return {@link GridToolBarRawData}
	 */
	GridToolBarRawData getGridToolBarRawData(final CompositeContext context, final DataPanelElementInfo elInfo);
}
