package ru.curs.showcase.app.api.element;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;

/**
 * Абстрактный менеджер событий, от которого наследуются менеджеры события для
 * грида, графика... Менеджер событий хранит набор событий для UI элемента, а
 * также функции для быстрого выбора нужного события из списка.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип событий.
 */
public abstract class EventManager<T extends Event> implements SerializableElement {

	private static final long serialVersionUID = -464983712459253702L;
	/**
	 * Набор возможных событий для элемента инф. панели.
	 */
	private List<T> events = new ArrayList<T>();

	/**
	 * Признак того, что даже при наличии конкретного события (c совпадающими
	 * id1 и id2) до него должно срабатывать общее событие (c совпадающим id1).
	 */
	private Boolean fireGeneralAndConcreteEvents = false;

	public List<T> getEvents() {
		return events;
	}

	public void setEvents(final List<T> aEvents) {
		events = aEvents;
	}

	/**
	 * Функция возвращает нужные обработчики события по переданным ей
	 * идентификаторам и типу события. При этом события, у которых заданы 2
	 * идентификатора имеют приоритет перед событиями с одним идентификатором. В
	 * возвращаемом списке может быть более одного события, если включена опция
	 * fireGeneralAndConcreteEvents.
	 * 
	 * @param id1
	 *            - идентификатор 1 (обязательный).
	 * @param id2
	 *            - идентификатор 2 (необязательный).
	 * @param interactionType
	 *            - тип взаимодействия.
	 * @return - событие или NULL.
	 */
	protected List<T> getEventByIds(final String id1, final String id2,
			final InteractionType interactionType) {
		List<T> res = new ArrayList<T>();
		if (id1 == null) {
			return res;
		}

		T general = null;
		Event testEvent = new Event(id1, id2, interactionType);
		for (T current : events) {
			if (current.isCompatible(testEvent)) {
				if (current.isGeneral()) {
					general = current;
					if (id2 == null) {
						break;
					}
				} else {
					res.add(current);
					break;
				}
			}
		}
		if (fireGeneralAndConcreteEvents || res.isEmpty()) {
			if (general != null) {
				res.add(0, general);
			}
		}
		return res;
	}

	/**
	 * Очищает коллекцию событий от мусорных элементов - т.е. элементов, чьи ID
	 * не входят в переданные в процедуру наборы.
	 * 
	 * @param ids1
	 *            - набор возможных id1.
	 * @param ids2
	 *            - набор возможных id2.
	 * @return - число удаленных элементов.
	 */
	public int clean(final Set<ID> ids1, final Set<ID> ids2) {
		int initSize = events.size();
		Iterator<T> iterator = events.iterator();
		while (iterator.hasNext()) {
			Event cur = iterator.next();
			if (!ids1.contains(cur.getId1())) {
				iterator.remove();
				continue;
			}
			if ((cur.getId2() != null) && (!ids2.contains(cur.getId2()))) {
				iterator.remove();
				continue;
			}
		}
		return initSize - events.size();
	}

	public Boolean getFireGeneralAndConcreteEvents() {
		return fireGeneralAndConcreteEvents;
	}

	public void setFireGeneralAndConcreteEvents(final Boolean aFireGeneralAndConcreteEvents) {
		fireGeneralAndConcreteEvents = aFireGeneralAndConcreteEvents;
	}
}
