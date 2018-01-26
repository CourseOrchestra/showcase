package ru.curs.showcase.app.server.redirection;

import java.io.*;
import java.util.*;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.TextUtils;

/**
 * Получает настройки приложения, касающиеся переадресации статического
 * контента, из файла redirect.properties в каталоге пользовательских данных
 * (userdatas).
 * 
 */
public final class RedirectionUserdataProp {

	/**
	 * Имя файла с настройками переадресации приложения.
	 */

	public static final String REDIRECTPROPFILENAME = "redirect.properties";
	// public static final String SERVER_STATIC_URL = "server.static.url";
	public static final String PATH_TO_REDIRECT = "path.to.redirect";
	public static final String EXTENSION_TO_REDIRECT = "extension.to.redirect";
	public static final String REDIRECTION_PROC = "redirection.proc";

	private static String redirectPropFile;

	private static final Logger LOGGER = LoggerFactory.getLogger(RedirectionUserdataProp.class);

	// private static String redirectionServerUrl;

	private static ArrayList<String> pathToRedirect = new ArrayList<String>();
	private static ArrayList<String> extensionToRedirect = new ArrayList<String>();
	private static String redirectionProc = "";

	public static String getRedirectionProc() {
		return redirectionProc;
	}

	public static void setRedirectionProc(final String aredirectionProc) {
		RedirectionUserdataProp.redirectionProc = aredirectionProc;
	}

	// public static String getRedirectionServerUrl() {
	// return redirectionServerUrl;
	// }

	// public static void setRedirectionServerUrl(final String
	// aredirectionServerUrl) {
	// RedirectionUserdataProp.redirectionServerUrl = aredirectionServerUrl;
	// }

	public static void setRedirectPropFile(final String aRedirectPropFile) {
		redirectPropFile = aRedirectPropFile;
	}

	private RedirectionUserdataProp() {
		throw new UnsupportedOperationException();
	}

	public static Properties getRedirectOptionalProp() {
		Properties prop = new Properties();
		try {
			InputStream is = new FileInputStream(getRedirectPropFile());
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				prop.load(reader);
			}
		} catch (IOException e) {
			prop = null;
			LOGGER.warn("ВНИМАНИЕ! Для использования возможности"
					+ " переадресации статического контента,"
					+ " разместите файл redirect.properties в корневой директории папки"
					+ " пользовательских данных. Если Вы не используете переадресацию,"
					+ " проигнорируйте данное сообщение: " + e.getMessage());
		}
		return prop;
	}

	public static void readAndSetRedirectproperties() {
		Properties prop;
		prop = getRedirectOptionalProp();

		if (prop == null) {
			return;
		}
		String tempStr = prop.getProperty(REDIRECTION_PROC);
		if (tempStr != null) {
			setRedirectionProc(tempStr);
		}

		tempStr = null;
		tempStr = prop.getProperty(EXTENSION_TO_REDIRECT);
		if (tempStr != null) {
			String[] tempExtArr = tempStr.split(",");
			for (int i = 0; i < tempExtArr.length; i++) {
				tempExtArr[i] = tempExtArr[i].trim();
			}
			setExtensionToRedirect(new ArrayList<String>(Arrays.asList(tempExtArr)));
		}

		tempStr = null;
		tempStr = prop.getProperty(PATH_TO_REDIRECT);
		if (tempStr != null) {
			String[] tempPathArr = tempStr.split(",");
			for (int i = 0; i < tempPathArr.length; i++) {
				tempPathArr[i] = tempPathArr[i].trim();
			}
			setPathToRedirect(new ArrayList<String>(Arrays.asList(tempPathArr)));
		}

	}

	private static String getRedirectPropFile() {
		if (redirectPropFile == null) {

			setRedirectPropFile(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
					+ REDIRECTPROPFILENAME);

			return AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + REDIRECTPROPFILENAME;
		}
		return redirectPropFile;
	}

	public static ArrayList<String> getExtensionToRedirect() {
		return extensionToRedirect;
	}

	public static void setExtensionToRedirect(final ArrayList<String> aextensionToRedirect) {
		RedirectionUserdataProp.extensionToRedirect = aextensionToRedirect;
	}

	public static ArrayList<String> getPathToRedirect() {
		return pathToRedirect;
	}

	public static void setPathToRedirect(final ArrayList<String> apathToRedirect) {
		RedirectionUserdataProp.pathToRedirect = apathToRedirect;
	}

}
