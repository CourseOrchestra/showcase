package ru.curs.showcase.core;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.ObjectSerializer;
import ru.curs.showcase.util.exception.BaseException;
import ru.curs.showcase.util.xml.XMLObjectSerializer;

/**
 * Исключение, вызванное тем, что на сервер из клиентской части или из слоя
 * связи с данными передан элемент с некорректным состоянием.
 * 
 * @author den
 * 
 */
public class IncorrectElementException extends BaseException {

	private static final long serialVersionUID = 2762191427245015158L;

	public IncorrectElementException(final String message) {
		super(ExceptionType.SOLUTION, message);
	}

	public IncorrectElementException(final String message, final Object obj) {
		super(ExceptionType.SOLUTION, message + generateMessage(obj));
	}

	private static Object generateMessage(final Object obj) {
		ObjectSerializer serializer = new XMLObjectSerializer();
		return serializer.serialize(obj);
	}

}
