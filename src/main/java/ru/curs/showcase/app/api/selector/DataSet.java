package ru.curs.showcase.app.api.selector;

import java.io.Serializable;
import java.util.List;

import ru.curs.showcase.app.api.UserMessage;

/**
 * Набор данных, возвращаемых сервером.
 */
public class DataSet implements Serializable {
	/**
	 * serialVersionUID.
	 * 
	 */
	private static final long serialVersionUID = 3810355718345205297L;

	/**
	 * records.
	 * 
	 */
	private List<DataRecord> records;

	/**
	 * firstRecord.
	 * 
	 */
	private int firstRecord;
	/**
	 * totalCount.
	 * 
	 */
	private int totalCount;

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	/**
	 * Записи в наборе данных (та часть полного набора, которая будет
	 * отображаться пользователю).
	 * 
	 * @return records
	 */
	public List<DataRecord> getRecords() {
		return records;
	}

	/**
	 * Устанавливает набор записей.
	 * 
	 * @param records1
	 *            набор записей
	 */
	public void setRecords(final List<DataRecord> records1) {
		this.records = records1;
	}

	/**
	 * Номер первой записи в полном наборе данных.
	 * 
	 * @return firstRecord
	 */
	public int getFirstRecord() {
		return firstRecord;
	}

	/**
	 * Устанавливает номер первой записи в полном наборе данных.
	 * 
	 * @param firstRecord1
	 *            номер первой записи
	 */
	public void setFirstRecord(final int firstRecord1) {
		this.firstRecord = firstRecord1;
	}

	/**
	 * Общее количество записей в полном наборе данных.
	 * 
	 * @return totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * Устанавливает общее количество записей в полном наборе данных.
	 * 
	 * @param totalCount1
	 *            общее количество записей
	 */
	public void setTotalCount(final int totalCount1) {
		this.totalCount = totalCount1;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

}
