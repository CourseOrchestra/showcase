package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.SQLException;

import org.slf4j.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Базовый шлюз для выполнения sql файлов в PostgreSQL.
 * 
 */
public abstract class PostgreSQLExecGateway extends SPQuery {

	private SPQuery external;

	private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSQLExecGateway.class);

	private static final String TEMPLATE_CREATE_FUNCTION = "CREATE OR REPLACE FUNCTION "
			+ POSTGRESQL_TEMP_SCHEMA
			+ ".%s(%s) RETURNS %s AS $BODY$\n%s\n$BODY$ LANGUAGE 'plpgsql' VOLATILE;";

	protected static final String STD_PARAMS =
		"OUT error_code int4, IN main_context text, IN add_context text, IN filterinfo xml, "
				+ "IN session_context xml, IN element_id text";

	public PostgreSQLExecGateway() {
		super();
	}

	public PostgreSQLExecGateway(final SPQuery aExternal) {
		super();
		external = aExternal;
	}

	private SPQuery self() {
		if (external != null) {
			return external;
		}
		return this;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return null;
	}

	protected abstract String getParamsDeclaration();

	protected String getReturnsDeclaration() {
		return "pg_catalog.record";
	}

	private String getFileName() {
		return SCRIPTS_SQL_DIR + self().getProcName();
	}

	public void createTempFunction() {
		try {
			if (self().getConn() == null) {
				self().setConn(ConnectionFactory.getInstance().acquire());
			}

			setProcName(self().getProcName());

			String proc = "\"" + TextUtils.extractFileName(self().getProcName()) + "\"";

			String script;
			try {
				File file =
					new File(UserDataUtils.getUserDataCatalog() + File.separator + getFileName());
				if (file.exists()) {
					script =
						TextUtils
								.streamToString(UserDataUtils.loadUserDataToStream(getFileName()));
				} else {
					script =
						TextUtils.streamToString(UserDataUtils.loadGeneralToStream(getFileName()));
				}
			} catch (IOException e) {
				throw new SettingsFileOpenException(getFileName(), SettingsFileType.SQL);
			}

			String sql =
				String.format(TEMPLATE_CREATE_FUNCTION, proc, getParamsDeclaration(),
						getReturnsDeclaration(), script);

			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				Marker marker = MarkerFactory.getDetachedMarker(SQL_MARKER);
				marker.add(HandlingDirection.INPUT.getMarker());
				LOGGER.info(marker, sql);
			}

			self().getConn().createStatement().execute(sql);

			adjustParentProcName(POSTGRESQL_TEMP_SCHEMA + "." + proc);

		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}

	}

	protected void adjustParentProcName(final String name) {
		self().setProcName(name);
	}

}