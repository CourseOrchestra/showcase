package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Виртуальный столбец.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualColumn implements SerializableElement {
	private static final long serialVersionUID = 3527864983485915432L;

	@XmlAttribute
	private String id = null;

	@XmlAttribute
	private String parentId = null;

	@XmlAttribute
	private String width = null;

	@XmlAttribute
	private String style = null;

	@XmlAttribute
	private VirtualColumnType virtualColumnType = null;

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(final String aParentId) {
		parentId = aParentId;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(final String aWidth) {
		width = aWidth;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(final String aStyle) {
		style = aStyle;
	}

	public VirtualColumnType getVirtualColumnType() {
		return virtualColumnType;
	}

	public void setVirtualColumnType(final VirtualColumnType aVirtualColumnType) {
		virtualColumnType = aVirtualColumnType;
	}

}
