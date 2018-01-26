package ru.curs.showcase.core.selector;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Результат получения данных нового триселектора.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultTreeSelectorData implements SerializableElement {
	private static final long serialVersionUID = 1L;
	private String data;

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	public ResultTreeSelectorData() {

	}

	public ResultTreeSelectorData(final String sData) {
		super();
		this.data = sData;
	}

	public String getData() {
		return data;
	}

	public void setData(final String sData) {
		this.data = sData;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

}
