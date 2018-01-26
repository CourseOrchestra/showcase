package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Ошибка при чтении тестового файла.
 * 
 * @author den
 * 
 */
public final class SettingsFileExchangeException extends BaseException {

	private static final String ERROR_MES =
		"%s '%s' - ошибка при обмене данными. Возможно файл поврежден или указан ошибочно.";

	private static final long serialVersionUID = 6810662604700277735L;

	public SettingsFileExchangeException(final String fileName, final Throwable aCause,
			final SettingsFileType settingsType) {
		super(ExceptionType.SOLUTION, String.format(ERROR_MES, settingsType.getName(), fileName),
				aCause);
	}

}
