package ru.curs.showcase.util.exception;

/**
 * Исключение, генерируемое при отсутствии в файле настроек приложения
 * необходимого свойства.
 * 
 * @author den
 * 
 */
public final class SettingsFileRequiredPropException extends SettingsFilePropValueFormatException {

	private static final long serialVersionUID = -2886682990651933862L;

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = " %s '%s' не содержит требуемого параметра '%s'";

	public SettingsFileRequiredPropException(final String aFileName, final String aPropName,
			final SettingsFileType aType) {
		super(aFileName, aPropName, aType, ERROR_MES);
	}
}
