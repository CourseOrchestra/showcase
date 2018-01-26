package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Исключение при чтении файла с настройками приложения.
 * 
 * @author den
 * 
 */
public class SettingsFileOpenException extends BaseException {

	private static final long serialVersionUID = 519101136526014887L;

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = "%s '%s' не найден или поврежден";

	/**
	 * Имя файла.
	 */
	private final String fileName;

	/**
	 * Тип файла.
	 */
	private final SettingsFileType fileType;

	public SettingsFileOpenException(final Throwable cause, final String aFileName,
			final SettingsFileType aFileType) {
		this(generateMessage(aFileName, aFileType), cause, aFileName, aFileType);
	}

	public SettingsFileOpenException(final String aFileName, final SettingsFileType aFileType) {
		this(generateMessage(aFileName, aFileType), aFileName, aFileType);
	}

	protected SettingsFileOpenException(final String error, final String aFileName,
			final SettingsFileType aFileType) {
		super(ExceptionType.SOLUTION, error);
		fileName = aFileName;
		fileType = aFileType;
	}

	protected SettingsFileOpenException(final String error, final Throwable cause,
			final String aFileName, final SettingsFileType aFileType) {
		super(ExceptionType.SOLUTION, error, cause);
		fileName = aFileName;
		fileType = aFileType;
	}

	private static String generateMessage(final String aFileName, final SettingsFileType aType) {
		return String.format(ERROR_MES, aType.getName(), aFileName);
	}

	public String getFileName() {
		return fileName;
	}

	public SettingsFileType getFileType() {
		return fileType;
	}
}
