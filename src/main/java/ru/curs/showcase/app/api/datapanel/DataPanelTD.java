package ru.curs.showcase.app.api.datapanel;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Аналог HTML td для группировки элементов на вкладке.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "td")
@XmlAccessorType(XmlAccessType.FIELD)
public final class DataPanelTD extends HTMLElement implements SerializableElement {

	private static final long serialVersionUID = -6437752427735919361L;

	private String width;

	private Integer rowspan;

	private Integer colspan;

	private DataPanelElementInfo element;

	/**
	 * Ссылка на строку таблицы, в которой расположена ячейка.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private DataPanelTR tr;

	public DataPanelTD(final DataPanelTR aTR) {
		super();
		tr = aTR;
	}

	public DataPanelTD() {
		super();
	}

	public String getHeight() {
		if (tr != null) {
			return tr.getHeight();
		}
		return null;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(final String aWidth) {
		width = aWidth;
	}

	public Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(final Integer aRowspan) {
		rowspan = aRowspan;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(final Integer aColspan) {
		colspan = aColspan;
	}

	public DataPanelTR getTR() {
		return tr;
	}

	public void setTR(final DataPanelTR aTR) {
		tr = aTR;
	}

	public DataPanelElementInfo add(final String aId, final DataPanelElementType aType) {
		element = new DataPanelElementInfo(aId, aType);
		element.setTab(tr.getTab());
		return element;
	}

	public DataPanelElementInfo getElement() {
		return element;
	}

	public void setElement(final DataPanelElementInfo aElement) {
		element = aElement;
	}

	public DataPanelTR getTr() {
		return tr;
	}

	public void setTr(final DataPanelTR aTr) {
		tr = aTr;
	}

}
