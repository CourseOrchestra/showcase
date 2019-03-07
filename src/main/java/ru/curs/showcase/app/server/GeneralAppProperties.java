package ru.curs.showcase.app.server;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.*;

import org.slf4j.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.util.exception.*;

/**
 * Класс, содержащий в себе карту, представляющую список свойств из файла
 * generalapp.properties.
 * 
 * @author s.borodanev
 */

public class GeneralAppProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneralAppProperties.class);

	private final Map<String, String> map = new HashMap<String, String>();

	public Map<String, String> getMap() {
		return map;
	}

	public String getProperty(String key) {
		return map.get(key);
	}

	public void initialize() {
		Properties props = UserDataUtils.getGeneralProperties();
		String key = "";
		String value = "";
		for (Object k : props.keySet()) {
			key = (String) k;
			value = props.getProperty(key);
			getMap().put(key, value);
		}

		if (getProperty(UserDataUtils.RDBMS_PREFIX + UserDataUtils.CELESTA_CONNECTION_URL) == null) {
			throw new SettingsFileRequiredPropException(AppInfoSingleton.getAppInfo()
					.getUserdataRoot() + "/" + UserDataUtils.GENERALPROPFILENAME,
					UserDataUtils.RDBMS_PREFIX + UserDataUtils.CELESTA_CONNECTION_URL,
					SettingsFileType.GENERAL_APP_PROPERTIES);
		} else {
			String databaseConnectionsAmount =
				getProperty("zero.configuration.database.connections.amount");
			String databaseConnectionsInterval =
				getProperty("zero.configuration.database.connections.interval");

			int retriesMax =
				databaseConnectionsAmount != null ? (Integer.valueOf(databaseConnectionsAmount
						.trim()) + 1) : 2;

			for (int retries = 1; retries < retriesMax; retries++) {

				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.info("Идёт проверка подключения к базе данных. Попытка " + retries);
				}

				Driver result = null;
				try {
					String url =
						getProperty(
								UserDataUtils.RDBMS_PREFIX + UserDataUtils.CELESTA_CONNECTION_URL)
								.trim().toLowerCase();

					final String mssql = "sqlserver";
					final String postgresql = "postgresql";
					final String oracle = "oracle";

					SQLServerType st = null;
					if (url.indexOf(mssql) > -1) {
						st = SQLServerType.MSSQL;
					} else {
						if (url.indexOf(postgresql) > -1) {
							st = SQLServerType.POSTGRESQL;
						} else {
							if (url.indexOf(oracle) > -1) {
								st = SQLServerType.ORACLE;
							}
						}
					}

					if (st == SQLServerType.POSTGRESQL) {
						result = (Driver) Class.forName("org.postgresql.Driver").newInstance();
					} else if (st == SQLServerType.ORACLE) {
						result =
							(Driver) Class.forName("oracle.jdbc.driver.OracleDriver")
									.newInstance();
					} else if (st == SQLServerType.MSSQL) {
						result =
							(Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
									.newInstance();
					}

					DriverManager.registerDriver(result);
					DriverManager.getConnection(
							getProperty(
									UserDataUtils.RDBMS_PREFIX
											+ UserDataUtils.CELESTA_CONNECTION_URL).trim(),
							getProperty(
									UserDataUtils.RDBMS_PREFIX
											+ UserDataUtils.CELESTA_CONNECTION_USERNAME).trim(),
							getProperty(
									UserDataUtils.RDBMS_PREFIX
											+ UserDataUtils.CELESTA_CONNECTION_PASSWORD).trim());

					retries = retriesMax;
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException
						| SQLException e) {
					if (databaseConnectionsAmount != null
							&& retries < Integer.valueOf(databaseConnectionsAmount.trim())) {
						try {
							Thread.sleep(databaseConnectionsInterval != null ? Long
									.valueOf(databaseConnectionsInterval.trim()) : 0L);
						} catch (NumberFormatException | InterruptedException e1) {
						}
						continue;
					} else {
						throw new RuntimeException(
								"Не удаётся подключиться к указанной базе данных");
					}
				}
			}
		}

		if (getProperty(SecurityParamsFactory.LOCAL_AUTH_SERVER_URL_PARAM) == null) {
			throw new SettingsFileRequiredPropException(AppInfoSingleton.getAppInfo()
					.getUserdataRoot() + "/" + UserDataUtils.GENERALPROPFILENAME,
					SecurityParamsFactory.LOCAL_AUTH_SERVER_URL_PARAM,
					SettingsFileType.GENERAL_APP_PROPERTIES);
		} else {
			String mellophoneConnectionsAmount =
				getProperty("zero.configuration.mellophone.connections.amount");
			String mellophoneConnectionsInterval =
				getProperty("zero.configuration.mellophone.connections.interval");

			int retriesMax =
				mellophoneConnectionsAmount != null ? (Integer.valueOf(mellophoneConnectionsAmount
						.trim()) + 1) : 2;

			for (int retries = 1; retries < retriesMax; retries++) {

				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.info("Идёт проверка подключения к меллофону. Попытка " + retries);
				}

				URL server;
				HttpURLConnection c = null;
				try {
					server =
						new URL(getProperty(SecurityParamsFactory.LOCAL_AUTH_SERVER_URL_PARAM)
								.trim());

					c = (HttpURLConnection) server.openConnection();
					c.setRequestMethod("GET");
					c.setReadTimeout(3000);
					c.setDoInput(true);

					if (getProperty("zero.configuration.check.mellophone") == null
							|| "true".equalsIgnoreCase(getProperty(
									"zero.configuration.check.mellophone").trim())) {
						c.connect();
					}

					retries = retriesMax;
				} catch (IOException e) {
					if (mellophoneConnectionsAmount != null
							&& retries < Integer.valueOf(mellophoneConnectionsAmount.trim())) {
						try {
							Thread.sleep(mellophoneConnectionsInterval != null ? Long
									.valueOf(mellophoneConnectionsInterval.trim()) : 0L);
						} catch (NumberFormatException | InterruptedException e1) {
						}
						continue;
					} else {
						throw new RuntimeException(
								"Невозможно подключиться к меллофону по указанному адресу", e);
					}
				} finally {
					if (c != null) {
						c.disconnect();
					}
				}
			}
		}
	}
}
