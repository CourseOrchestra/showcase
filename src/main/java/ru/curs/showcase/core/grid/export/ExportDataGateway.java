package ru.curs.showcase.core.grid.export;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза выгрузки данных в Excel.
 * 
 * @author bogatov
 * 
 */
public interface ExportDataGateway {

	/**
	 * Получить данные для формирования файла Excel.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elInfo
	 *            - описание элемента.
	 * @param handler
	 *            - обработчик результирующего набора записей.
	 */
	void getExportData(final CompositeContext context, final DataPanelElementInfo elInfo,
			final ResultSetHandler handler);
}
