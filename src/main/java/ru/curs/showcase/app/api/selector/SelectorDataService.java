package ru.curs.showcase.app.api.selector;

import com.google.gwt.user.client.rpc.RemoteService;

import ru.curs.showcase.app.api.services.GeneralException;

/**
 * Сервисный интерфейс для получения данных.
 */

public interface SelectorDataService extends RemoteService {
	/**
	 * Возвращает набор данных.
	 * 
	 * @param request
	 *            запрос данных
	 * @return набор данных
	 */
	DataSet getSelectorData(DataRequest request) throws GeneralException;

}
