package ru.curs.showcase.runtime;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.server.internatiolization.CourseLocalization;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

/**
 * Получает настройки приложения приложения из файлов properties в каталоге
 * пользовательских данных (userdatas).
 * 
 */
public final class UserDataUtils {

	public static final String GENERAL_RES_ROOT = "general";

	public static final String IMAGES_IN_GRID_DIR = "images.in.grid.dir";

	public static final String CURRENT_USERDATA_TEMPLATE = "${userdata.dir}";

	public static final String DEF_FOOTER_HEIGTH = "50px";

	public static final String DEF_HEADER_HEIGTH = "50px";

	public static final String HEADER_HEIGHT_PROP = "header.height";

	public static final String CSS_PROC_NAME_PROP = "scc.proc.name";

	public static final String FOOTER_HEIGHT_PROP = "footer.height";

	/**
	 * Каталог на работающем сервере с решениями (сейчас туда копируются
	 * userdata при старте сервера).
	 */
	public static final String SOLUTIONS_DIR = "solutions";

	public static final String RESOURCES_DIR = "resources";

	/**
	 * Часть названия параметров в app.properties, относящихся к системе
	 * аутентификации - mellophone (authserver).
	 */
	// public static final String AUTHSERVERURL_PART = "authserverurl";
	public static final String AUTHSERVERURL_PART = "mellophoneurl";

	/**
	 * Имя файла с настройками приложения.
	 */
	public static final String PROPFILENAME = "app.properties";

	public static final String GENERALPROPFILENAME = "generalapp.properties";

	public static final String XSLTTRANSFORMSFORGRIDDIR = "xslttransformsforgrid";

	public static final String SCHEMASDIR = "schemas";

	public static final String SCRIPTSDIR = "scripts";

	public static final String GRIDDATAXSL = "GridData.xsl";

	public static final String INDEX_WELCOME_TAB_CAPTION = "index.welcometabcaption";

	private static final String NAVIGATOR_ICONS_DIR_NAME = "navigator.icons.dir.name";

	private static final String DIR_IN_SOLUTIONS = SOLUTIONS_DIR + "/%s/%s";

	private static final String WEB_CONSOLE_ADD_TEXT_FILES_PARAM = "web.console.add.text.files";

	private static String generalPropFile;

	private static final String CONNECTION_URL_PARAM = "rdbms.connection.url";
	private static final String CONNECTION_USERNAME_PARAM = "rdbms.connection.username";
	private static final String CONNECTION_PASSWORD_PARAM = "rdbms.connection.password";

	private static final String INDEX_TITLE = "index.title";
	private static final String LOGIN_TITLE = "login.title";

	public static final String RDBMS_PREFIX = "rdbms.";
	public static final String CELESTA_PREFIX = "celesta.";
	public static final String CELESTA_SCORE_PATH = "score.path";
	public static final String CELESTA_SKIP_DBUPDATE = "skip.dbupdate";
	public static final String CELESTA_FORCE_DBINITIALIZE = "force.dbinitialize";
	public static final String CELESTA_LOG_LOGINS = "log.logins";
	// public static final String CELESTA_DATABASE_CLASSNAME =
	// "database.classname";
	// public static final String CELESTA_DATABASE_CONNECTION =
	// "database.connection";
	public static final String CELESTA_PYLIB_PATH = "pylib.path";
	public static final String CELESTA_JAVALIB_PATH = "javalib.path";
	public static final String CELESTA_ALLOW_INDEXED_NULLS = "allow.indexed.nulls";
	public static final String CELESTA_CONNECTION_URL = "connection.url";
	public static final String CELESTA_CONNECTION_USERNAME = "connection.username";
	public static final String CELESTA_CONNECTION_PASSWORD = "connection.password";

	public static final String OAUTH_PREFIX = "oauth2.";
	public static final String OAUTH_AUTHORIZE_URL = "authorize.url";
	public static final String OAUTH_TOLEN_URL = "token.url";
	public static final String OAUTH_CLIENT_ID = "clientId";
	public static final String OAUTH_CLIENT_SECRET = "clientSecret";
	private static Properties generalOauth2Properties = null;
	private static Properties generalSpnegoProperties = null;

	public static final String SPNEGO_PREAUTH_USERNAME = "spnego.preauth.username";
	public static final String SPNEGO_PREAUTH_PASSWORD = "spnego.preauth.password";
	public static final String SPNEGO_ALLOW_BASIC = "spnego.allow.basic";
	public static final String SPNEGO_LOGGER_LEVEL = "spnego.logger.level";

	public static final String INTERFACE_ONLY_DATAPANEL = "interface.onlyDataPanel";
	public static final String IMPORT_STATIC_EXTERNAL_JS_LIBRARIES =
		"import.static.external.js.libraries";
	public static final String IMPORT_STATIC_EXTERNAL_CSS = "import.static.external.css";
	public static final String INTERNATIONALIZATION_LANGUAGE = "internationalization.language";

	public static void setGeneralPropFile(final String aGeneralPropFile) {
		generalPropFile = aGeneralPropFile;
	}

	private UserDataUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Функция загрузки пользовательских ресурсов Web-приложения, используя
	 * стандартный java IO API. Загрузка идет из каталога текущей userdata.
	 * 
	 * @param fileName
	 *            - путь к загружаемому файлу в каталоге userdata
	 * @return поток с файлом.
	 * @throws IOException
	 */
	public static InputStream loadUserDataToStream(final String fileName) throws IOException {
		FileInputStream result =
			new FileInputStream(getUserDataCatalog() + File.separator + fileName);
		return result;
	}

