package ru.curs.showcase.app.api.selector;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Запрос данных.
 */
public class DataRequest implements SerializableElement {
	private static final long serialVersionUID = 578677012513550530L;

	private String params;
	private String procName;
	private String curValue;
	private boolean startsWith;
	private int firstRecord;
	private int recordCount;

	private SelectorAdditionalData addData = null;

	public SelectorAdditionalData getAddData() {
		return addData;
	}

	public void setAddData(final SelectorAdditionalData aAddData) {
		addData = aAddData;
	}

	/**
	 * Возвращает общие параметры (общие фильтры) запроса.
	 * 
	 * @return params
	 */
	public String getParams() {
		return params;
	}

	/**
	 * 
	 * Устанавливает общие параметры запроса.
	 * 
	 * @param params1
	 *            параметры
	 */
	public void setParams(final String params1) {
		this.params = params1;
	}

	/**
	 * Возвращает имя процедуры, обеспечивающей данные.
	 * 
	 * @return procName
	 */
	public String getProcName() {
		return procName;
	}

	/**
	 * Устанавливает имя процедуры, обеспечивающей данные.
	 * 
	 * @param procName1
	 *            имя процедуры.
	 */
	public void setProcName(final String procName1) {
		this.procName = procName1;
	}

	/**
	 * Возвращает значение в поле поиска.
	 * 
	 * @return curValue
	 */
	public String getCurValue() {
		return curValue;
	}

	/**
	 * Устанавливает значение в поле поиска.
	 * 
	 * @param curValue1
	 *            значение
	 */
	public void setCurValue(final String curValue1) {
		this.curValue = curValue1;
	}

	/**
	 * Значение флага "Начинается с (Ctrl+F)".
	 * 
	 * @return startsWith
	 */
	public boolean isStartsWith() {
		return startsWith;
	}

	/**
	 * Устанавливает флаг "Начинается с".
	 * 
	 * @param startsWith1
	 *            значение флага
	 */
	public void setStartsWith(final boolean startsWith1) {
		this.startsWith = startsWith1;
	}

	/**
	 * Индекс первой записи в возвращаемом наборе данных (нумерация с нуля).
	 * 
	 * @return firstRecord
	 */
	public int getFirstRecord() {
		return firstRecord;
	}

	/**
	 * Устанавливает индекс первой записи.
	 * 
	 * @param firstRecord1
	 *            индекс первой записи
	 */
	public void setFirstRecord(final int firstRecord1) {
		this.firstRecord = firstRecord1;
	}

	/**
	 * Количество записей, которое нужно вернуть в наборе данных.
	 * 
	 * @return recordCount
	 */
	public int getRecordCount() {
		return recordCount;
	}

	/**
	 * Устанавливает количество записей.
	 * 
	 * @param recordCount1
	 *            количество записей
	 */
	public void setRecordCount(final int recordCount1) {
		this.recordCount = recordCount1;
	}
}
