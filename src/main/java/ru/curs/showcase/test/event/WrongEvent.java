package ru.curs.showcase.test.event;

import ru.curs.showcase.app.api.event.Event;

/**
 * Специальное событие для тестирования. Конструктор по умолчанию должен
 * выдавать исключение.
 * 
 * @author den
 * 
 */
public class WrongEvent extends Event {

	private static final long serialVersionUID = 4424456086820072208L;

	public WrongEvent() throws IllegalAccessException {
		super();
		throw new IllegalAccessException();
	}

}
