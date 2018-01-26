package ru.curs.showcase.app.api.element;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;

/**
 * Абстрактный класс элемента информационной панели, в основе которого лежит
 * некий компонент (сделанный по технологии GWT или JS) и имеющий Header и
 * Footer.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DataPanelCompBasedElement extends DataPanelElement {

	private static final long serialVersionUID = 8093132785939139397L;

	/**
	 * Заголовок перед элементом.
	 */
	private String header = "";

	/**
	 * Подпись под элементом.
	 */
	private String footer = "";

	public final String getFooter() {
		return footer;
	}

	public final void setFooter(final String aFooter) {
		footer = aFooter;
	}

	public final String getHeader() {
		return header;
	}

	public final void setHeader(final String aHeader) {
		header = aHeader;
	}

	public DataPanelCompBasedElement() {
		super();
	}

	public DataPanelCompBasedElement(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}
}
