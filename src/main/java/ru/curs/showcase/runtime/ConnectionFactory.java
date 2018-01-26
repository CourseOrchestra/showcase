package ru.curs.showcase.runtime;

import java.sql.*;

import org.slf4j.*;

import com.ziclix.python.sql.PyConnection;

/**
 * Фабрика для создания соединений с БД.
 * 
 * @author den
 * 
 */
public final class ConnectionFactory extends PoolByUserdata<Connection> {
	/**
	 * Параметр файла настроек приложения, содержащий адрес для соединения с SQL
	 * сервером через JDBC.
	 */
	public static final String CONNECTION_URL_PARAM = "rdbms.connection.url";
	private static final String CONNECTION_USERNAME_PARAM = "rdbms.connection.username";
	private static final String CONNECTION_PASSWORD_PARAM = "rdbms.connection.password";

	private static final ConnectionFactory INSTANCE = new ConnectionFactory();

	protected static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);

	private ConnectionFactory() {
		super();
	}

	public static ConnectionFactory getInstance() {
		return INSTANCE;
	}

	@Override
	protected Pool<Connection> getLock() {
		return ConnectionFactory.getInstance();
	}

	@Override
	protected Connection createReusableItem() {
		try {
			registerDriver();
			Connection result =
				DriverManager.getConnection(UserDataUtils.getRequiredProp(CONNECTION_URL_PARAM),
						UserDataUtils.getRequiredProp(CONNECTION_USERNAME_PARAM),
						UserDataUtils.getRequiredProp(CONNECTION_PASSWORD_PARAM));
			return result;
		} catch (SQLException e) {
			throw new DBConnectException(e);
		}
	}

	@Override
	protected boolean checkForValidity(final Connection conn) {
		try {
			return conn.isValid(0);
		} catch (SQLException e) {
			return false;
		}
	}

	public static PyConnection getPyConnection() {
		try {
			return new PyConnection(getInstance().acquire());
		} catch (SQLException e) {
			throw new DBConnectException(e);
		}
	}

	/**
	 * Возвращает тип SQL сервера.
	 */
	public static SQLServerType getSQLServerType() {
		String url = UserDataUtils.getRequiredProp(CONNECTION_URL_PARAM).toLowerCase();

		// TODO regexp
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

		return st;
	}

	/**
	 * Возвращает тип SQL сервера для userdata по умолчанию.
	 */
	public static SQLServerType getSQLServerTypeForDefaultUserdata() {
		String url = UserDataUtils.getOptionalProp(CONNECTION_URL_PARAM, "default").toLowerCase();

		// TODO regexp
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

		return st;
	}

	public static Driver unregisterDrivers() {
		Driver result = null;
		while (DriverManager.getDrivers().hasMoreElements()) {
			try {
				result = DriverManager.getDrivers().nextElement();
				DriverManager.deregisterDriver(result);
			} catch (SQLException e) {
				throw new DBConnectException(e);
			}
		}
		return result;
	}

	protected static Driver registerDriver() {
		Driver result = null;
		try {
			if (getSQLServerType() == SQLServerType.POSTGRESQL) {
				result = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			} else if (getSQLServerType() == SQLServerType.ORACLE) {
				result = (Driver) Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			} else if (getSQLServerType() == SQLServerType.MSSQL) {
				result =
					(Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
							.newInstance();
			} else {
				return null;
			}
			DriverManager.registerDriver(result);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException
				| SQLException e) {
			throw new DBConnectException(e);
		}
		return result;
	}

	private static final String ERROR_CAPTION = "Сообщение об ошибке";

	@Override
	protected void cleanReusable(final Connection aReusable) {
		if (getSQLServerType() == SQLServerType.POSTGRESQL) {
			try {
				if (!aReusable.getAutoCommit()) {
					aReusable.commit();
				}
			} catch (SQLException e) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error(ERROR_CAPTION, e);
				}
			}
		}
	}
}
