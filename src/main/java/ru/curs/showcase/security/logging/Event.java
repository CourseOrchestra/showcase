package ru.curs.showcase.security.logging;

import java.util.*;
import java.util.Map.Entry;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Событие.
 * 
 * @author bogatov
 * 
 */
public class Event {
	public static final String DATA_TAG = "data";

	/**
	 * Тип события.
	 * 
	 */
	public enum TypeEvent {
		/**
		 * LOGIN.
		 * 
		 */
		LOGIN,
		/**
		 * Login error.
		 * 
		 */
		LOGINERROR,
		/**
		 * LOGOUT.
		 * 
		 */
		LOGOUT,
		/**
		 * Session time out.
		 * 
		 */
		SESSIONTIMEOUT
	}

	private final TypeEvent typeEvent;
	private CompositeContext context;
	private final Map<String, Object> mapData = new HashMap<String, Object>();

	public Event(final TypeEvent eTypeEvent) {
		super();
		this.typeEvent = eTypeEvent;
	}

	public Event(final TypeEvent eTypeEvent, final CompositeContext oContext) {
		super();
		this.typeEvent = eTypeEvent;
		this.context = oContext;
	}

	/**
	 * Функция-getter для переменной typeEvent.
	 * 
	 * @return typeEvent
	 */
	public TypeEvent getTypeEvent() {
		return typeEvent;
	}

	/**
	 * Функция-getter для переменной context.
	 * 
	 * @return context
	 */
	public CompositeContext getContext() {
		return context;
	}

	/**
	 * Функция-setter для переменной context.
	 * 
	 * @param oContext
	 *            - переменная типа CompositeContext
	 */
	public void setContext(final CompositeContext oContext) {
		this.context = oContext;
	}

	/**
	 * Добавление атрибута.
	 * 
	 */
	public void add(final String name, final Object value) {
		mapData.put(name, value);
	}

	private String getXml(final Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : map.entrySet()) {
			sb.append("<").append(entry.getKey()).append(">");
			Object value = entry.getValue();
			String strValue = "";
			if (value != null) {
				if (value instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Object> mapValue = (Map<String, Object>) value;
					strValue = getXml(mapValue);
				} else {
					strValue = value.toString();
				}
			}
			sb.append(strValue);
			sb.append("</").append(entry.getKey()).append(">");
		}
		return sb.toString();
	}

	/**
	 * Возвращает все параметры в виде xml строки.
	 * 
	 * @return string xml
	 */
	public String getXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(DATA_TAG).append(">");
		sb.append(getXml(this.mapData));
		sb.append("</").append(DATA_TAG).append(">");
		return sb.toString();
	}

}
