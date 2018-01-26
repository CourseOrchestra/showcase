package ru.curs.showcase.util.alfresco;

/**
 * Базовый класс для результатов вызова функций интеграции с Alfresco.
 * 
 */
public class AlfrescoBaseResult {

	private int result = 1;
	private String errorMessage = null;

	public int getResult() {
		return result;
	}

	public void setResult(final int aResult) {
		result = aResult;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(final String aErrorMessage) {
		errorMessage = aErrorMessage;
	}

}
