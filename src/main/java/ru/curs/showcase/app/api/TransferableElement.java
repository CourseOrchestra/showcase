package ru.curs.showcase.app.api;

import com.google.gwt.user.client.rpc.*;

/**
 * Элемент, умеющий кодировать свое состояние в формате, допустимом для
 * использования в HTTP GET-запросе.
 * 
 * @author den
 * 
 */
public abstract class TransferableElement {

	/**
	 * Сериализация объекта для отправки его на сервер. В текущей реализации
	 * используется GWT механизм сериализации + URL кодирование (URL Encode).
	 * 
	 * @return строка с сериализованным объектом.
	 * @throws SerializationException
	 * @param factory
	 *            - фабрика для сериализации
	 */
	public String toParamForHttpPost(final Object factory) throws SerializationException {
		SerializationStreamWriter writer =
			((SerializationStreamFactory) factory).createStreamWriter();
		writer.writeObject(this);
		return writer.toString();
	}
}
