package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.SQLException;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Базовый шлюз для выполнения sql файлов в MSSQL СУБД. Выполнение происходит
 * при помощи процедуры sp_executesql. 2 первых параметра процедуры: это
 * содержимое sql файла и список параметров. 2 последних: код возврата и
 * сообщение об ошибке.
 * 
 * @author den
 * 
 */
public abstract class MSSQLExecGateway extends SPQuery {

	public static final String SCRIPTS_SQL_DIR = "scripts/sql/";

	private SPQuery external;

	public MSSQLExecGateway() {
		super();
	}

	public MSSQLExecGateway(final SPQuery aExternal) {
		super();
		external = aExternal;
	}

	private SPQuery self() {
		if (external != null) {
			return external;
		}
		return this;
	}

	protected String getParamsDeclaration() {
		return "@return int output, @error_mes varchar(MAX) output";
	}

	@Override
	public int getReturnParamIndex() {
		return getParamCount() - 1;
	}

	@Override
	public int getErrorMesIndex(final int index) {
		return getParamCount();
	}

	@Override
	public void prepareSQL() throws SQLException {
		if (self().getConn() == null) {
			self().setConn(ConnectionFactory.getInstance().acquire());
		}
		self().setStatement(self().getConn().prepareCall(self().getSqlText()));
		try {
			File file =
				new File(UserDataUtils.getUserDataCatalog() + File.separator + getFileName());
			if (file.exists()) {
				self().setStringParam(
						1,
						TextUtils.streamToString(UserDataUtils.loadUserDataToStream(getFileName())));
			} else {
				self().setStringParam(1,
						TextUtils.streamToString(UserDataUtils.loadGeneralToStream(getFileName())));
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(getFileName(), SettingsFileType.SQL);
		}
		self().setStringParam(2, getParamsDeclaration());
		addErrorMesParams();
	}

	private String getFileName() {
		return SCRIPTS_SQL_DIR + self().getProcName();
	}

	private void addErrorMesParams() throws SQLException {
		self().getStatement().registerOutParameter(getReturnParamIndex(), java.sql.Types.INTEGER);
		self().getStatement().registerOutParameter(getErrorMesIndex(0), java.sql.Types.VARCHAR);
	}

	private int getParamCount() {
		return getParamsDeclaration().split(",").length + 2;
	}

	@Override
	public String getSqlTemplate(final int index) {
		String template = "{call sp_executesql (?, ?, %s ?, ?)}";
		StringBuilder specialParams = new StringBuilder();
		for (int i = 0; i < getParamCount() - 2 - 2; i++) {
			specialParams.append("?, ");
		}
		return String.format(template, specialParams.toString());
	}

	@Override
	public int getTemplateIndex() {
		return self().getTemplateIndex();
	}

}