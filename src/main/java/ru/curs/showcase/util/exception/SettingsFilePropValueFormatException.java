package ru.curs.showcase.util.exception;

/**
 * Исключение, возникающее в случае неверного формата значений параметров в
 * файлах настроек приложения.
 * 
 * @author den
 * 
 */
public class SettingsFilePropValueFormatException extends SettingsFileOpenException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES =
		"%s '%s' содержит параметр '%s' c неверным форматом значения";

	private static final long serialVersionUID = -8355753684819986193L;

	/**
	 * Имя свойства.
	 */
	private final String propName;

	public SettingsFilePropValueFormatException(final Throwable aCause, final String aFileName,
			final String aPropName, final SettingsFileType aType) {
		super(generateMessage(ERROR_MES, aFileName, aPropName, aType), aCause, aFileName, aType);
		propName = aPropName;
	}

	public SettingsFilePropValueFormatException(final String aFileName, final String aPropName,
			final SettingsFileType aType) {
		this(aFileName, aPropName, aType, ERROR_MES);
	}

	protected SettingsFilePropValueFormatException(final String aFileName, final String aPropName,
			final SettingsFileType aType, final String aTemplate) {
		super(generateMessage(aTemplate, aFileName, aPropName, aType), aFileName, aType);
		propName = aPropName;
	}

	private static String generateMessage(final String template, final String aFileName,
			final String aPropName, final SettingsFileType aType) {
		return String.format(template, aType.getName(), aFileName, aPropName);
	}

	public String getPropName() {
		return propName;
	}

}
