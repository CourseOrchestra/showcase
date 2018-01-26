package ru.curs.showcase.app.api.chart;

import java.util.List;

import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.InteractionType;

/**
 * Адаптер абстрактного менеджера событий для графика.
 * 
 * @author den
 * 
 */
public final class ChartEventManager extends EventManager<ChartEvent> {

	private static final long serialVersionUID = 3574166433929505612L;

	/**
	 * Функция возвращает нужный обработчик события по переданным ей
	 * идентификаторам значения на графике и типу взаимодействия.
	 * 
	 * @param seriesName
	 *            - идентификатор строки.
	 * @param x
	 *            - идентификатор строки.
	 * @return - событие или NULL.
	 */
	public List<ChartEvent> getEventForValue(final String seriesName, final Integer x) {
		return getEventByIds(seriesName, x.toString(), InteractionType.SINGLE_CLICK);
	}
}
