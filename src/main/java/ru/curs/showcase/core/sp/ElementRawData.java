package ru.curs.showcase.core.sp;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Базовый класс для сырых данных.
 * 
 * @author den
 * 
 */
public abstract class ElementRawData {

	public ElementRawData(final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext, final InputStream aProps) {
		super();
		elementInfo = aElementInfo;
		callContext = aContext;
		settings = aProps;
	}

	public ElementRawData(final DataPanelElementInfo aElementInfo, final CompositeContext aContext) {
		super();
		elementInfo = aElementInfo;
		callContext = aContext;
	}

	/**
	 * Настройки элемента. Как правило, в формате XML.
	 */
	private InputStream settings;
	/**
	 * Описание элемента.
	 */
	private DataPanelElementInfo elementInfo;
	/**
	 * Контекст, в котором был вызван элемент.
	 */
	private CompositeContext callContext;

	public InputStream getSettings() {
		return settings;
	}

	public void setSettings(final InputStream aSettings) {
		settings = aSettings;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
	}

	public CompositeContext getCallContext() {
		return callContext;
	}

	public void setCallContext(final CompositeContext aCallContext) {
		callContext = aCallContext;
	}

}