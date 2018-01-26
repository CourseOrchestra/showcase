package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Класс для результатов сохранения отредактированных данных в гриде.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GridSaveResult implements SerializableElement {
	private static final long serialVersionUID = 830182167624371725L;

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	/**
	 * Надо ли обновлять грид после сохранения отредактированных значений.
	 */
	private boolean refreshAfterSave = false;

	public GridSaveResult() {
		super();
	}

	public GridSaveResult(final UserMessage aOkMessage) {
		super();
		okMessage = aOkMessage;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

	public boolean isRefreshAfterSave() {
		return refreshAfterSave;
	}

	public void setRefreshAfterSave(final boolean aRefreshAfterSave) {
		refreshAfterSave = aRefreshAfterSave;
	}

}
