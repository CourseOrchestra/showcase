package ru.curs.showcase.app.api.common;

/**
 * Обработчик событий об изменении действия. User: Inc Date: 05.03.2010 Time:
 * 19:37:51
 */
public interface ActionListener {
	/**
	 * Состояние enabled изменено.
	 * 
	 * @param a
	 *            действие
	 */
	void enabledChanged(Action a);
}
