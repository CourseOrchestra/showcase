package ru.curs.showcase.core.jython;

import ru.curs.showcase.app.api.UserMessage;

/**
 * DTO класс с сырыми данными для элементов Showcase: навигатора, инф. панели
 * или ее элементов. Данные передаются в виде строк.
 * 
 * @author den
 * 
 */
public final class JythonDTO {
	/**
	 * Данные (в формате HTML или XML).
	 */
	private String data;

	/**
	 * Массив с данными. Используется вместо data.
	 */
	private String[] dataArray;
	/**
	 * Настройки элемента в формате XML.
	 */
	private String settings;

	/**
	 * Основное назначение -- выдача "ok"-сообщений.
	 */
	private UserMessage userMessage = null;

	public String getData() {
		return data;
	}

	public void setData(final String aData) {
		data = aData;
	}

	public String getSettings() {
		return settings;
	}

	public void setSettings(final String aSettings) {
		settings = aSettings;
	}

	public JythonDTO(final String aData, final String aSettings) {
		super();
		data = aData;
		settings = aSettings;
	}

	public JythonDTO(final String aData, final String aSettings, final UserMessage aUserMessage) {
		this(aData, aSettings);
		userMessage = aUserMessage;
	}

	public JythonDTO(final String[] aData, final String aSettings) {
		super();
		dataArray = aData;
		settings = aSettings;
	}

	public JythonDTO(final String[] aData, final String aSettings, final UserMessage aUserMessage) {
		this(aData, aSettings);
		userMessage = aUserMessage;
	}

	public JythonDTO(final String[] aData) {
		super();
		dataArray = aData;
	}

	public JythonDTO(final String[] aData, final UserMessage aUserMessage) {
		this(aData);
		userMessage = aUserMessage;
	}

	public JythonDTO(final String aData) {
		super();
		data = aData;
	}

	public JythonDTO(final String aData, final UserMessage aUserMessage) {
		this(aData);
		userMessage = aUserMessage;
	}

	public String[] getDataArray() {
		return dataArray;
	}

	public void setDataArray(final String[] aDataArray) {
		dataArray = aDataArray;
	}

	public UserMessage getUserMessage() {
		return userMessage;
	}

}
