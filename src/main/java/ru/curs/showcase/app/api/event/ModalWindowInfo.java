package ru.curs.showcase.app.api.event;

import ru.curs.showcase.app.api.*;

/**
 * Информация о модальном окне, которое должно быть отображено при выполнении
 * действия.
 * 
 * @author den
 * 
 */
public final class ModalWindowInfo implements SerializableElement, GWTClonable, SizeEstimate {

	private static final long serialVersionUID = 9177503564264889964L;

	/**
	 * Заголовок окна.
	 */
	private String caption;

	/**
	 * Ширина окна.
	 */
	private Integer width;

	/**
	 * Высота окна.
	 */
	private Integer height;

	/**
	 * Признак того, нужно ли отображать кнопку закрытия формы, расположенную
	 * внизу окна.
	 */
	private Boolean showCloseBottomButton = false;

	/**
	 * Признак того, будет срабатывать кнопка ESC клавиатуры для закрытия окна.
	 */
	private Boolean closeOnEsc = true;

	/**
	 * css class, отвечающий за высоту и ширину окна, соответствует атрибуту
	 * class_style действия.
	 */
	private String cssClass = null;

	public String getCaption() {
		return caption;
	}

	public void setCaption(final String aCaption) {
		caption = aCaption;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(final Integer aWidth) {
		width = aWidth;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(final Integer aHeight) {
		height = aHeight;
	}

	public Boolean getShowCloseBottomButton() {
		return showCloseBottomButton;
	}

	public void setShowCloseBottomButton(final Boolean aShowCloseBottomButton) {
		showCloseBottomButton = aShowCloseBottomButton;
	}

	public Boolean getCloseOnEsc() {
		return closeOnEsc;
	}

	public void setCloseOnEsc(final Boolean aCloseOnEsc) {
		closeOnEsc = aCloseOnEsc;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(final String aCssClass) {
		cssClass = aCssClass;
	}

	@Override
	public ModalWindowInfo gwtClone() {
		ModalWindowInfo res = new ModalWindowInfo();
		res.caption = caption;
		res.height = height;
		res.width = width;
		res.showCloseBottomButton = showCloseBottomButton;
		res.closeOnEsc = closeOnEsc;
		res.cssClass = cssClass;
		return res;
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		if (caption != null) {
			result += caption.length();
		}
		if (height != null) {
			result += Integer.SIZE / Byte.SIZE;
		}
		if (width != null) {
			result += Integer.SIZE / Byte.SIZE;
		}
		return result;
	}
}
