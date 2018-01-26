package ru.curs.showcase.app.api.common;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;

/**
 * Пользовательское действие (паттерн Action).
 * 
 * User: Inc Date: 04.03.2010 Time: 23:03:33
 */
public interface Action extends Command {

	/**
	 * "Пустое" действие, служащее разделителем в меню.
	 */
	Action SEPARATOR = new ActionSeparator();

	/**
	 * Заголовок действия.
	 * 
	 * @return - текст.
	 */
	String getText();

	/**
	 * Является ли действие доступным.
	 * 
	 * @return - доступность.
	 */
	boolean isEnabled();

	/**
	 * Иконка действия.
	 * 
	 * @return - иконку.
	 */
	Image getImage();

	/**
	 * Добавляет обработчик, который должен делать доступными или недоступными
	 * соответствующие элементы управления.
	 * 
	 * @param listener
	 *            обработчик
	 */
	void addListener(ActionListener listener);

}
