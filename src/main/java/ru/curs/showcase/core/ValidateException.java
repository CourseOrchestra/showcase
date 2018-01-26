package ru.curs.showcase.core;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, возникающее при проверке введенных пользователем данных в БД.
 * Исключение можно вызвать, если в тексте сообщения об ошибке из БД будут
 * определенные комбинации символов. Описание выдаваемых пользователю сообщений
 * при таких исключений хранятся в файле в папке userdata. Формат этих файлов
 * следующий: <messages> <messages id="id1" type="ERROR"> Это текст сообщения об
 * ошибке в хранимой процедуре.
 * 
 * @author den
 * 
 */
public final class ValidateException extends BaseException {

	private static final long serialVersionUID = 870894006633410366L;

	/**
	 * Сообщение, выдаваемое пользователю.
	 */
	private final UserMessage userMessage;

	public ValidateException(final UserMessage aUserMessage) {
		super(ExceptionType.USER, aUserMessage.getText());
		userMessage = aUserMessage;
	}

	public UserMessage getUserMessage() {
		return userMessage;
	}

}
