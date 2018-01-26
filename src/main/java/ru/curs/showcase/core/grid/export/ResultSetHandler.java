package ru.curs.showcase.core.grid.export;

import java.sql.ResultSet;

/**
 * Интерфейс обработки результирующего набора данных.
 * 
 * @author bogatov
 * 
 */
public interface ResultSetHandler {

	/**
	 * Выполнить обработку.
	 * 
	 * @param rs
	 *            - ResultSet
	 */
	void onProcess(ResultSet rs) throws Exception;
}
