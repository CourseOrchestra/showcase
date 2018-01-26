package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.Event;

/**
 * Событие на карте.
 * 
 * @author den
 * 
 */
public final class GeoMapEvent extends Event {

	private static final long serialVersionUID = 2590511685180732104L;

	public ID getObjectId() {
		return getId1();
	}

	/**
	 * Устанавливает идентификатор объекта на карте.
	 * 
	 * @param objectId
	 *            - значение идентификатора.
	 */
	public void setObjectId(final String objectId) {
		setId1(objectId);
	}
}
