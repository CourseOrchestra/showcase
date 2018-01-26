package ru.curs.showcase.app.api.grid;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Горизонтальное выравнивание.
 */
public enum HorizontalAlignment implements SerializableElement {
	/**
	 * Слева.
	 */
	LEFT,
	/**
	 * По центру.
	 */
	CENTER,
	/**
	 * Справа.
	 */
	RIGHT;

	public static HorizontalAlignmentConstant
			getHorizontalAlignmentConstant(final HorizontalAlignment horizontalAlignment) {
		switch (horizontalAlignment) {
		case LEFT:
			return HasHorizontalAlignment.ALIGN_LEFT;
		case CENTER:
			return HasHorizontalAlignment.ALIGN_CENTER;
		case RIGHT:
			return HasHorizontalAlignment.ALIGN_RIGHT;
		default:
			return HasHorizontalAlignment.ALIGN_LEFT;
		}
	}

}
