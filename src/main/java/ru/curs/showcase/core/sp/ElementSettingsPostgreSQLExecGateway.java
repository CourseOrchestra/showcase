package ru.curs.showcase.core.sp;

import java.sql.SQLException;

import ru.curs.showcase.util.Description;

/**
 * Реализация шлюза к PostgreSQL с sql скриптами для получения настроек
 * элементов.
 * 
 */
@Description(
		process = "Загрузка метаданных для элемента инф. панели из БД c помощью выполнения SQL скрипта")
public class ElementSettingsPostgreSQLExecGateway extends ElementSettingsDBGateway {

	@Override
	protected void prepareSQL() throws SQLException {

		final PostgreSQLExecGateway pgsql = new PostgreSQLExecGateway(this) {
			@Override
			protected String getParamsDeclaration() {
				return STD_PARAMS + ", OUT settings xml, OUT error_mes text";
			}

			@Override
			protected void adjustParentProcName(final String name) {
				getElementInfo().getMetadataProc().setName(name);
			}

		};
		pgsql.createTempFunction();

		super.prepareSQL();

	}

}
