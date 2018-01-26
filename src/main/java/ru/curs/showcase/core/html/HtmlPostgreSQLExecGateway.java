package ru.curs.showcase.core.html;

import java.sql.SQLException;

import ru.curs.showcase.core.sp.PostgreSQLExecGateway;
import ru.curs.showcase.util.Description;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Реализация шлюза к PostgreSQL с sql скриптами для получения данных для
 * элементов инф. панели типа вебтекст, xform и UI плагин.
 * 
 */
@Description(
		process = "Загрузка данных для вебтекста, xform или UI плагина из БД с помощью sql скрипта")
public class HtmlPostgreSQLExecGateway extends HtmlDBGateway {

	@Override
	protected void prepareSQL() throws SQLException {

		final PostgreSQLExecGateway pgsql = new PostgreSQLExecGateway(this) {
			@Override
			protected String getParamsDeclaration() {
				switch (templateIndex()) {
				case GET_DATA_TEMPALTE_IND:
					return STD_PARAMS + ", OUT data xml, OUT settings xml";
				case SAVE_TEMPLATE_IND:
					return STD_PARAMS + ", IN data xml, OUT error_mes text";
				case SUBMISSION_TEMPLATE_IND:
					return "OUT error_code int4, IN inputdata xml, OUT outputdata xml, OUT error_mes text";
				case DOWNLOAD_FILE_TEMPLATE_IND:
					throw new NotImplementedYetException();
				case UPLOAD_FILE_TEMPLATE_IND:
					throw new NotImplementedYetException();
				default:
					throw new NotImplementedYetException();
				}
			}

		};
		pgsql.createTempFunction();

		super.prepareSQL();

	}

	private int templateIndex() {
		return getTemplateIndex();
	}

}
