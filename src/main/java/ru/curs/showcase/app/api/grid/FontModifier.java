package ru.curs.showcase.app.api.grid;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Модификаторы шрифта.
 */
public enum FontModifier implements SerializableElement {
	/**
	 * Жирное начертание.
	 */
	BOLD,
	/**
	 * Наклонный.
	 */
	ITALIC,
	/**
	 * Подчеркнутый.
	 */
	UNDERLINE,
	/**
	 * Зачеркнутый.
	 */
	STRIKETHROUGH
}
