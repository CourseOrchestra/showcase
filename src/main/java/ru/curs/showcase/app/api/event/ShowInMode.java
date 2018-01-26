package ru.curs.showcase.app.api.event;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Способ отображения элементов для данного действия.
 * 
 * @author den
 * 
 */
public enum ShowInMode implements SerializableElement {
	/**
	 * Внутри вкладки информационной панели (по умолчанию).
	 */
	PANEL,
	/**
	 * В модальном окне.
	 */
	MODAL_WINDOW
}
