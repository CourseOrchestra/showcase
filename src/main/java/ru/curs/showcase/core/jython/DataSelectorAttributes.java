package ru.curs.showcase.core.jython;

/**
 * Входные атрибуты получения данных для селектора.
 * 
 * @author bogatov
 * 
 */
public class DataSelectorAttributes extends CountSelectorAttributes {
	private int firstRecord;
	private int recordCount;

	public int getFirstRecord() {
		return firstRecord;
	}

	public void setFirstRecord(final int aFirstRecord) {
		this.firstRecord = aFirstRecord;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(final int aRecordCount) {
		this.recordCount = aRecordCount;
	}
}
