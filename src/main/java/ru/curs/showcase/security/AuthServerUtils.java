package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.xml.transform.TransformerException;

import org.slf4j.*;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Набор утилит (функций) для работы с сервлетами сервера аутентификации.
 */
public final class AuthServerUtils {
	private static final String REQUEST_METHOD = "GET";

	private static final String LOGOUT_WARN = "Не удалось разлогиниться с AuthServer";

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerUtils.class);

	public static final String AUTH_SERVER_DATA_ERROR =
		"Ошибка при разборе данных, возвращенных AuthServer: ";
	/**
	 * Экземпляр-псевдоним псевдоним для работы с сервлетами сервера
	 * аутентификации.
	 */
	private static AuthServerUtils theAuthServerAlias;

	/**
	 * Адрес сервера аутентификации.
	 */
	private final String authServerURL;

	private AuthServerUtils(final String url) {
		this.authServerURL = url;
	}

	/**
	 * Возвращает экземпляр-псевдоним для работы с сервером аутентификации (если
	 * таковой имеется).
	 * 
	 * @return theAuthServerAlias
	 * 
	 */
	public static AuthServerUtils getTheAuthServerAlias() {
		return theAuthServerAlias;
	}

	/**
	 * Производит аутентификацию по заданному логину и паролю.
	 * 
	 * @param sesid
	 *            Идентификатор сессии
	 * @param login
	 *            Логин
	 * @param pwd
	 *            Переданный пароль
	 * @return true, если аутентификация удалась
	 */
	public boolean login(final String sesid, final String login, final String pwd) {
		if (authServerURL == null) {
			return false;
		}
		HttpURLConnection c = null;
		try {
			URL server = new URL(authServerURL
					+ String.format("/login?sesid=%s&login=%s&pwd=%s", sesid, login, pwd));
			c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod(REQUEST_METHOD);
			c.connect();
			return c.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false;
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}
	}

	/**
	 * Производит выход из сессии аутентификации.
	 * 
	 * @param sesid
	 *            Идентификатор сессии.
	 */
	public void logout(final String sesid) {
		if (authServerURL != null) {
			HttpURLConnection c = null;
			try {
				URL server = new URL(authServerURL + String.format("/logout?sesid=%s", sesid));
				c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod(REQUEST_METHOD);
				c.connect();
				c.getResponseCode();
			} catch (IOException e) {
				// Do nothing, not our problems.
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
					LOGGER.warn(LOGOUT_WARN);
				}
			} finally {
				if (c != null) {
					c.disconnect();
				}
			}

		}
	}

	/**
	 * Проверяет валидность соединения и работоспособность сервера
	 * аутентификации.
	 * 
	 * @return true если сервер аутентификации работоспособен и доступен.
	 */
	public boolean checkServer() {
		if (authServerURL == null) {
			return false;
		}
		HttpURLConnection c = null;
		try {
			URL server = new URL(authServerURL + "/authentication.gif");
			c = (HttpURLConnection) server.openConnection();
			c.connect();
			return c.getResponseCode() == HttpURLConnection.HTTP_OK
					&& c.getContentType().startsWith("image/gif");
		} catch (IOException e) {
			return false;
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}
	}

	/**
	 * Фабричный метод генерации клиента менеджера аутентификации.
	 * 
	 * @param url
	 *            URL соединения с менеджером аутентификации, настроенным на
	 *            взаимодействие с LDAP.
	 */
	public static void init(final String url) {

		// На этапе создания проверяется только правильность URL.
		if (url != null) {
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Проверка URL", e);
				}
			}
		}
		theAuthServerAlias = new AuthServerUtils(url);
	}

	/**
	 * Проверяет введённое имя пользователя и возвращает SID.
	 * 
	 * @param sesid
	 *            Идентификатор сессии
	 * @param login
	 *            Проверяемый логин
	 * @return UserData
	 */
	public UserInfo checkUser(final String sesid, final String login) {
		if (authServerURL == null) {
			return null;
		}
		HttpURLConnection c = null;
		try {
			URL server = new URL(
					authServerURL + String.format("/checkname?sesid=%s&name=%s", sesid, login));
			c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod(REQUEST_METHOD);
			c.setDoInput(true);
			c.connect();
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				List<UserInfo> l = UserInfoUtils.parseStream(c.getInputStream());
				if (l.isEmpty()) {
					return null;
				} else {
					return l.get(0);
				}
			} else {
				return null;
			}
		} catch (IllegalStateException | TransformerException | SecurityException
				| IllegalFormatException | NullPointerException | IOException
				| IndexOutOfBoundsException e) {

			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Проверка пользователя", e);
			}
			return null;
		} finally {
			if (c != null) {
				c.disconnect();
			}
		}
	}

	/**
	 * Возвращает информацию о текущем пользователе, если сессия
	 * аутентифицирована, и null, если сессия не аутентифицирована.
	 * 
	 * @param sesid
	 *            текущая сессия
	 * @return UserData
	 */
	public UserInfo isAuthenticated(final String sesid) {
		if (authServerURL == null) {
			return null;
		}
		try {
			URL server =
				new URL(authServerURL + String.format("/isauthenticated?sesid=%s", sesid));

			HttpURLConnection c = null;
			try {
				c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod(REQUEST_METHOD);
				c.setDoInput(true);
				c.connect();
				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					List<UserInfo> l = UserInfoUtils.parseStream(c.getInputStream());
					if (l.isEmpty()) {
						return null;
					} else {
						return l.get(0);
					}
				} else {
					return null;
				}
			} finally {
				if (c != null)
					c.disconnect();
			}

		} catch (IllegalStateException | TransformerException | SecurityException
				| IllegalFormatException | NullPointerException | IOException
				| IndexOutOfBoundsException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Проверка входа в систему", e);
			}
			return null;
		}
	}

	/**
	 * Возвращает URL сервера аутентификации.
	 * 
	 * @return authServerURL
	 */
	public String getUrl() {
		return authServerURL;
	}

}
