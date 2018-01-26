package ru.curs.showcase.app.api.element;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Позиция вспомогательной панели относительно элемента информационной панели.
 * 
 * @author den
 * 
 */
public enum ChildPosition implements SerializableElement {
	/**
	 * Слева.
	 */
	LEFT,
	/**
	 * Справа.
	 */
	RIGHT,
	/**
	 * Сверху.
	 */
	TOP,
	/**
	 * Снизу.
	 */
	BOTTOM
}
