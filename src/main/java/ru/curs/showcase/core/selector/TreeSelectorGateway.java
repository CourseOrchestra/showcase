package ru.curs.showcase.core.selector;

import ru.curs.showcase.app.api.selector.TreeDataRequest;

/**
 * Шлюз для получения данных, необходимых для нового триселектора.
 * 
 */
public interface TreeSelectorGateway {
	/**
	 * Получения данных для компонента.
	 * 
	 * @param request
	 *            - параметры запроса.
	 * @return - данные.
	 */
	ResultTreeSelectorData getData(TreeDataRequest request) throws Exception;

}
