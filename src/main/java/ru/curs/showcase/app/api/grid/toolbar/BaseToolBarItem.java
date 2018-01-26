package ru.curs.showcase.app.api.grid.toolbar;

/**
 * Абстрактный базовый класс элемента (элемент, группа) панели инструментов
 * грида.
 * 
 * @author bogatov
 * 
 */
public abstract class BaseToolBarItem extends AbstractToolBarItem {
	private static final long serialVersionUID = 1L;
	private String text = "";
	private String img;
	private Boolean visible = Boolean.TRUE;
	private Boolean disable = Boolean.FALSE;
	private String hint = "";
	private String style = "";
	private String className = "";
	private String iconClassName = "";
	private String id = null;
	private String popupText = null;

	public String getText() {
		return text;
	}

	public void setText(final String sText) {
		this.text = sText;
	}

	public String getImg() {
		return img;
	}

	public void setImg(final String sImg) {
		this.img = sImg;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(final Boolean bVisible) {
		this.visible = bVisible != null ? bVisible : Boolean.TRUE;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(final Boolean bDisable) {
		this.disable = bDisable != null ? bDisable : Boolean.FALSE;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(final String sHint) {
		this.hint = sHint;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(final String aStyle) {
		style = aStyle;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(final String aClassName) {
		className = aClassName;
	}

	public String getIconClassName() {
		return iconClassName;
	}

	public void setIconClassName(final String aIconClassName) {
		iconClassName = aIconClassName;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getPopupText() {
		return popupText;
	}

	public void setPopupText(final String aPopupText) {
		popupText = aPopupText;
	}
}
