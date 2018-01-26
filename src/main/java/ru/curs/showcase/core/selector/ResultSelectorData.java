package ru.curs.showcase.core.selector;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.selector.DataRecord;

/**
 * Результат получения данных селектора.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultSelectorData implements SerializableElement {
	private static final long serialVersionUID = 1L;
	private final List<DataRecord> dataRecordList;
	private final int count;

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	public ResultSelectorData() {
		super();
		this.dataRecordList = new ArrayList<>();
		this.count = 0;
	}

	public ResultSelectorData(final List<DataRecord> aDataRecordList, final int aCount) {
		super();
		this.dataRecordList = aDataRecordList;
		this.count = aCount;
	}

	public ResultSelectorData(final DataRecord[] aDataRecordArray, final int aCount) {
		super();
		if (aDataRecordArray != null) {
			this.dataRecordList = Arrays.asList(aDataRecordArray);
		} else {
			this.dataRecordList = null;
		}
		this.count = aCount;
	}

	public List<DataRecord> getDataRecordList() {
		return dataRecordList;
	}

	public int getCount() {
		return count;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

}
