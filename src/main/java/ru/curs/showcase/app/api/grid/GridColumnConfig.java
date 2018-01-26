package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Столбец.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GridColumnConfig implements SerializableElement {

	private static final long serialVersionUID = -5349384847976436436L;

	@XmlAttribute
	private String id = null;
	@XmlAttribute
	private String parentId = null;
	@XmlAttribute
	private String caption = null;
	@XmlAttribute
	private boolean visible = true;
	@XmlAttribute
	private boolean readonly = false;
	@XmlAttribute
	private String editor = null;
	@XmlAttribute
	private Integer width = null;
	@XmlAttribute
	private GridValueType valueType = null;
	@XmlAttribute
	private String format = null;
	@XmlAttribute
	private HorizontalAlignment horizontalAlignment = null;
	@XmlAttribute
	private Sorting firstSortDirection = Sorting.ASC;

	/**
	 * Ссылка на процедуру загрузки файла.
	 */
	@XmlAttribute
	private String linkId = null;

	public GridColumnConfig() {
	}

	public GridColumnConfig(final String aId, final String aCaption, final Integer aWidth) {
		id = aId;
		caption = aCaption;
		width = aWidth;
	}

	public final GridValueType getValueType() {
		return valueType;
	}

	public final void setValueType(final GridValueType aValueType) {
		valueType = aValueType;
	}

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

	public String getCaption() {
		return caption;
	}

	public void setCaption(final String aCaption) {
		caption = aCaption;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(final boolean aVisible) {
		visible = aVisible;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(final boolean aReadonly) {
		readonly = aReadonly;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(final String aEditor) {
		editor = aEditor;
	}

	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(final HorizontalAlignment aHorizontalAlignment) {
		horizontalAlignment = aHorizontalAlignment;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(final Integer aWidth) {
		width = aWidth;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(final String aLinkId) {
		linkId = aLinkId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(final String aFormat) {
		format = aFormat;
	}

	public Sorting getFirstSortDirection() {
		return firstSortDirection;
	}

	public void setFirstSortDirection(final Sorting aFirstSortDirection) {
		firstSortDirection = aFirstSortDirection;
	}

}
