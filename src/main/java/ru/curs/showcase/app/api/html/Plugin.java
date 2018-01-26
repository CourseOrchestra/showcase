package ru.curs.showcase.app.api.html;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.event.Event;

/**
 * Класс UI плагина. Плагин является элементом информационной панели. Является
 * адаптером для произвольных JS и Flash компонент.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Plugin extends DataPanelElement {

	private static final long serialVersionUID = 6296217539597232694L;

	private String createProc;

	private List<String> params = new ArrayList<String>();

	private List<String> requiredJS = new ArrayList<String>();

	private List<String> requiredCSS = new ArrayList<String>();

	private StringSize stringSize = new StringSize();

	@Override
	protected EventManager<? extends Event> initEventManager() {
		return new HTMLEventManager();
	}

	@Override
	public HTMLEventManager getEventManager() {
		return (HTMLEventManager) super.getEventManager();
	}

	public String getCreateProc() {
		return createProc;
	}

	public void setCreateProc(final String aCreateProc) {
		createProc = aCreateProc;
	}

	public List<String> getRequiredJS() {
		return requiredJS;
	}

	public void setRequiredJS(final List<String> aRequiredJS) {
		requiredJS = aRequiredJS;
	}

	public List<String> getRequiredCSS() {
		return requiredCSS;
	}

	public void setRequiredCSS(final List<String> aRequiredCSS) {
		requiredCSS = aRequiredCSS;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(final List<String> aParams) {
		params = aParams;
	}

	public Plugin() {
		super();
	}

	public Plugin(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

	public StringSize getStringSize() {
		return stringSize;
	}

	public void setStringSize(final StringSize aStringSize) {
		stringSize = aStringSize;
	}
}
