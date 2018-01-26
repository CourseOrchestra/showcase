package ru.curs.showcase.core.plugin;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Результат получения данных плагина.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultPluginData implements SerializableElement {
	private static final long serialVersionUID = 1L;
	private String data;

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	public ResultPluginData() {

	}

	public ResultPluginData(final String sData) {
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
