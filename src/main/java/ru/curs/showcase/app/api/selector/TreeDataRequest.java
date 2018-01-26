package ru.curs.showcase.app.api.selector;

import ru.curs.showcase.app.api.SerializableElement;
import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Данные для выполнения запроса к серверу для нового триселектора.
 * 
 */
public class TreeDataRequest implements SerializableElement {
	private static final long serialVersionUID = 4230779299523949392L;

	private CompositeContext context = null;
	private PluginInfo elInfo = null;

	private String params = null;
	private String curValue = null;
	private boolean startsWith = false;
	private String parentId = null;

	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}

	public PluginInfo getElInfo() {
		return elInfo;
	}

	public void setElInfo(final PluginInfo aElInfo) {
		elInfo = aElInfo;
	}

	public String getParams() {
		return params;
	}

	public void setParams(final String aParams) {
		params = aParams;
	}

	public String getCurValue() {
		return curValue;
	}

	public void setCurValue(final String aCurValue) {
		curValue = aCurValue;
	}

	public boolean isStartsWith() {
		return startsWith;
	}

	public void setStartsWith(final boolean aStartsWith) {
		startsWith = aStartsWith;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(final String aParentId) {
		parentId = aParentId;
	}

}
