package ru.curs.showcase.core.jython;

/**
 * Объект возвращается из Jython скрипта в случае возникновение ошибки.
 * 
 * @author bogatov
 * 
 */
public class JythonErrorResult {
	private final String message;
	private final int errorCode;

	public JythonErrorResult() {
		this.message = null;
		this.errorCode = 0;
	}

	public JythonErrorResult(final String aMessage, final int aErrorCode) {
		super();
		this.message = aMessage;
		this.errorCode = aErrorCode;
	}

	public String getMessage() {
		return message;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
