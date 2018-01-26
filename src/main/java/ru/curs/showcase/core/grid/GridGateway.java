package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.OutputStreamDataFile;

/**
 * Интерфейс шлюза к уровню данных для грида. Возвращает данные и метаданные для
 * грида.
 * 
 * @author den
 * 
 */
public interface GridGateway extends RecordSetElementGateway<GridContext> {

	RecordSetElementRawData
			getRawDataAndSettings(GridContext context, DataPanelElementInfo element);

	/**
	 * Возвращает файл для грида.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param aLinkId
	 *            - идентификатор хранимой процедуры для скачивания файла
	 * @param recordId
	 *            - идентификатор записи грида для скачивания файла
	 * @return - файл.
	 */
	OutputStreamDataFile downloadFile(CompositeContext context, DataPanelElementInfo elementInfo,
			ID aLinkId, String recordId);

	/**
	 * Сохраняет отредактированные данные.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @return - GridSaveResult.
	 */
	GridSaveResult saveData(GridContext context, DataPanelElementInfo element);

	/**
	 * Добавляет запись.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @return - GridAddRecordResult.
	 */
	GridAddRecordResult addRecord(GridContext context, DataPanelElementInfo element);

	/**
	 * Указывает на то, что шлюз должен продолжать сессию, переданную ему в
	 * параметре. Имеет смысл только для соединений с БД.
	 * 
	 * @param sessionHolder
	 *            - объект-хранитель сессии.
	 */
	void continueSession(ElementSettingsGateway sessionHolder);

}
