package ru.curs.showcase.app.api.selector;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Асинхронный интерфейс сервиса SelectorDataService.
 */
public interface SelectorDataServiceAsync {
	/**
	 * Возвращает данные для селектора.
	 * 
	 * @param request
	 *            запрос
	 * @param async
	 *            асинхронный обработчик
	 */
	void getSelectorData(DataRequest request, AsyncCallback<DataSet> async);
}
