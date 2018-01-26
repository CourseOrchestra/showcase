package ru.curs.showcase.app.api.element;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.*;

/**
 * Класс для "пустых" элементов информационной панели (Отработка серверного
 * действия, сохранение xforms).
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VoidElement implements SerializableElement {

	private static final long serialVersionUID = -5903151672960225408L;

	/**
	 * "ok"-сообщение.
	 */
	private UserMessage okMessage = null;

	public VoidElement() {
		super();
	}

	public VoidElement(final UserMessage aOkMessage) {
		super();
		okMessage = aOkMessage;
	}

	public UserMessage getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(final UserMessage aOkMessage) {
		okMessage = aOkMessage;
	}

}
