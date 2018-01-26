package ru.curs.showcase.app.api.datapanel;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Абстрактный класс элемента инф. панели, соответствующего определенному HTML
 * элементу и содержащего базовые HTML атрибуты.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class HTMLElement implements SerializableElement {

	private static final long serialVersionUID = -3437752423735910377L;

	public HTMLElement() {
		super();
	}

	/**
	 * HTML id. In HTML, all values are case-insensitive!
	 */
	private String id;
	private HTMLAttrs htmlAttrs = new HTMLAttrs();

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public HTMLAttrs getHtmlAttrs() {
		return htmlAttrs;
	}

	public void setHtmlAttrs(final HTMLAttrs aHtmlAttrs) {
		htmlAttrs = aHtmlAttrs;
	}

}