package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Настройки мультиселектора, используемого при условии фильтра "список
 * значений".
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterMultiselector implements SerializableElement {
	private static final long serialVersionUID = -6208647715370107774L;

	private String windowCaption = null;
	private String dataWidth = null;
	private String dataHeight = null;
	private String selectedDataWidth = null;
	private String visibleRecordCount = null;
	private String procCount = null;
	private String procList = null;
	private String procListAndCount = null;
	private String currentValue = null;
	private boolean manualSearch = false;
	private boolean startWith = true;
	private boolean hideStartsWith = false;
	private boolean needInitSelection = true;

	public String getWindowCaption() {
		return windowCaption;
	}

	public void setWindowCaption(final String aWindowCaption) {
		windowCaption = aWindowCaption;
	}

	public String getDataWidth() {
		return dataWidth;
	}

	public void setDataWidth(final String aDataWidth) {
		dataWidth = aDataWidth;
	}

	public String getDataHeight() {
		return dataHeight;
	}

	public void setDataHeight(final String aDataHeight) {
		dataHeight = aDataHeight;
	}

	public String getSelectedDataWidth() {
		return selectedDataWidth;
	}

	public void setSelectedDataWidth(final String aSelectedDataWidth) {
		selectedDataWidth = aSelectedDataWidth;
	}

	public String getVisibleRecordCount() {
		return visibleRecordCount;
	}

	public void setVisibleRecordCount(final String aVisibleRecordCount) {
		visibleRecordCount = aVisibleRecordCount;
	}

	public String getProcCount() {
		return procCount;
	}

	public void setProcCount(final String aProcCount) {
		procCount = aProcCount;
	}

	public String getProcList() {
		return procList;
	}

	public void setProcList(final String aProcList) {
		procList = aProcList;
	}

	public String getProcListAndCount() {
		return procListAndCount;
	}

	public void setProcListAndCount(final String aProcListAndCount) {
		procListAndCount = aProcListAndCount;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(final String aCurrentValue) {
		currentValue = aCurrentValue;
	}

	public boolean isManualSearch() {
		return manualSearch;
	}

	public void setManualSearch(final boolean aManualSearch) {
		manualSearch = aManualSearch;
	}

	public boolean isStartWith() {
		return startWith;
	}

	public void setStartWith(final boolean aStartWith) {
		startWith = aStartWith;
	}

	public boolean isHideStartsWith() {
		return hideStartsWith;
	}

	public void setHideStartsWith(final boolean aHideStartsWith) {
		hideStartsWith = aHideStartsWith;
	}

	public boolean isNeedInitSelection() {
		return needInitSelection;
	}

	public void setNeedInitSelection(final boolean aNeedInitSelection) {
		needInitSelection = aNeedInitSelection;
	}
}
