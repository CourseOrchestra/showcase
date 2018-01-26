package ru.curs.showcase.app.api.html;

import java.util.List;

import ru.curs.showcase.app.api.element.EventManager;
import ru.curs.showcase.app.api.event.InteractionType;

/**
 * Адаптер менеджера событий для HTML based элементов.
 * 
 * @author den
 * 
 */
public final class HTMLEventManager extends EventManager<HTMLEvent> {

	private static final long serialVersionUID = 3574166433929505612L;

	/**
	 * Функция возвращает нужный обработчик события по переданным ей
	 * идентификаторам значения на графике и типу взаимодействия.
	 * 
	 * @param linkId
	 *            - идентификатор строки.
	 * @return - событие или NULL.
	 */
	public List<HTMLEvent> getEventForLink(final String linkId) {
		return getEventByIds(linkId, null, InteractionType.SINGLE_CLICK);
	}
}
