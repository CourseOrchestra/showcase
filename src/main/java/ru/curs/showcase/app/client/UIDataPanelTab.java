/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.datapanel.DataPanelTab;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author anlug
 * 
 */
public class UIDataPanelTab {

	UIDataPanelTab(final DataPanelTab adataPanelTabMetaData, final Widget awidgetDataPanelTab) {
		this.setDataPanelTabMetaData(adataPanelTabMetaData);
		this.setWidgetDatapanelTab(awidgetDataPanelTab);
	}

	/**
	 * метаданные таба.
	 */
	private DataPanelTab dataPanelTabMetaData;

	/**
	 * виджет соответствующего таба информационной панели.
	 */
	private Widget widgetDataPanelTab;

	/**
	 * @return the dataPanelTabMetaData
	 */
	public DataPanelTab getDataPanelTabMetaData() {
		return dataPanelTabMetaData;
	}

	/**
	 * @param adataPanelTabMetaData
	 *            the dataPanelTabMetaData to set
	 */
	public final void setDataPanelTabMetaData(final DataPanelTab adataPanelTabMetaData) {
		this.dataPanelTabMetaData = adataPanelTabMetaData;
	}

	/**
	 * Элемент пользовательского интерфейса.
	 */
	private List<UIDataPanelElement> uiElements = new ArrayList<UIDataPanelElement>();

	/**
	 * @return the uiElements
	 */
	public List<UIDataPanelElement> getUiElements() {
		return uiElements;
	}

	/**
	 * @param auiElements
	 *            the uiElements to set
	 */
	public void setUiElements(final List<UIDataPanelElement> auiElements) {
		this.uiElements = auiElements;
	}

	public final void setWidgetDatapanelTab(final Widget awidgetDataPanelTab) {
		this.widgetDataPanelTab = awidgetDataPanelTab;
	}

	public Widget getWidgetDatapanelTab() {
		return widgetDataPanelTab;
	}
	//
	// public void add(DataPanelTab adataPanelTabMetaData, Widget
	// awidgetDataPanelTab) {
	//
	// this.setDataPanelTabMetaData(adataPanelTabMetaData);
	// this.setWidgetDatapanelTab(awidgetDataPanelTab);
	//
	// }

}
