package ru.curs.showcase.core.jython;

/**
 * Входные атрибуты получения общего числа записей для селектора.
 * 
 * @author bogatov
 * 
 */
public class GridDownloadAttributes extends InputAttributes {
	private String elementId;
	private String recordId;

	public String getElementId() {
		return elementId;
	}

	public void setElementId(final String aElementId) {
		this.elementId = aElementId;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(final String aRecordId) {
		this.recordId = aRecordId;
	}

}
