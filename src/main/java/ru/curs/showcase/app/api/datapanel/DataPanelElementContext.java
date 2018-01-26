package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Информация об элементе и контексте его создания.
 * 
 * @author den
 * 
 */
public class DataPanelElementContext implements SerializableElement {

	private static final long serialVersionUID = -2803616788574282075L;

	/**
	 * Контекст.
	 */
	private CompositeContext compositeContext;

	/**
	 * Сохраняем ссылку на случай запроса доп. информации в процедуре обработки
	 * исключений.
	 */
	private DataPanelElementInfo elementInfo;

	public CompositeContext getCompositeContext() {
		return compositeContext;
	}

	public DataPanelElementContext() {
		super();
	}

	public void setCompositeContext(final CompositeContext aContext) {
		compositeContext = aContext;
	}

	@Override
	public String toString() {
		String res = "";
		if (compositeContext != null) {
			res = compositeContext.toString();
		}
		if (elementInfo != null) {
			res = res + ExchangeConstants.LINE_SEPARATOR + "panel=" + getPanelId().getString()
					+ ExchangeConstants.LINE_SEPARATOR + "elementId=" + getElementId().getString()
					+ ExchangeConstants.LINE_SEPARATOR;
		}
		return res;
	}

	public DataPanelElementContext(final CompositeContext aContext,
			final DataPanelElementInfo dpei) {
		super();
		compositeContext = aContext;
		elementInfo = dpei;
	}

	public DataPanelElementContext(final CompositeContext aContext) {
		super();
		compositeContext = aContext;
	}

	public ID getPanelId() {
		if (elementInfo != null) {
			return elementInfo.getTab().getDataPanel().getId();
		}
		return null;
	}

	public ID getElementId() {
		if (elementInfo != null) {
			return elementInfo.getId();
		}
		return null;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
	}
}
