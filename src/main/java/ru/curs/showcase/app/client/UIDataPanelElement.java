package ru.curs.showcase.app.client;

import ru.curs.showcase.app.client.api.BasicElementPanel;

/**
 * @author anlug
 * 
 */
public class UIDataPanelElement {

	UIDataPanelElement(final BasicElementPanel aobj
	/*
	 * , final DataPanelElement adataPanelElementMetaData, final Widget
	 * awidgetDataPanelElement
	 */) {
		// this.setDataPanelElementMetaData(adataPanelElementMetaData);
		// this.setWidgetDataPanelElement(awidgetDataPanelElement);
		this.setElementPanel(aobj);
	}

	/**
	 * метаданные элемента.
	 */
	// private DataPanelElement dataPanelElementMetaData;

	/**
	 * 
	 */
	private BasicElementPanel elementPanel;

	/**
	 * @return the obj
	 */
	public BasicElementPanel getElementPanel() {
		return elementPanel;
	}

	/**
	 * @param aelementPanel
	 *            the obj to set
	 */
	public final void setElementPanel(final BasicElementPanel aelementPanel) {
		this.elementPanel = aelementPanel;
	}

	/**
	 * виджет соответствующего элемента в Tabe (закладке).
	 */
	// private Widget widgetDataPanelElement;

	// public void setDataPanelElementMetaData(final DataPanelElement
	// adataPanelElementMetaData) {
	// this.dataPanelElementMetaData = adataPanelElementMetaData;
	// }

	// public DataPanelElement getDataPanelElementMetaData() {
	// return dataPanelElementMetaData;
	// }

	// public void setWidgetDataPanelElement(final Widget
	// awidgetDataPanelElement) {
	// this.widgetDataPanelElement = awidgetDataPanelElement;
	// }

	// public Widget getWidgetDataPanelElement() {
	// return widgetDataPanelElement;
	// }

}