	public static InputStream loadGeneralToStream(final String fileName) throws IOException {
		File fileRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
		File file =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + "common.sys" + "/"
					+ fileName);
		FileInputStream result = null;
		if (file.exists()) {
			result =
				new FileInputStream(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
						+ "common.sys" + "/" + fileName);
		}
		if (!(file.exists())) {
			File[] files = fileRoot.listFiles();
			for (File f : files) {
				if (f.getName().startsWith("common.") && !("common.sys".equals(f.getName()))) {
					File fileN =
						new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
								+ f.getName() + "/" + fileName);

					if (fileN.exists()) {
						result =
							new FileInputStream(AppInfoSingleton.getAppInfo().getUserdataRoot()
									+ "/" + f.getName() + "/" + fileName);
						break;
					}

				}
			}
		}
		// FileInputStream result =
		// new FileInputStream(AppInfoSingleton.getAppInfo().getUserdataRoot() +
		// "/"
		// + UserDataUtils.GENERAL_RES_ROOT + File.separator + fileName);
		return result;
	}

	/**
	 * Функция загрузки пользовательских ресурсов Web-приложения, используя
	 * стандартный java IO API. Загрузка идет из каталога userdata с
	 * идентификатором userdataId.
	 * 
	 * @param fileName
	 *            путь к загружаемому файлу в каталоге userdata
	 * @param userdataId
	 *            идентификатор userdata
	 * @return поток с файлом.
	 * @throws IOException
	 */
	public static InputStream loadUserDataToStream(final String fileName, final String userdataId)
			throws IOException {
		FileInputStream result =
			new FileInputStream(getUserDataCatalog(userdataId) + File.separator + fileName);
		return result;
	}

	/**
	 * Получает значение обязательного параметра по его имени из текущей
	 * userdata.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @return - значение параметра.
	 */
	public static String getRequiredProp(final String propName) {
		String result = getOverridenProp(propName);
		if (result == null) {
			throw new SettingsFileRequiredPropException(getCurrentPropFile(), propName,
					SettingsFileType.APP_PROPERTIES);
		}
		return result;
	}

	/**
	 * Получает значение необязательного параметра по его имени из текущей
	 * userdata.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @return - значение параметра.
	 */
	public static String getOptionalProp(final String propName) {
		return propReadFunc(propName, null);
	}

	/**
	 * Получает значение необязательного параметра по его имени из userdata с
	 * идентификатором userdataId.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @param userdataId
	 *            идентификатор userdata, из которого будет считан параметр
	 * @return - значение параметра.
	 */
	public static String getOptionalProp(final String propName, final String userdataId) {
		return propReadFunc(propName, userdataId);
	}

	private static String propReadFunc(final String propName, final String aUserdataId) {
		try {
			String userdataId = aUserdataId;
			if (propName.trim().contains(AUTHSERVERURL_PART)) {
				userdataId = ExchangeConstants.DEFAULT_USERDATA;
			}

			String result = getProperties(userdataId).getProperty(propName);
			if (result != null) {
				result = result.trim();
				result = correctPathToSolutionResources(propName, result);
			} else if (result == null
					&& (CONNECTION_URL_PARAM.equals(propName)
							|| CONNECTION_USERNAME_PARAM.equals(propName) || CONNECTION_PASSWORD_PARAM
								.equals(propName))) {
				result = getGeneralProperties().getProperty(propName);
				if (result != null) {
					result = result.trim();
				}
			}
			return result;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	private static String
			correctPathToSolutionResources(final String propName, final String source) {
		String result = source;
		if (NAVIGATOR_ICONS_DIR_NAME.equals(propName) || IMAGES_IN_GRID_DIR.equals(propName)) {
			String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
			result = String.format(DIR_IN_SOLUTIONS, userdataId, source);
		}
		return result;
	}

	private static Properties getProperties(final String aUserdataId) throws IOException {
		String userdataId;
		if (aUserdataId == null) {
			userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		} else {
			userdataId = aUserdataId;
		}

		Properties prop = new Properties();

		InputStream is = loadUserDataToStream(getCurrentPropFile(), userdataId);
		try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
			prop.load(reader);
		}

		return prop;
	}

	public static void checkAppPropsExists(final String aUserdataId) {
		String fileName = getUserDataCatalog(aUserdataId) + File.separator + PROPFILENAME;
		File propsFile = new File(fileName);
		if (!propsFile.exists()) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.APP_PROPERTIES);
		}
	}

	/**
	 * Возвращает идентификатор текущей userdata.
	 * 
	 * @return - идентификатор текущей userdata.
	 */
	public static String getUserDataId() {
		String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();

		if (userdataId == null) {
			userdataId = ExchangeConstants.DEFAULT_USERDATA;
		}

		return userdataId;
	}

	/**
	 * Возвращает каталог с данными пользователя из текущей userdata.
	 * 
	 * @return - каталог.
	 */
	public static String getUserDataCatalog() {
		return getUserDataCatalog(null);
	}

	/**
	 * Возвращает каталог с данными пользователя из userdata с идентификатором
	 * userdataId.
	 * 
	 * @param userdataId
	 *            идентификатор userdata
	 * @return - каталог.
	 */
	private static String getUserDataCatalog(final String aUserdataId) {
		String userDataCatalog = null;
		String userdataId = aUserdataId;
		if (userdataId == null) {
			userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		}
		UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
		if (us != null) {
			userDataCatalog = us.getPath();
		} else {
			throw new NoSuchUserDataException(userdataId);
		}

		return userDataCatalog;
	}

	/**
	 * Функция по подстановке стандартных параметров приложения вместо их
	 * шаблонов.
	 * 
	 * @param source
	 *            - исходный текст.
	 */
	public static String replaceVariables(final String source) {
		String value =
			source.replace("${" + IMAGES_IN_GRID_DIR + "}", getRequiredProp(IMAGES_IN_GRID_DIR));
		value =
			value.replace(CURRENT_USERDATA_TEMPLATE, String.format("solutions/%s",
					AppInfoSingleton.getAppInfo().getCurUserDataId()));

		String data = modifyVariables(value);

		return data;
	}

	public static String getGeneralOptionalProp(final String paramName) {
		Properties prop = new Properties();
		try {
			InputStream is = new FileInputStream(getGeneralPropFile());
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				prop.load(reader);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(getGeneralPropFile(),
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}
		return getGeneralProperties().getProperty(paramName);
	}

	public static Properties getGeneralProperties() {
		Properties props = new Properties();
		try {
			InputStream is = new FileInputStream(getGeneralPropFile());
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				props.load(reader);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(getGeneralPropFile(),
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}
		return props;
	}

	public static Properties getGeneralCelestaProperties() {
		Properties generalProps = getGeneralProperties();
		Properties celestaProps = new Properties();

		String propertyKeyString;
		int prefixLength = CELESTA_PREFIX.length();
		for (Object propertyKey : generalProps.keySet()) {
			propertyKeyString = ((String) propertyKey).trim();
			if (propertyKeyString.startsWith(CELESTA_PREFIX)) {
				celestaProps.put(propertyKeyString.substring(prefixLength), generalProps
						.getProperty(propertyKeyString).trim());
			}
		}

		String scorePath = generalProps.getProperty(CELESTA_PREFIX + CELESTA_SCORE_PATH);
		String scorePathForCelestaPropertiesBasedOnCurrentUserdata =
			generateScorePathForCelestaPropertiesBasedOnCurrentUserdata();
		if (!(scorePath == null || scorePath.isEmpty())) {
			scorePath = scorePath.replace("/", File.separator);
			String[] scores = scorePath.split(File.pathSeparator);
			scorePath = scorePathForCelestaPropertiesBasedOnCurrentUserdata;
			for (String path : scores) {
				if (!(scorePath.toUpperCase().contains(path.trim().toUpperCase()))) {
					scorePath = path.trim() + File.pathSeparator + scorePath;
				}
			}
			celestaProps.put(CELESTA_SCORE_PATH, scorePath);
		} else {
			celestaProps.put(CELESTA_SCORE_PATH,
					scorePathForCelestaPropertiesBasedOnCurrentUserdata);
		}

		// ---
		String logLogins = generalProps.getProperty(CELESTA_PREFIX + CELESTA_LOG_LOGINS);
		if (!(logLogins == null || logLogins.isEmpty())) {
			celestaProps.put(CELESTA_LOG_LOGINS, logLogins);
		}

		String skipDbupdate = generalProps.getProperty(CELESTA_PREFIX + CELESTA_SKIP_DBUPDATE);
		if (!(skipDbupdate == null || skipDbupdate.isEmpty())) {
			celestaProps.put(CELESTA_SKIP_DBUPDATE, skipDbupdate);
		}

		String forceDBInitialize =
			generalProps.getProperty(CELESTA_PREFIX + CELESTA_FORCE_DBINITIALIZE);
		if (!(forceDBInitialize == null || forceDBInitialize.isEmpty())) {
			celestaProps.put(CELESTA_FORCE_DBINITIALIZE, forceDBInitialize);
		}

		String connectionURL = generalProps.getProperty(RDBMS_PREFIX + CELESTA_CONNECTION_URL);
		if (!(connectionURL == null || connectionURL.isEmpty())) {
			celestaProps.put(RDBMS_PREFIX + CELESTA_CONNECTION_URL, connectionURL);
		}

		String connectionUsername =
			generalProps.getProperty(RDBMS_PREFIX + CELESTA_CONNECTION_USERNAME);
		if (!(connectionUsername == null || connectionUsername.isEmpty())) {
			celestaProps.put(RDBMS_PREFIX + CELESTA_CONNECTION_USERNAME, connectionUsername);
		}

		String connectionPassword =
			generalProps.getProperty(RDBMS_PREFIX + CELESTA_CONNECTION_PASSWORD);
		if (!(connectionPassword == null || connectionPassword.isEmpty())) {
			celestaProps.put(RDBMS_PREFIX + CELESTA_CONNECTION_PASSWORD, connectionPassword);
		}
		// -----

		// String dbClassname = generalProps.getProperty(CELESTA_PREFIX +
		// CELESTA_DATABASE_CLASSNAME);
		// if (!(dbClassname == null || dbClassname.isEmpty())) {
		// celestaProps.put(CELESTA_DATABASE_CLASSNAME, dbClassname);
		// }

		// String dbConnection =
		// generalProps.getProperty(CELESTA_PREFIX +
		// CELESTA_DATABASE_CONNECTION);
		// if (!(dbConnection == null || dbConnection.isEmpty())) {
		// celestaProps.put(CELESTA_DATABASE_CONNECTION, dbConnection);
		// }
		String pyLibPath = generalProps.getProperty(CELESTA_PREFIX + CELESTA_PYLIB_PATH);
		if (!(pyLibPath == null || pyLibPath.isEmpty())) {
			pyLibPath = pyLibPath.replace("/", File.separator);
		}
		String pyLibShowcasePath = JythonIterpretatorFactory.getInstance().getLibJythonDir();
		File f = new File(pyLibShowcasePath);
		pyLibShowcasePath = f.getAbsolutePath();
		String pyLibShowcasePathSitePacks = pyLibShowcasePath + File.separator + "site-packages";

		if (pyLibPath == null || pyLibPath.isEmpty()) {
			celestaProps.put(CELESTA_PYLIB_PATH, pyLibShowcasePath + File.pathSeparator
					+ pyLibShowcasePathSitePacks);
		} else {
			if (!(pyLibPath.toUpperCase().contains(pyLibShowcasePath.toUpperCase()))) {
				pyLibPath = pyLibPath + File.pathSeparator + pyLibShowcasePath;
			}
			if (!(pyLibPath.toUpperCase().contains(pyLibShowcasePathSitePacks.toUpperCase()))) {
				pyLibPath = pyLibPath + File.pathSeparator + pyLibShowcasePathSitePacks;
			}
			celestaProps.put(CELESTA_PYLIB_PATH, pyLibPath);
		}

		String javaLibPath = generalProps.getProperty(CELESTA_PREFIX + CELESTA_JAVALIB_PATH);
		if (!(javaLibPath == null || javaLibPath.isEmpty())) {
			javaLibPath = javaLibPath.replace("/", File.separator);
			celestaProps.put(CELESTA_JAVALIB_PATH, javaLibPath);
		}

		String allowIndexedNulls =
			generalProps.getProperty(CELESTA_PREFIX + CELESTA_ALLOW_INDEXED_NULLS);
		if (!(allowIndexedNulls == null || allowIndexedNulls.isEmpty())) {
			celestaProps.put(CELESTA_ALLOW_INDEXED_NULLS, allowIndexedNulls);
		}

		return celestaProps;
	}

	/**
	 * Возвращает параметр score.path для челесты, который в этой процедуре
	 * заполняется автоматически на основе userdata (из всех userdata
	 * извлекаются пути, ведущие к подпапкам score (фиксированное имя папки), и
	 * перечисляются через System.pathSeparator.
	 * 
	 * @return - String
	 */
	private static String generateScorePathForCelestaPropertiesBasedOnCurrentUserdata() {

		String result = "";
		Collection<UserData> uds = AppInfoSingleton.getAppInfo().getUserdatas().values();

		for (UserData ud : uds) {

			File f = new File(ud.getPath());

			result = result + f.getAbsolutePath() + File.separator + "score" + File.pathSeparator;

		}

		File fileRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
		File[] files = fileRoot.listFiles();
		for (File f : files) {
			if (f.getName().startsWith("common.")) {
				File fileN =
					new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + f.getName());
				if (fileN.exists()) {
					if ((new File(fileN.getAbsolutePath() + File.separator + "score")).exists())
						result =
							result + fileN.getAbsolutePath() + File.separator + "score"
									+ File.pathSeparator;
				}
			}
		}

		if (!result.isEmpty()) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	public static Properties getGeneralOauth2Properties() {
		if (generalOauth2Properties == null) {
			generalOauth2Properties = new Properties();
			Properties generalProps = getGeneralProperties();

			String tempParam = generalProps.getProperty(OAUTH_PREFIX + OAUTH_AUTHORIZE_URL);
			if (tempParam != null) {
				generalOauth2Properties.put(OAUTH_AUTHORIZE_URL, tempParam);
			}

			tempParam = generalProps.getProperty(OAUTH_PREFIX + OAUTH_TOLEN_URL);
			if (tempParam != null) {
				generalOauth2Properties.put(OAUTH_TOLEN_URL, tempParam);
			}

			tempParam = generalProps.getProperty(OAUTH_PREFIX + OAUTH_CLIENT_ID);
			if (tempParam != null) {
				generalOauth2Properties.put(OAUTH_CLIENT_ID, tempParam);
			}

			tempParam = generalProps.getProperty(OAUTH_PREFIX + OAUTH_CLIENT_SECRET);
			if (tempParam != null) {
				generalOauth2Properties.put(OAUTH_CLIENT_SECRET, tempParam);
			}
		}
		return generalOauth2Properties.isEmpty() ? null : generalOauth2Properties;
	}

	public static Properties getGeneralSpnegoProperties() {
		if (generalSpnegoProperties == null) {
			generalSpnegoProperties = new Properties();
			Properties generalProps = getGeneralProperties();

			String tempParam = generalProps.getProperty(SPNEGO_PREAUTH_USERNAME);
			if (tempParam != null) {
				generalSpnegoProperties.put(SPNEGO_PREAUTH_USERNAME, tempParam);
			}

			tempParam = generalProps.getProperty(SPNEGO_PREAUTH_PASSWORD);
			if (tempParam != null) {
				generalSpnegoProperties.put(SPNEGO_PREAUTH_PASSWORD, tempParam);
			}

			tempParam = generalProps.getProperty(SPNEGO_ALLOW_BASIC);
			if (tempParam != null) {
				generalSpnegoProperties.put(SPNEGO_ALLOW_BASIC, tempParam);
			}

			tempParam = generalProps.getProperty(SPNEGO_LOGGER_LEVEL);
			if (tempParam != null) {
				generalSpnegoProperties.put(SPNEGO_LOGGER_LEVEL, tempParam);
			}

		}
		return generalSpnegoProperties.isEmpty() ? null : generalSpnegoProperties;
	}

	private static String getGeneralPropFile() {
		if (generalPropFile == null) {
			// return AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
			// + UserDataUtils.GENERAL_RES_ROOT + "/" + GENERALPROPFILENAME;
			return AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + GENERALPROPFILENAME;
		}
		return generalPropFile;
	}

	public static String getGeneralRequiredProp(final String propName) {
		String value = getGeneralOptionalProp(propName);
		if (value == null) {
			throw new SettingsFileRequiredPropException(GENERALPROPFILENAME, propName,
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}
		return value;
	}

	public static String getGeoMapKey(final String engine, final String host) {
		String realHost = host;
		final String localhost = "localhost";
		if ("127.0.0.1".equals(host)) {
			realHost = localhost;
		}
		String userdataRoot = AppInfoSingleton.getAppInfo().getUserdataRoot();
		String path = String.format("%s/geomap.key.%s.properties", userdataRoot, realHost);
		Properties props = new Properties();

		FileInputStream is;
		try {
			is = new FileInputStream(path);
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				props.load(reader);
			} catch (IOException e) {
				throw new SettingsFileOpenException(e, path, SettingsFileType.GEOMAP_KEYS);
			}
		} catch (FileNotFoundException e) {
			if (localhost.equals(realHost)) {
				return "";
			} else {
				return getGeoMapKey(engine, localhost);
			}
		}
		return props.getProperty(engine, "");

	}

	public static void checkUserdatas() {
		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			checkAppPropsExists(userdataId);
		}

		try {
			Properties props = getGeneralProperties();
			checkAppPropsForWrongSymbols("", props);

			for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
				props = getProperties(userdataId);
				checkAppPropsForWrongSymbols(userdataId, props);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}

		// checkUserdataFilesNamesForWrongSymbols(AppInfoSingleton.getAppInfo().getUserdataRoot()
		// + "/" + GENERAL_RES_ROOT);

		File fileRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
		File[] files = fileRoot.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.getName().startsWith("common.")) {
					checkUserdataFilesNamesForWrongSymbols(AppInfoSingleton.getAppInfo()
							.getUserdataRoot() + "/" + f.getName());
				}
			}
		}

		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			checkUserdataFilesNamesForWrongSymbols(AppInfoSingleton.getAppInfo()
					.getUserData(userdataId).getPath());
		}

	}

	private static void checkUserdataFilesNamesForWrongSymbols(final String userDataCatalog) {
		BatchFileProcessor fprocessor =
			new BatchFileProcessor(userDataCatalog, new RegexFilenameFilter("^[.].*", false));
		try {
			fprocessor.process(new CheckFileNameAction());
		} catch (IOException e) {
			throw new CheckFilesNameException(e.getMessage());
		}
	}

	private static void checkAppPropsForWrongSymbols(final String userdataId,
			final Properties props) {
		String prop;
		String value;
		for (Object opr : props.keySet()) {
			prop = (String) opr;
			if ("﻿#".equalsIgnoreCase(prop.trim())) {
				continue;
			}
			if (INDEX_TITLE.equalsIgnoreCase(prop.trim())) {
				continue;
			}
			if (INDEX_WELCOME_TAB_CAPTION.equalsIgnoreCase(prop.trim())) {
				continue;
			}
			if (LOGIN_TITLE.equalsIgnoreCase(prop.trim())) {
				continue;
			}
			if ((CELESTA_PREFIX + CELESTA_PYLIB_PATH).equals(prop.trim())) {
				continue;
			}
			if ((CELESTA_PREFIX + CELESTA_SCORE_PATH).equals(prop.trim())) {
				continue;
			}

			value = props.getProperty(prop);
			if (checkValueForSpace(value.trim())) {
				if ("".equalsIgnoreCase(userdataId)) {
					throw new GeneralAppPropsValueContainsSpaceException(prop, value);
				} else {
					throw new AppPropsValueContainsSpaceException(userdataId, prop, value);
				}
			}
			if (checkValueForCyrillicSymbols(value.trim())) {
				if ("".equalsIgnoreCase(userdataId)) {
					throw new GeneralAppPropsValueContainsCyrillicSymbolException(prop, value);
				} else {
					throw new AppPropsValueContainsCyrillicSymbolException(userdataId, prop, value);
				}
			}
		}
	}

	public static boolean checkValueForSpace(final String value) {
		Pattern pSpace = Pattern.compile("\\s", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		return pSpace.matcher(value).find();
	}

	public static boolean checkValueForCyrillicSymbols(final String value) {
		// Pattern pCyr =
		// Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE +
		// Pattern.UNICODE_CASE);

		Pattern pCyr = Pattern.compile("[а-яё]", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		return pCyr.matcher(value).find();
	}

	public static Boolean isTextFile(final Object obj) {
		DataFile<?> file = (DataFile<?>) obj;
		String fromAppProps = getGeneralOptionalProp(WEB_CONSOLE_ADD_TEXT_FILES_PARAM);
		if (fromAppProps != null) {
			String[] userTextExtensions = fromAppProps.split(":");
			for (String ext : userTextExtensions) {
				if (file.getName().endsWith(ext)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getCurrentPropFile() {
		String overrided = MDC.get(CommandContext.PROP_FILE_TAG);
		if ((overrided != null) && (!overrided.isEmpty())) {
			return overrided;
		}
		return PROPFILENAME;
	}

	private static String getOverridenProp(final String aPropName) {
		String overrided = MDC.get(aPropName);
		if ((overrided != null) && (!overrided.isEmpty())) {
			return overrided;
		}
		return propReadFunc(aPropName, null);
	}

	public static Boolean getInterfaceOnlyDatapanelForCurrentUserdata() {
		String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		Boolean b = false;
		try {
			String result = getProperties(userdataId).getProperty(INTERFACE_ONLY_DATAPANEL);
			if (result != null) {
				result = result.trim();
				b = Boolean.parseBoolean(result);
				return b;
			}
			return b;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	public static Boolean getInterfaceOnlyDatapanelByUserdataId(final String aUserdataId) {
		String userdataId = aUserdataId;
		Boolean b = false;
		try {
			String result = getProperties(userdataId).getProperty(INTERFACE_ONLY_DATAPANEL);
			if (result != null) {
				result = result.trim();
				b = Boolean.parseBoolean(result);
				return b;
			}
			return b;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	public static List<String> getImportStaticExternalJsLibrariesForCurrentUserdata() {
		String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		List<String> list = new ArrayList<String>();
		try {
			String result =
				getProperties(userdataId).getProperty(IMPORT_STATIC_EXTERNAL_JS_LIBRARIES);
			if (result != null) {
				result = result.trim();
				String[] str = result.split(",");
				for (int i = 0; i < str.length; i++) {
					str[i] = str[i].trim();
					if (!("".equals(str[i]))) {
						list.add(str[i]);
					}
				}
			}
			return list;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	public static List<String> getImportStaticExternalJsLibrariesByUserdataId(
			final String aUserdataId) {
		String userdataId = aUserdataId;
		List<String> list = new ArrayList<String>();
		try {
			String result =
				getProperties(userdataId).getProperty(IMPORT_STATIC_EXTERNAL_JS_LIBRARIES);
			if (result != null) {
				result = result.trim();
				String[] str = result.split(",");
				for (int i = 0; i < str.length; i++) {
					str[i] = str[i].trim();
					if (!("".equals(str[i]))) {
						list.add(str[i]);
					}
				}
			}
			return list;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	public static List<String> getImportStaticExternalCssForCurrentUserdata() {
		String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		List<String> list = new ArrayList<String>();
		try {
			String result = getProperties(userdataId).getProperty(IMPORT_STATIC_EXTERNAL_CSS);
			if (result != null) {
				result = result.trim();
				String[] str = result.split(",");
				for (int i = 0; i < str.length; i++) {
					str[i] = str[i].trim();
					if (!("".equals(str[i]))) {
						list.add(str[i]);
					}
				}
			}
			return list;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	public static List<String> getImportStaticExternalCssByUserdataId(final String aUserdataId) {
		String userdataId = aUserdataId;
		List<String> list = new ArrayList<String>();
		try {
			String result = getProperties(userdataId).getProperty(IMPORT_STATIC_EXTERNAL_CSS);
			if (result != null) {
				result = result.trim();
				String[] str = result.split(",");
				for (int i = 0; i < str.length; i++) {
					str[i] = str[i].trim();
					if (!("".equals(str[i]))) {
						list.add(str[i]);
					}
				}
			}
			return list;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	/**
	 * Метод, возвращающий локаль из текущей юзердаты.
	 * 
	 * @return локаль
	 */
	public static String getLocaleForCurrentUserdata() {
		String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		try {
			String result = getProperties(userdataId).getProperty(INTERNATIONALIZATION_LANGUAGE);
			if (result != null) {
				result = result.trim();
				return result;
			} else {
				return "";
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	/**
	 * Метод, возвращающий локаль из заданной юзердаты.
	 * 
	 * @param - заданная юзердаты
	 * @return локаль
	 */
	public static String getLocaleForCurrentUserdata(String userdataId) {
		try {
			String result = getProperties(userdataId).getProperty(INTERNATIONALIZATION_LANGUAGE);
			if (result != null) {
				result = result.trim();
				return result;
			} else {
				return "";
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	public static String getAppVersion() {
		Properties prop = new Properties();
		try {
			InputStream is = FileUtils.loadClassPathResToStream("version.properties");
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(is, TextUtils.DEF_ENCODING);
				prop.load(reader);
			} finally {
				if (reader != null)
					reader.close();
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException("version.properties", SettingsFileType.VERSION);
		} catch (NullPointerException ex) {
			throw new SettingsFileOpenException("version.properties", SettingsFileType.VERSION);
		}
		String major = prop.getProperty("version");

		String build;
		try {
			InputStream is = FileUtils.loadClassPathResToStream("build");
			BufferedReader buf = null;
			try {
				buf = new BufferedReader(new InputStreamReader(is));
				build = buf.readLine();
				Pattern pattern2 = Pattern.compile("(\\d+|development)");
				Matcher matcher2 = pattern2.matcher(build);
				matcher2.find();
				build = matcher2.group();
			} finally {
				if (buf != null)
					buf.close();
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException("build", SettingsFileType.VERSION);
		} catch (NullPointerException ex) {
			throw new SettingsFileOpenException("build", SettingsFileType.VERSION);
		}
		String appVersion = String.format("%s.%s", major, build);
		return appVersion;
	}

	/**
	 * Метод, модифицирующий системную переменную окружения CLASSPATH, в
	 * зависимости от настройки localization.path файла generalapp.properties.
	 * Данный метод модифицирует переменную CLASSPATH только в памяти в пределах
	 * рабочей сессии. В пределах же системы переменная остаётся в неизменном
	 * состоянии.
	 */

	public static void modifyClasspathEnvVar() {
		if (getGeneralOptionalProp("localization.path") != null) {
			String localizationPath = getGeneralOptionalProp("localization.path").trim();
			File localizationPathAsFile = new File(localizationPath);

			if (localizationPathAsFile.exists()) {
				ProcessBuilder pb = new ProcessBuilder();
				Map<String, String> env = pb.environment();
				String classpath = env.get("CLASSPATH");
				if (classpath == null) {
					env.put("CLASSPATH", localizationPath);
				} else if (!classpath.toUpperCase().contains(localizationPath.toUpperCase())) {
					String path = env.get("CLASSPATH") + ";" + localizationPath;
					env.put("CLASSPATH", path);
				}
			}
		}

	}

	/**
	 * Метод поиска (в указанной юзердате в подпапке resources) файла с
	 * расширенем .po, служащего для локализации клиентской части Showcase.
	 * 
	 * @param anUserdataId
	 *            - юзердата, в которой будет просиходить поиск локализационного
	 *            файла
	 * @return имя локализационного файла с расширением
	 */
	public static String getPlatformPoFile(String anUserdataId) {
		// File dir = new File(getUserDataCatalog(anUserdataId) + "/" +
		// "resources");
		File dir =
			new File(AppInfoSingleton.getAppInfo().getSolutionsDirRoot() + File.separator
					+ anUserdataId + File.separator + "resources");
		// AppInfoSingleton.getAppInfo().setCurUserDataId(anUserdataId);

		String poFileName = "";

		String lang = UserDataUtils.getLocaleForCurrentUserdata(anUserdataId);

		String platform =
			(lang == null || lang.equals("") || lang.equals("en")) ? "platform" : "platform" + "_"
					+ lang;

		if (dir.exists()) {
			for (String file : dir.list()) {
				if (file.equals(platform + ".po")) {
					poFileName = file;
					break;
				}
			}
		}

		return poFileName;
	}

	/**
	 * Метод поиска (в указанной юзердате в подпапке resources) файла с
	 * расширенем .po, служащего для локализации клиентской части Showcase. Язык
	 * поиска задаётся как парарметр.
	 * 
	 * @param anUserdataId
	 *            - юзердата, в которой будет просиходить поиск локализационного
	 *            файла
	 * @param lang
	 *            - язык локали
	 * @return имя локализационного файла с расширением
	 */
	public static String getPlatformPoFile(String anUserdataId, String lang) {
		// File dir = new File(getUserDataCatalog(anUserdataId) + "/" +
		// "resources");
		File dir =
			new File(AppInfoSingleton.getAppInfo().getSolutionsDirRoot() + File.separator
					+ anUserdataId + File.separator + "resources");

		// AppInfoSingleton.getAppInfo().setCurUserDataId(anUserdataId);

		String poFileName = "";
		String platform =
			(lang == null || lang.equals("") || lang.equals("en")) ? "platform" : "platform" + "_"
					+ lang;

		if (dir.exists()) {
			for (String file : dir.list()) {
				if (file.equals(platform + ".po")) {
					poFileName = file;
					break;
				}
			}
		}

		return poFileName;
	}

	/**
	 * Метод поиска (в папке common.sys в подпапке resources) дефолтного файла с
	 * расширенем .po, служащего для локализации клиентской части Showcase.
	 * 
	 * @return имя локализационного файла с расширением
	 */
	public static String getDefaultPlatformPoFile() {
		// File dir =
		// new File(AppInfoSingleton.getAppInfo().getUserdataRoot() +
		// "/common.sys/"
		// + "resources");

		File dir =
			new File(AppInfoSingleton.getAppInfo().getSolutionsDirRoot() + File.separator
					+ "default" + File.separator + "resources");

		String poFileName = "";

		String platform = "platform";

		if (dir.exists()) {
			for (String file : dir.list()) {
				if (file.equals(platform + ".po")) {
					poFileName = file;
					break;
				}
			}
		}

		File dir2 = new File(AppInfoSingleton.getAppInfo().getResourcesDirRoot());

		if ("".equals(poFileName)) {
			for (String file : dir2.list()) {
				if (file.equals(platform + ".po")) {
					poFileName = file;
					break;
				}
			}
		}

		return poFileName;
	}

	/**
	 * Метод, объединяющий в себе все методы поиска файла с расширенем .po,
	 * служащего для локализации клиентской части Showcase.
	 * 
	 * @return имя локализационного файла с расширением
	 */
	public static String getFinalPlatformPoFile(String userdataid) {
		String domainFile = UserDataUtils.getPlatformPoFile(userdataid);

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			// String sesid = ((UserAndSessionDetails)
			// auth.getDetails()).getSessionId();
			String sesid = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();
			String lang = null;
			try {
				lang = AppInfoSingleton.getAppInfo().getLocalizationCache().get(sesid);
			} catch (Exception e) {
				lang = null;
			}
			if (lang != null && !"".equals(lang))
				domainFile = UserDataUtils.getPlatformPoFile(userdataid, lang);

		}

		if ("".equals(domainFile))
			domainFile = UserDataUtils.getDefaultPlatformPoFile();

		return domainFile;
	}

	/**
	 * Возвращает директорию resources юзердаты в виде объкта File.
	 * 
	 * @param anUserdataId
	 *            - текущая юзердата
	 */
	public static File getResourceDir(String anUserdataId) {
		// File dir = new File(getUserDataCatalog(anUserdataId) + "/resources" +
		// "/loc8n");
		File dir =
			new File(AppInfoSingleton.getAppInfo().getSolutionsDirRoot() + File.separator
					+ anUserdataId + File.separator + "resources" + File.separator + "loc8n");

		return dir;
	}

	/**
	 * Метод, используемый для задания языка локали через Celesta-процедуру,
	 * которая вызывается во время залогинивания в Showcase.
	 * 
	 * @param sesid
	 *            - id сессии
	 * @param loc
	 *            - язык локали
	 */
	public static void setLocaleFromCelestaLoggingProc(String sesid, String loc) {
		AppInfoSingleton.getAppInfo().getLocalizationCache().put(sesid, loc);
		ResourceBundle bundle = CourseLocalization.getLocalizedResourseBundle(loc);
		if (bundle != null) {
			AppInfoSingleton.getAppInfo().getLocalizedBundleCache().put(sesid, bundle);
		} else {
			AppInfoSingleton.getAppInfo().getLocalizedBundleCache()
					.put(sesid, CourseLocalization.getLocalizedResourseBundle());
		}
	}

	public static ResourceBundle getResourceBundleForGettext() {
		ResourceBundle bundle = null;
		String sesid = "";
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			sesid = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();
			if (sesid != null
					&& AppInfoSingleton.getAppInfo().getLocalizedBundleCache().get(sesid) == null) {
				bundle = CourseLocalization.getLocalizedResourseBundle();
				if (bundle != null)
					AppInfoSingleton.getAppInfo().getLocalizedBundleCache().put(sesid, bundle);
			}
		}
		if (sesid != null)
			bundle =
				(ResourceBundle) AppInfoSingleton.getAppInfo().getLocalizedBundleCache()
						.get(sesid);

		if (bundle == null)
			bundle = CourseLocalization.getLocalizedResourseBundle();

		return bundle;
	}

	/**
	 * Метод, служущаий для перевода строк с помощью Gettext в серверной части
	 * Showcase.
	 * 
	 * @param value
	 *            - входящая строка
	 * @return переведённая строка
	 */
	public static String modifyVariables(final String value) {
		String data = value;
		if (data == null)
			return null;

		ResourceBundle bundle = null;
		String sesid = "";
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			sesid = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();
			if (sesid == null) {
				// sesid = AppInfoSingleton.getAppInfo().getSesid();
				// String[] arr =
				// AppInfoSingleton.getAppInfo().getRemoteAddrSessionMap().values()
				// .toArray(new String[0]);
				// if (arr.length > 0)
				// sesid = arr[arr.length - 1];
				String remoteAddr =
					((WebAuthenticationDetails) auth.getDetails()).getRemoteAddress();
				if (remoteAddr != null)
					sesid =
						AppInfoSingleton.getAppInfo().getRemoteAddrSessionMap().get(remoteAddr);
			}
			if (AppInfoSingleton.getAppInfo().getLocalizedBundleCache().get(sesid) == null) {
				bundle = CourseLocalization.getLocalizedResourseBundle();
				if (bundle != null)
					AppInfoSingleton.getAppInfo().getLocalizedBundleCache().put(sesid, bundle);
			}
		}
		bundle =
			(ResourceBundle) AppInfoSingleton.getAppInfo().getLocalizedBundleCache().get(sesid);

		if (bundle == null)
			bundle = CourseLocalization.getLocalizedResourseBundle();

		if (bundle != null) {

			if (data.contains("$localize(_(\""))
				while (data.contains("$localize(_(\"")) {
					int index11 = -1;
					if (data.contains("$localize(_(\" $localize(_(")) {
						index11 = data.indexOf("$localize(_(\" $localize(_(");
					}
					int index = data.indexOf("\"))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(\" $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(\" $localize(_(\"" + substr11 + "\"))\"))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\"" + substr1 + "\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index11 = -1;
					if (data.contains("$localize(_(' $localize(_(")) {
						index11 = data.indexOf("$localize(_(' $localize(_(");
					}
					int index = data.indexOf("'))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(' $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(' $localize(_('" + substr11 + "'))'))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&#34;"))
				while (data.contains("$localize(_(&#34;")) {
					int index1 = data.indexOf("$localize(_(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;quot;"))
				while (data.contains("$localize(_(&amp;quot;")) {
					int index1 = data.indexOf("$localize(_(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;apos;"))
				while (data.contains("$localize(_(&amp;apos;")) {
					int index1 = data.indexOf("$localize(_(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;apos;"))
				while (data.contains("$localize(gettext(&amp;apos;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(\\\""))
				while (data.contains("$localize(_(\\\"")) {
					int index1 = data.indexOf("$localize(_(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index1 = data.indexOf("$localize(_('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext('"))
				while (data.contains("$localize(gettext('")) {
					int index1 = data.indexOf("$localize(gettext('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(gettext('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\""))
				while (data.contains("$localize(gettext(\"")) {
					int index22 = -1;
					if (data.contains("$localize(gettext(\" $localize(gettext(")) {
						index22 = data.indexOf("$localize(gettext(\" $localize(gettext(");
					}
					int index = data.indexOf("\"))", index22);

					String substr22 = "";
					if (index22 != -1 && index != -1)
						substr22 =
							data.substring(
									index22 + "$localize(gettext(\" $localize(gettext(".length()
											+ 1, index);

					if (!"".equals(substr22))
						data =
							data.replace("$localize(gettext(\" $localize(gettext(\"" + substr22
									+ "\"))\"))", CourseLocalization.gettext(bundle, substr22));

					int index2 = data.indexOf("$localize(gettext(");
					index = data.indexOf("\"))", index2);

					String substr2 = "";

					if (index2 != -1 && index != -1)
						substr2 =
							data.substring(index2 + "$localize(gettext(".length() + 1, index);

					if (!"".equals(substr2))
						data =
							data.replace("$localize(gettext(\"" + substr2 + "\"))",
									CourseLocalization.gettext(bundle, substr2));
				}

			if (data.contains("$localize(gettext(&#34;"))
				while (data.contains("$localize(gettext(&#34;")) {
					int index1 = data.indexOf("$localize(gettext(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;quot;"))
				while (data.contains("$localize(gettext(&amp;quot;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\\\""))
				while (data.contains("$localize(gettext(\\\"")) {
					int index1 = data.indexOf("$localize(gettext(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(ngettext(\""))
				while (data.contains("$localize(ngettext(\"")) {
					int index1 = data.indexOf("$localize(ngettext(\"");
					int index2 = data.indexOf("\",", index1 + 1);
					int index3 = data.indexOf("\"", index2 + 1);
					int index4 = data.indexOf("\",", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 =
							data.substring(index1 + "$localize(ngettext(\"".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

			if (data.contains("$localize(ngettext('"))
				while (data.contains("$localize(ngettext('")) {
					int index1 = data.indexOf("$localize(ngettext('");
					int index2 = data.indexOf("',", index1 + 1);
					int index3 = data.indexOf("'", index2 + 1);
					int index4 = data.indexOf("',", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 = data.substring(index1 + "$localize(ngettext('".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

		}

		return data;
	}

	public static String modifyVariables(final String value, ResourceBundle bundle) {
		String data = value;
		if (data == null)
			return null;

		if (bundle != null) {

			if (data.contains("$localize(_(\""))
				while (data.contains("$localize(_(\"")) {
					int index11 = -1;
					if (data.contains("$localize(_(\" $localize(_(")) {
						index11 = data.indexOf("$localize(_(\" $localize(_(");
					}
					int index = data.indexOf("\"))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(\" $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(\" $localize(_(\"" + substr11 + "\"))\"))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\"" + substr1 + "\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index11 = -1;
					if (data.contains("$localize(_(' $localize(_(")) {
						index11 = data.indexOf("$localize(_(' $localize(_(");
					}
					int index = data.indexOf("'))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(' $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(' $localize(_('" + substr11 + "'))'))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&#34;"))
				while (data.contains("$localize(_(&#34;")) {
					int index1 = data.indexOf("$localize(_(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;quot;"))
				while (data.contains("$localize(_(&amp;quot;")) {
					int index1 = data.indexOf("$localize(_(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;apos;"))
				while (data.contains("$localize(_(&amp;apos;")) {
					int index1 = data.indexOf("$localize(_(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;apos;"))
				while (data.contains("$localize(gettext(&amp;apos;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(\\\""))
				while (data.contains("$localize(_(\\\"")) {
					int index1 = data.indexOf("$localize(_(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index1 = data.indexOf("$localize(_('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext('"))
				while (data.contains("$localize(gettext('")) {
					int index1 = data.indexOf("$localize(gettext('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(gettext('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\""))
				while (data.contains("$localize(gettext(\"")) {
					int index22 = -1;
					if (data.contains("$localize(gettext(\" $localize(gettext(")) {
						index22 = data.indexOf("$localize(gettext(\" $localize(gettext(");
					}
					int index = data.indexOf("\"))", index22);

					String substr22 = "";
					if (index22 != -1 && index != -1)
						substr22 =
							data.substring(
									index22 + "$localize(gettext(\" $localize(gettext(".length()
											+ 1, index);

					if (!"".equals(substr22))
						data =
							data.replace("$localize(gettext(\" $localize(gettext(\"" + substr22
									+ "\"))\"))", CourseLocalization.gettext(bundle, substr22));

					int index2 = data.indexOf("$localize(gettext(");
					index = data.indexOf("\"))", index2);

					String substr2 = "";

					if (index2 != -1 && index != -1)
						substr2 =
							data.substring(index2 + "$localize(gettext(".length() + 1, index);

					if (!"".equals(substr2))
						data =
							data.replace("$localize(gettext(\"" + substr2 + "\"))",
									CourseLocalization.gettext(bundle, substr2));
				}

			if (data.contains("$localize(gettext(&#34;"))
				while (data.contains("$localize(gettext(&#34;")) {
					int index1 = data.indexOf("$localize(gettext(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;quot;"))
				while (data.contains("$localize(gettext(&amp;quot;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\\\""))
				while (data.contains("$localize(gettext(\\\"")) {
					int index1 = data.indexOf("$localize(gettext(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(ngettext(\""))
				while (data.contains("$localize(ngettext(\"")) {
					int index1 = data.indexOf("$localize(ngettext(\"");
					int index2 = data.indexOf("\",", index1 + 1);
					int index3 = data.indexOf("\"", index2 + 1);
					int index4 = data.indexOf("\",", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 =
							data.substring(index1 + "$localize(ngettext(\"".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

			if (data.contains("$localize(ngettext('"))
				while (data.contains("$localize(ngettext('")) {
					int index1 = data.indexOf("$localize(ngettext('");
					int index2 = data.indexOf("',", index1 + 1);
					int index3 = data.indexOf("'", index2 + 1);
					int index4 = data.indexOf("',", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 = data.substring(index1 + "$localize(ngettext('".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

		}

		return data;
	}

	/**
	 * Метод, служущаий для перевода строк с помощью Gettext для вэб-сервисов.
	 * 
	 * @param value
	 *            - входящая строка
	 * @return переведённая строка
	 */
	public static String modifyVariablesForWS(final String value) {
		String data = value;
		if (data == null)
			return null;

		ResourceBundle bundle = CourseLocalization.getLocalizedResourseBundle();

		if (bundle != null) {

			if (data.contains("$localize(_(\""))
				while (data.contains("$localize(_(\"")) {
					int index11 = -1;
					if (data.contains("$localize(_(\" $localize(_(")) {
						index11 = data.indexOf("$localize(_(\" $localize(_(");
					}
					int index = data.indexOf("\"))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(\" $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(\" $localize(_(\"" + substr11 + "\"))\"))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\"" + substr1 + "\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index11 = -1;
					if (data.contains("$localize(_(' $localize(_(")) {
						index11 = data.indexOf("$localize(_(' $localize(_(");
					}
					int index = data.indexOf("'))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(' $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(' $localize(_('" + substr11 + "'))'))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&#34;"))
				while (data.contains("$localize(_(&#34;")) {
					int index1 = data.indexOf("$localize(_(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;quot;"))
				while (data.contains("$localize(_(&amp;quot;")) {
					int index1 = data.indexOf("$localize(_(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;apos;"))
				while (data.contains("$localize(_(&amp;apos;")) {
					int index1 = data.indexOf("$localize(_(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;apos;"))
				while (data.contains("$localize(gettext(&amp;apos;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(\\\""))
				while (data.contains("$localize(_(\\\"")) {
					int index1 = data.indexOf("$localize(_(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index1 = data.indexOf("$localize(_('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext('"))
				while (data.contains("$localize(gettext('")) {
					int index1 = data.indexOf("$localize(gettext('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(gettext('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\""))
				while (data.contains("$localize(gettext(\"")) {
					int index22 = -1;
					if (data.contains("$localize(gettext(\" $localize(gettext(")) {
						index22 = data.indexOf("$localize(gettext(\" $localize(gettext(");
					}
					int index = data.indexOf("\"))", index22);

					String substr22 = "";
					if (index22 != -1 && index != -1)
						substr22 =
							data.substring(
									index22 + "$localize(gettext(\" $localize(gettext(".length()
											+ 1, index);

					if (!"".equals(substr22))
						data =
							data.replace("$localize(gettext(\" $localize(gettext(\"" + substr22
									+ "\"))\"))", CourseLocalization.gettext(bundle, substr22));

					int index2 = data.indexOf("$localize(gettext(");
					index = data.indexOf("\"))", index2);

					String substr2 = "";

					if (index2 != -1 && index != -1)
						substr2 =
							data.substring(index2 + "$localize(gettext(".length() + 1, index);

					if (!"".equals(substr2))
						data =
							data.replace("$localize(gettext(\"" + substr2 + "\"))",
									CourseLocalization.gettext(bundle, substr2));
				}

			if (data.contains("$localize(gettext(&#34;"))
				while (data.contains("$localize(gettext(&#34;")) {
					int index1 = data.indexOf("$localize(gettext(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;quot;"))
				while (data.contains("$localize(gettext(&amp;quot;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\\\""))
				while (data.contains("$localize(gettext(\\\"")) {
					int index1 = data.indexOf("$localize(gettext(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(ngettext(\""))
				while (data.contains("$localize(ngettext(\"")) {
					int index1 = data.indexOf("$localize(ngettext(\"");
					int index2 = data.indexOf("\",", index1 + 1);
					int index3 = data.indexOf("\"", index2 + 1);
					int index4 = data.indexOf("\",", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 =
							data.substring(index1 + "$localize(ngettext(\"".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

			if (data.contains("$localize(ngettext('"))
				while (data.contains("$localize(ngettext('")) {
					int index1 = data.indexOf("$localize(ngettext('");
					int index2 = data.indexOf("',", index1 + 1);
					int index3 = data.indexOf("'", index2 + 1);
					int index4 = data.indexOf("',", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 = data.substring(index1 + "$localize(ngettext('".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

		}

		return data;
	}

	/**
	 * Метод, служущаий для перевода строк с помощью Gettext c учётом
	 * передаваемого языка для вэб-сервисов.
	 * 
	 * @param lang
	 *            - передаваемый язык
	 * @param value
	 *            - входящая строка
	 * @return переведённая строка
	 */
	public static String modifyVariablesForWS(final String value, final String lang) {
		String data = value;
		if (data == null)
			return null;

		ResourceBundle bundle = CourseLocalization.getLocalizedResourseBundle(lang);

		if (bundle != null) {

			if (data.contains("$localize(_(\""))
				while (data.contains("$localize(_(\"")) {
					int index11 = -1;
					if (data.contains("$localize(_(\" $localize(_(")) {
						index11 = data.indexOf("$localize(_(\" $localize(_(");
					}
					int index = data.indexOf("\"))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(\" $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(\" $localize(_(\"" + substr11 + "\"))\"))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\"" + substr1 + "\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index11 = -1;
					if (data.contains("$localize(_(' $localize(_(")) {
						index11 = data.indexOf("$localize(_(' $localize(_(");
					}
					int index = data.indexOf("'))", index11);

					String substr11 = "";
					if (index11 != -1 && index != -1)
						substr11 =
							data.substring(index11 + "$localize(_(' $localize(_(".length() + 1,
									index);

					if (!"".equals(substr11))
						data =
							data.replace("$localize(_(' $localize(_('" + substr11 + "'))'))",
									CourseLocalization.gettext(bundle, substr11));

					int index1 = data.indexOf("$localize(_(");
					index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(".length() + 1, index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&#34;"))
				while (data.contains("$localize(_(&#34;")) {
					int index1 = data.indexOf("$localize(_(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;quot;"))
				while (data.contains("$localize(_(&amp;quot;")) {
					int index1 = data.indexOf("$localize(_(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(&amp;apos;"))
				while (data.contains("$localize(_(&amp;apos;")) {
					int index1 = data.indexOf("$localize(_(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(_(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;apos;"))
				while (data.contains("$localize(gettext(&amp;apos;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;apos;");
					int index = data.indexOf("&amp;apos;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;apos;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;apos;" + substr1 + "&amp;apos;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_(\\\""))
				while (data.contains("$localize(_(\\\"")) {
					int index1 = data.indexOf("$localize(_(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(_('"))
				while (data.contains("$localize(_('")) {
					int index1 = data.indexOf("$localize(_('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(_('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(_('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext('"))
				while (data.contains("$localize(gettext('")) {
					int index1 = data.indexOf("$localize(gettext('");
					int index = data.indexOf("'))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 = data.substring(index1 + "$localize(gettext('".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext('" + substr1 + "'))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\""))
				while (data.contains("$localize(gettext(\"")) {
					int index22 = -1;
					if (data.contains("$localize(gettext(\" $localize(gettext(")) {
						index22 = data.indexOf("$localize(gettext(\" $localize(gettext(");
					}
					int index = data.indexOf("\"))", index22);

					String substr22 = "";
					if (index22 != -1 && index != -1)
						substr22 =
							data.substring(
									index22 + "$localize(gettext(\" $localize(gettext(".length()
											+ 1, index);

					if (!"".equals(substr22))
						data =
							data.replace("$localize(gettext(\" $localize(gettext(\"" + substr22
									+ "\"))\"))", CourseLocalization.gettext(bundle, substr22));

					int index2 = data.indexOf("$localize(gettext(");
					index = data.indexOf("\"))", index2);

					String substr2 = "";

					if (index2 != -1 && index != -1)
						substr2 =
							data.substring(index2 + "$localize(gettext(".length() + 1, index);

					if (!"".equals(substr2))
						data =
							data.replace("$localize(gettext(\"" + substr2 + "\"))",
									CourseLocalization.gettext(bundle, substr2));
				}

			if (data.contains("$localize(gettext(&#34;"))
				while (data.contains("$localize(gettext(&#34;")) {
					int index1 = data.indexOf("$localize(gettext(&#34;");
					int index = data.indexOf("&#34;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&#34;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(&#34;" + substr1 + "&#34;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(&amp;quot;"))
				while (data.contains("$localize(gettext(&amp;quot;")) {
					int index1 = data.indexOf("$localize(gettext(&amp;quot;");
					int index = data.indexOf("&amp;quot;))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(&amp;quot;".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace(
									"$localize(gettext(&amp;quot;" + substr1 + "&amp;quot;))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(gettext(\\\""))
				while (data.contains("$localize(gettext(\\\"")) {
					int index1 = data.indexOf("$localize(gettext(\\\"");
					int index = data.indexOf("\\\"))", index1);

					String substr1 = "";

					if (index1 != -1 && index != -1)
						substr1 =
							data.substring(index1 + "$localize(gettext(\\\"".length(), index);

					if (!"".equals(substr1))
						data =
							data.replace("$localize(gettext(\\\"" + substr1 + "\\\"))",
									CourseLocalization.gettext(bundle, substr1));
				}

			if (data.contains("$localize(ngettext(\""))
				while (data.contains("$localize(ngettext(\"")) {
					int index1 = data.indexOf("$localize(ngettext(\"");
					int index2 = data.indexOf("\",", index1 + 1);
					int index3 = data.indexOf("\"", index2 + 1);
					int index4 = data.indexOf("\",", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 =
							data.substring(index1 + "$localize(ngettext(\"".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

			if (data.contains("$localize(ngettext('"))
				while (data.contains("$localize(ngettext('")) {
					int index1 = data.indexOf("$localize(ngettext('");
					int index2 = data.indexOf("',", index1 + 1);
					int index3 = data.indexOf("'", index2 + 1);
					int index4 = data.indexOf("',", index3 + 1);
					int index5 = data.indexOf("),", index1);
					int index6 = data.indexOf(")", index5 + 1);

					String substr1 = "";
					if (index1 != -1 && index2 != -1)
						substr1 = data.substring(index1 + "$localize(ngettext('".length(), index2);

					String substr2 = "";
					if (!"".equals(substr1) && index3 != -1 && index4 != -1)
						substr2 = data.substring(index3 + 1, index4);

					int number = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && index5 != -1)
						number = Integer.parseInt(data.substring(index4 + 2, index5).trim());

					int number2 = -1;
					if (!"".equals(substr1) && !"".equals(substr2) && number != -1 && index6 != -1)
						number2 = Integer.parseInt(data.substring(index5 + 2, index6).trim());

					data =
						data.replace(data.substring(index1, index6 + 1), String.format(
								CourseLocalization.ngettext(bundle, substr1, substr2, number),
								number2));
				}

		}

		return data;
	}

}
