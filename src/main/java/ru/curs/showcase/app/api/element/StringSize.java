package ru.curs.showcase.app.api.element;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Текстовые данные о размере элемента.
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StringSize implements SerializableElement {
	private static final long serialVersionUID = -7087700620409401211L;

	/**
	 * Ширина элемента.
	 */
	private String width = null;
	/**
	 * Высота элемента.
	 */
	private String height = null;

	public final String getWidth() {
		return width;
	}

	public void setWidth(final String aWidth) {
		width = aWidth;
	}

	public final String getHeight() {
		return height;
	}

	public void setHeight(final String aHeight) {
		height = aHeight;
	}

	public boolean getAutoSize() {
		return (width == null) && (height == null);
	}

}