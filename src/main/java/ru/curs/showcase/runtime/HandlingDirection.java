package ru.curs.showcase.runtime;

import org.slf4j.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Направления обработки запросов. Содержит 2 значения: вход и выход.
 * 
 * @author den
 * 
 */
public enum HandlingDirection implements SerializableElement {
	INPUT, OUTPUT;

	public Marker getMarker() {
		return MarkerFactory.getMarker(this.name());
	}
}
