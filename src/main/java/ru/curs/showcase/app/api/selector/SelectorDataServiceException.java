package ru.curs.showcase.app.api.selector;

/**
 * Исключение, генерируемое в случае ошибки получения данных для селектора.
 * 
 */
public class SelectorDataServiceException extends RuntimeException {
	private static final long serialVersionUID = 2427492360531299691L;

	public SelectorDataServiceException() {
		super();
	}

	public SelectorDataServiceException(final String message) {
		super(message);
	}

}
