package ru.curs.showcase.util.xml;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Специальный класс ошибки для передачи наверх любого исключения в обработчике
 * SAX парсера - замена SAXException. Последний не удобен тем, что является
 * контролируемым.
 * 
 * @author den
 * 
 */
public final class SAXError extends BaseException {

	public SAXError(final Throwable aCause) {
		super(ExceptionType.APP, aCause);
	}

	private static final long serialVersionUID = 5024218668352683986L;

}
