package ru.curs.showcase.core.grid.export;

import java.sql.ResultSet;

/**
 * Результат запроса к источнику данных.
 * 
 * @author bogatov
 * 
 */
public class ExportData {
	private final ResultSet resultSet;

	public ExportData(final ResultSet oResultSet) {
		super();
		this.resultSet = oResultSet;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

}
