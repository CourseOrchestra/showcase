package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Типы полей в Action. Используется в обработчике webTextPanelClick для замены
 * значения в одном из полей.
 * 
 * @author den
 * 
 */
public enum ActionFieldType implements SerializableElement {
	ADD_CONTEXT, MAIN_CONTEXT, FILTER_CONTEXT, ELEMENT_ID
}
