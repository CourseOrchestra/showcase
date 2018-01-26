package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Класс, содержащий стандартные HTML атрибуты, которые будут отображены на
 * соответствующие графические элементы.
 * 
 * @author den
 * 
 */
public class HTMLAttrs implements SerializableElement {

	private static final long serialVersionUID = -8582791141472066462L;

	private String style;

	private String styleClass;

	public String getStyle() {
		return style;
	}

	public void setStyle(final String aStyle) {
		style = aStyle;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(final String aStyleClass) {
		styleClass = aStyleClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		result = prime * result + ((styleClass == null) ? 0 : styleClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HTMLAttrs)) {
			return false;
		}
		HTMLAttrs other = (HTMLAttrs) obj;
		if (style == null) {
			if (other.style != null) {
				return false;
			}
		} else if (!style.equals(other.style)) {
			return false;
		}
		if (styleClass == null) {
			if (other.styleClass != null) {
				return false;
			}
		} else if (!styleClass.equals(other.styleClass)) {
			return false;
		}
		return true;
	}
}
