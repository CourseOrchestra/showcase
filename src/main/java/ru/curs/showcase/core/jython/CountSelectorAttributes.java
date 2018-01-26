package ru.curs.showcase.core.jython;

/**
 * Входные атрибуты получения общего числа записей для селектора.
 * 
 * @author bogatov
 * 
 */
public class CountSelectorAttributes extends InputAttributes {
	private String params;
	private String curValue;
	private boolean startsWith;

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
		this.curValue = aCurValue;
	}

	public boolean getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(final boolean aStartsWith) {
		this.startsWith = aStartsWith;
	}
}
