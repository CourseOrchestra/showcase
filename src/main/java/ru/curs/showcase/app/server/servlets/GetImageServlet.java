/**
 * 
 */
package ru.curs.showcase.app.server.servlets;

import java.io.*;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.runtime.*;

/**
 * @author a.Lugovtsov
 * 
 */
public class GetImageServlet extends HttpServlet {

	public static final String CONNECTION_URL_PARAM = "rdbms.connection.url";
	private static final String CONNECTION_USERNAME_PARAM = "rdbms.connection.username";
	private static final String CONNECTION_PASSWORD_PARAM = "rdbms.connection.password";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -5116327914400230233L;

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String procName = request.getParameter("proc");
		String paramsForProc = request.getParameter("params");
		String typeOfReterningDate = request.getParameter("type");
		if ((typeOfReterningDate == null) || typeOfReterningDate.isEmpty()) {
			typeOfReterningDate = "image/jpeg";
		} else {
			switch (typeOfReterningDate) {
			case "pdf":
				typeOfReterningDate = "Application/pdf";
				break;
			default:
				typeOfReterningDate = "image/jpeg";
			}

		}

		response.reset();

		// response.setHeader("Pragma", "no-cache");
		// response.setHeader("Cache-Control",
		// "must-revalidate,no-store,no-cache");

		long now = System.currentTimeMillis();

		// столько секунд в году
		final long chachDurationInMs = 60 * 60 * 24 * 365 * 1000;

		// кэшируем на год
		response.setDateHeader("Expires", now + chachDurationInMs);

		response.setContentType(typeOfReterningDate);
		response.setCharacterEncoding("UTF-8");

		response.setStatus(HttpServletResponse.SC_OK);

		// AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);

		// UserDataUtils.get

		//
		// ConnectionFactory.getSQLServerTypeForDefaultUserdata();

		//

		Connection conn = null;
		try {
			registerDriver();
			conn =
				DriverManager.getConnection(
						UserDataUtils.getOptionalProp(CONNECTION_URL_PARAM, "default"),
						UserDataUtils.getOptionalProp(CONNECTION_USERNAME_PARAM, "default"),
						UserDataUtils.getOptionalProp(CONNECTION_PASSWORD_PARAM, "default"));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Connection conn = ConnectionFactory.getInstance().acquire();

		CallableStatement cstmt = null;
		OutputStream os = response.getOutputStream();
		try {
			try {
				String SQL = "{call " + procName + " (?, ?, ?)}";
				cstmt = conn.prepareCall(SQL);
				// cstmt.set
				cstmt.setString(1, "parameter1_anlug");
				cstmt.setString(2, paramsForProc);

				switch (ConnectionFactory.getSQLServerTypeForDefaultUserdata()) {
				case MSSQL:
					cstmt.registerOutParameter(3, java.sql.Types.BLOB);
					break;
				case POSTGRESQL:
					cstmt.registerOutParameter(3, java.sql.Types.BINARY);
					break;
				default:
					cstmt.registerOutParameter(3, java.sql.Types.BLOB);
					break;
				}

				cstmt.execute();

				byte[] fdf = cstmt.getBytes(3);

				os.write(fdf, 0, fdf.length);

			} finally {
				os.close();

				cstmt.close();
				conn.close();

			}
		} catch (SQLException e) {
			System.out.print(e.getMessage());

		}

	}

	protected static Driver registerDriver() {
		Driver result = null;
		try {
			if (ConnectionFactory.getSQLServerTypeForDefaultUserdata() == SQLServerType.POSTGRESQL) {
				result = (Driver) Class.forName("org.postgresql.Driver").newInstance();
			} else if (ConnectionFactory.getSQLServerTypeForDefaultUserdata() == SQLServerType.ORACLE) {
				result = (Driver) Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			} else if (ConnectionFactory.getSQLServerTypeForDefaultUserdata() == SQLServerType.MSSQL) {
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

}
