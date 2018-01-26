package ru.curs.showcase.core.primelements.navigator;

import java.sql.SQLException;

import ru.curs.showcase.core.sp.PostgreSQLExecGateway;
import ru.curs.showcase.util.Description;

/**
 * Реализация шлюза к PostgreSQL с sql скриптами для получения данных для
 * навигатора.
 * 
 */
@Description(
		process = "Загрузка данных для информационной панели из БД с помощью выполнения sql скрипта")
public class NavigatorPostgreSQLExecGateway extends NavigatorDBGateway {

	@Override
	protected void prepareSQL() throws SQLException {

		final PostgreSQLExecGateway pgsql = new PostgreSQLExecGateway(this) {
			@Override
			protected String getParamsDeclaration() {
				return "OUT error_code int4, IN session_context xml, OUT data xml";
			}

		};
		pgsql.createTempFunction();

		super.prepareSQL();

	}

}
