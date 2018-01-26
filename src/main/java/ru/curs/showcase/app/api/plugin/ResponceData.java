package ru.curs.showcase.app.api.plugin;

import java.io.Serializable;

import ru.curs.showcase.app.api.UserMessage;

/**
 * Данные возвращаемые сервером.
 * 
 * @author bogatov
 * 
 */
public class ResponceData implements Serializable {
	private static final long serialVersionUID = 1L;
	private String jsonData;

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(final String sJsonData) {
		this.jsonData = sJsonData;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

}
