package ru.curs.showcase.core.grid;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementSubType;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.sp.PostgreSQLExecGateway;
import ru.curs.showcase.util.Description;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Реализация шлюза к PostgreSQL с sql скриптами для получения данных для грида.
 * 
 */
@Description(process = "Загрузка данных для грида из БД c помощью SQL скрипта")
public class GridPostgreSQLExecGateway extends GridDBGateway {

	@Override
	protected void prepareSQL() throws SQLException {

		final PostgreSQLExecGateway pgsql = new PostgreSQLExecGateway(this) {
			// CHECKSTYLE:OFF
			@Override
			protected String getParamsDeclaration() {
				switch (templateIndex()) {
				case DATA_AND_SETTINS_QUERY:
					if (getGridContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
						return STD_PARAMS
								+ ", IN sortcols text, IN parent_id text, OUT settings xml, OUT error_mes text";
					} else {
						return STD_PARAMS
								+ ", IN sortcols text, OUT settings xml, OUT error_mes text";
					}
				case DATA_ONLY_QUERY:
					if (getGridContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
						return "main_context text, add_context text, filterinfo xml, "
								+ "session_context xml, element_id text, sortcols text, firstrecord int4, pagesize int4, parent_id text";
					} else {
						return "main_context text, add_context text, filterinfo xml, "
								+ "session_context xml, element_id text, sortcols text, firstrecord int4, pagesize int4";
					}
				default:
					throw new NotImplementedYetException();
				}
			}

			// CHECKSTYLE:ON

			@Override
			protected String getReturnsDeclaration() {
				switch (templateIndex()) {
				case DATA_ONLY_QUERY:
					return "pg_catalog.refcursor";
				default:
					return super.getReturnsDeclaration();
				}
			}

		};
		pgsql.createTempFunction();

		super.prepareSQL();

	}

	private int templateIndex() {
		return getTemplateIndex();
	}

	private GridContext getGridContext() {
		return getContext();
	}

}
