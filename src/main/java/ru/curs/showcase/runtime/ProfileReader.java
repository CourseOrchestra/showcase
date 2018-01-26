package ru.curs.showcase.runtime;

import java.io.*;
import java.util.Properties;

import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Базовый класс для чтения профайлов настроек.
 * 
 * @author den
 * 
 */
public class ProfileReader {
	/**
	 * Properties-файл с настройками.
	 */
	private final Properties props = new Properties();
	/**
	 * Путь к профайлу с настройками.
	 */
	private final String profileName;

	private final SettingsFileType settingsFileType;

	public ProfileReader(final String aProfile, final SettingsFileType aSettingsFileType) {
		super();
		settingsFileType = aSettingsFileType;
		profileName = generateProfileName(aProfile);
	}

	public void init() {
		try {
			InputStream is = null;
			if ((new File(UserDataUtils.getUserDataCatalog() + "/" + profileName)).exists()) {
				is = UserDataUtils.loadUserDataToStream(profileName);
			} else {
				is = UserDataUtils.loadGeneralToStream(profileName);
			}
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				props.load(reader);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, profileName, getSettingsType());
		}
	}

	public final String getProfileCatalog() {
		return getSettingsType().getFileDir();
	}

	public SettingsFileType getSettingsType() {
		return settingsFileType;
	}

	private String generateProfileName(final String aProfile) {
		if (!getProfileCatalog().isEmpty()) {
			return getProfileCatalog() + "/" + aProfile;
		} else {
			return aProfile;
		}
	}

	public Properties getProps() {
		return props;
	}

	public String getProfileName() {
		return profileName;
	}

	/**
	 * Получает строковое значение параметра по его имени.
	 * 
	 * @param propName
	 *            Название настройки
	 * 
	 * @return Значение настройки
	 * 
	 */
	public String getStringValue(final String propName) {

		String result = props.getProperty(propName);
		if (result != null) {
			result = result.trim();
		}
		return result;

	}

	/**
	 * Стандартная функция для чтения Integer значения из файла настроек.
	 * 
	 * @param paramName
	 *            - имя параметра в файле.
	 * @return - значение параметра.
	 */
	public Integer getIntValue(final String paramName) {
		Integer result = null;
		String value = null;
		value = getStringValue(paramName);
		if (value != null) {
			try {
				result = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new SettingsFilePropValueFormatException(e, profileName, paramName,
						getSettingsType());
			}
		}
		return result;
	}

	/**
	 * Стандартная функция для чтения Boolean значения из файла настроек.
	 * 
	 * @param paramName
	 *            - имя параметра в файле.
	 * @return - значение параметра.
	 */
	public Boolean getBoolValue(final String paramName) {
		Boolean result = null;
		String value = null;
		value = getStringValue(paramName);
		if (value != null) {
			if (!("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
				throw new SettingsFilePropValueFormatException(profileName, paramName,
						getSettingsType());
			}
			result = Boolean.valueOf(value);
		}
		return result;
	}

	/**
	 * Определение того, что в значении параметра содержится true.
	 * 
	 * @param paramName
	 *            - имя параметра.
	 */
	public boolean isTrueValue(final String paramName) {
		Boolean value = getBoolValue(paramName);
		return (value != null) && value;
	}

}