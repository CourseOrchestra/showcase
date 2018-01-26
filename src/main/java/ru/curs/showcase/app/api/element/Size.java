package ru.curs.showcase.app.api.element;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Данные о размере элемента. Являются базовым классом для данных графика и
 * карты.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Size implements SerializableElement {

	private static final long serialVersionUID = 9180510243385481114L;

	public static final Integer AUTOSIZE_CONSTANT = -999;
	/**
	 * Ширина элемента.
	 */
	private Integer width;
	/**
	 * Высота элемента.
	 */
	private Integer height;

	public final Integer getWidth() {
		return width;
	}

	public void setWidth(final Integer aWidth) {
		width = aWidth;
	}

	public final Integer getHeight() {
		return height;
	}

	public void setHeight(final Integer aHeight) {
		height = aHeight;
	}

	public void initAutoSize() {
		setHeight(AUTOSIZE_CONSTANT);
		setWidth(AUTOSIZE_CONSTANT);
	}

	public boolean getAutoSize() {
		return (AUTOSIZE_CONSTANT.equals(width)) && (AUTOSIZE_CONSTANT.equals(height));
	}

}