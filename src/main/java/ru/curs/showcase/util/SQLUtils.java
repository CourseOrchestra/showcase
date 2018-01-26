package ru.curs.showcase.util;

import java.sql.*;
import java.util.Map;
import java.util.regex.*;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import com.sun.rowset.CachedRowSetImpl;

/**
 * Содержит вспомогательные функции для работы с SQL.
 * 
 * @author den
 * 
 */
public final class SQLUtils {

	/**
	 * Создает кэшированный RowSet в памяти. Сейчас используется default и
	 * deprecated реализация CachedRowSetImpl.
	 * 
	 * @param rs
	 *            - открытый ResultSet с данными.
	 * @return - CachedRowSet.
	 * @throws SQLException
	 */
	public static RowSet cacheResultSet(final ResultSet rs) throws SQLException {
		CachedRowSet sql = new CachedRowSetImpl();
		sql.populate(rs);
		return sql;
	}

	private SQLUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Определяет наличие столбца с определенным заголовком в ResultSet.
	 * 
	 * @param md
	 *            - метаданные ResultSet.
	 * @param caption
	 *            - заголовок столбца.
	 * @return - результат проверки.
	 * @throws SQLException
	 */
	public static boolean existsColumn(final ResultSetMetaData md, final String caption)
			throws SQLException {
		for (int i = 1; i <= md.getColumnCount(); i++) {
			if (caption.equalsIgnoreCase(md.getColumnLabel(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Определяет индекс столбца с определенным названием в ResultSet (в случае
	 * отсутствия возвращается -1).
	 * 
	 * @param md
	 *            - метаданные ResultSet.
	 * @param name
	 *            - название столбца.
	 * @return - индекс столбца.
	 * @throws SQLException
	 */
	public static int getColumnIndex(final ResultSetMetaData md, final String name)
			throws SQLException {
		int index = -1;
		for (int i = 1; i <= md.getColumnCount(); i++) {
			if (name.equalsIgnoreCase(md.getColumnName(i))) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Определяет, является ли тип SQL датой .
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isDateType(final int aSqlType) {
		return aSqlType == Types.DATE;
	}

	/**
	 * Определяет, является ли тип SQL временем.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isTimeType(final int aSqlType) {
		return aSqlType == Types.TIME;
	}

	/**
	 * Определяет, является ли тип SQL датой и временем.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isDateTimeType(final int aSqlType) {
		return aSqlType == Types.TIMESTAMP;
	}

	/**
	 * Определяет, является ли тип "обобщенной" SQL датой .
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isGeneralizedDateType(final int aSqlType) {
		return isDateType(aSqlType) || isTimeType(aSqlType) || isDateTimeType(aSqlType);
	}

	/**
	 * Определяет, является ли тип SQL целым числом.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isIntType(final int aSqlType) {
		return (aSqlType == Types.BIGINT) || (aSqlType == Types.INTEGER)
				|| (aSqlType == Types.SMALLINT) || (aSqlType == Types.TINYINT);
	}

	/**
	 * Определяет, является ли тип SQL дробным числом.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isFloatType(final int aSqlType) {
		return (aSqlType == Types.DECIMAL) || (aSqlType == Types.DOUBLE)
				|| (aSqlType == Types.FLOAT) || (aSqlType == Types.NUMERIC)
				|| (aSqlType == Types.REAL);
	}

	/**
	 * Определяет, является ли тип SQL строкой.
	 * 
	 * @param aSqlType
	 *            - тип SQL.
	 * @return - результат проверки.
	 */
	public static boolean isStringType(final int aSqlType) {
		return (aSqlType == Types.CHAR) || (aSqlType == Types.NCHAR)
				|| (aSqlType == Types.NVARCHAR) || (aSqlType == Types.VARCHAR)
				|| (aSqlType == Types.LONGNVARCHAR) || (aSqlType == Types.LONGVARCHAR);
	}

	public static String addParamsToSQLTemplate(final String template,
			final Map<Integer, Object> params) {
		String value = template;
		Pattern pattern = Pattern.compile("(\\?)");
		Matcher matcher = pattern.matcher(value);
		int i = 1;
		StringBuffer result = new StringBuffer("");

		while (matcher.find()) {
			Object paramValue = params.get(i++);
			if (paramValue instanceof Integer) {
				matcher.appendReplacement(result, ((Integer) paramValue).toString());
			} else if (paramValue == null) {
				matcher.appendReplacement(result, "null");
			} else {
				String tmpValue = (String) paramValue;
				tmpValue = tmpValue.replace("\\", "\\\\").replace("$", "\\$").replace("'", "''");
				matcher.appendReplacement(result, String.format("N'%s'", tmpValue));
			}
		}
		matcher.appendTail(result);

		pattern = Pattern.compile("call (\\w+)\\s\\(([\\s\\S]+)\\)");
		matcher = pattern.matcher(result.toString());
		if (matcher.find()) {
			value = matcher.group(1) + " " + matcher.group(2);
		}
		return value;
	}

}
