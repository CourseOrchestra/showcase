package ru.curs.showcase.app.api;

/**
 * Тип сообщения.
 * 
 * @author den
 * 
 */
public enum MessageType implements SerializableElement {
	/**
	 * Информационное сообщение.
	 */
	INFO("Информация"),
	/**
	 * Предупреждение.
	 */
	WARNING("Предупреждение"),
	/**
	 * Ошибка.
	 */
	ERROR("Ошибка");

	/**
	 * Наименование типа.
	 */
	private String name;

	private MessageType(final String aName) {
		name = aName;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}
}
