package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Аналог HTML tr для группировки элементов на вкладке.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "tr")
@XmlAccessorType(XmlAccessType.FIELD)
public final class DataPanelTR extends HTMLElement implements SerializableElement {

	private static final long serialVersionUID = -5308488538331779152L;;

	private String height;

	private List<DataPanelTD> tds = new ArrayList<DataPanelTD>();

	/**
	 * Ссылка на вкладку панели, на которой расположен элемент.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private DataPanelTab tab;

	public DataPanelTR(final DataPanelTab aTab) {
		super();
		tab = aTab;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(final String aHeight) {
		height = aHeight;
	}

	public List<DataPanelTD> getTds() {
		return tds;
	}

	public void setTds(final List<DataPanelTD> aTds) {
		tds = aTds;
	}

	public DataPanelTab getTab() {
		return tab;
	}

	public void setTab(final DataPanelTab aTab) {
		tab = aTab;
	}

	public DataPanelTD add() {
		DataPanelTD td = new DataPanelTD(this);
		tds.add(td);
		return td;
	}

	public DataPanelTR() {
		super();
	}

}
