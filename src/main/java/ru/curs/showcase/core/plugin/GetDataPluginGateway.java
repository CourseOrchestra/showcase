package ru.curs.showcase.core.plugin;

import ru.curs.showcase.app.api.plugin.RequestData;

/**
 * Шлюз для получения данных, необходимых для компонента plugin.
 * 
 * @author bogatov
 * 
 */
public interface GetDataPluginGateway {
	/**
	 * Получения данных для компонента.
	 * 
	 * @param request
	 *            - параметры запроса.
	 * @return - данные.
	 */
	ResultPluginData getData(RequestData request) throws Exception;

}
