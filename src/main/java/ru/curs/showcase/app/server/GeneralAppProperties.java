package ru.curs.showcase.app.server;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.*;

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
			Driver result = null;
			try {
				String url =
					getProperty(UserDataUtils.RDBMS_PREFIX + UserDataUtils.CELESTA_CONNECTION_URL)
							.toLowerCase();

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
						(Driver) Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				} else if (st == SQLServerType.MSSQL) {
					result =
						(Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
								.newInstance();
				}

				DriverManager.registerDriver(result);
				DriverManager.getConnection(getProperty(UserDataUtils.RDBMS_PREFIX
						+ UserDataUtils.CELESTA_CONNECTION_URL),
						getProperty(UserDataUtils.RDBMS_PREFIX
								+ UserDataUtils.CELESTA_CONNECTION_USERNAME),
						getProperty(UserDataUtils.RDBMS_PREFIX
								+ UserDataUtils.CELESTA_CONNECTION_PASSWORD));

			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException
					| SQLException e) {
				throw new RuntimeException("Не удаётся подключиться к указанной базе данных");
			}
		}

		if (getProperty(SecurityParamsFactory.LOCAL_AUTH_SERVER_URL_PARAM) == null) {
			throw new SettingsFileRequiredPropException(AppInfoSingleton.getAppInfo()
					.getUserdataRoot() + "/" + UserDataUtils.GENERALPROPFILENAME,
					SecurityParamsFactory.LOCAL_AUTH_SERVER_URL_PARAM,
					SettingsFileType.GENERAL_APP_PROPERTIES);
		} else {
			URL server;
			HttpURLConnection c = null;
			try {
				server = new URL(getProperty(SecurityParamsFactory.LOCAL_AUTH_SERVER_URL_PARAM));

				c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod("GET");
				c.setReadTimeout(3000);
				c.setDoInput(true);

				if (getProperty("zero.configuration.check.mellophone") == null
						|| "true".equalsIgnoreCase(getProperty(
								"zero.configuration.check.mellophone").trim())) {
					c.connect();
				}

			} catch (IOException e) {
				throw new RuntimeException(
						"Невозможно подключиться к меллофону по указанному адресу", e);
			} finally {
				if (c != null) {
					c.disconnect();
				}
			}
		}
	}
}
