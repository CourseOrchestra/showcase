package ru.curs.showcase.core.jython;

/**
 * Входные атрибуты получения даннных для нового триселектора.
 * 
 */
public class DataTreeSelectorAttributes {
	private String params;
	private String curValue;
	private boolean startsWith;
	private String parentId;

	public String getParams() {
		return params;
	}

	public void setParams(final String aParams) {
		this.params = aParams;
	}

	public String getCurValue() {
		return curValue;
	}

	public void setCurValue(final String aCurValue) {
		curValue = aCurValue;
	}

	public boolean getStartsWith() {
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
