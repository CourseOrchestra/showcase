package ru.curs.showcase.app.api.geomap;

import java.util.List;

import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.InteractionType;

/**
 * Менеджер события для карты.
 * 
 * @author den
 * 
 */
public class GeoMapEventManager extends EventManager<GeoMapEvent> {

	private static final long serialVersionUID = 3329856250556569988L;

	/**
	 * Функция возвращает нужный обработчик события по переданному ей
	 * идентификатору объекта на карте. Мы не разделяем события на одном и том
	 * же объекте для разных слоев.
	 * 
	 * @param featureId
	 *            - идентификатор строки.
	 * @return - событие или NULL в случае его отсутствия.
	 */
	public List<GeoMapEvent> getEventForFeature(final String featureId) {
		return getEventByIds(featureId, null, InteractionType.SINGLE_CLICK);
	}
}
