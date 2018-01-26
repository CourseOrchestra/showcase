package ru.curs.showcase.core.sp;

import java.sql.*;

import javax.sql.RowSet;

import org.joda.time.DateTime;
import org.joda.time.format.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.SQLUtils;
import ru.curs.showcase.util.xml.*;

/**
 * Утилиты пакета SP.
 * 
 * @author bogatov
 * 
 */
public final class SPUtils {

	private SPUtils() {

	}

	/**
	 * Создание xml данных для компонента grid.
	 * 
	 * @param rs
	 *            - sql ResultSet.
	 * @return - xml строка.
	 * @throws SQLException
	 */
	public static String createXmlDSForGrid(final ResultSet rs) throws SQLException {
		RowSet rowset = SQLUtils.cacheResultSet(rs);
		return createXmlDSForGrid(rowset);
	}

	/**
	 * Создание xml данных для компонента grid.
	 * 
	 * @param rowset
	 *            - sql RowSet.
	 * @return - xml строка.
	 * @throws SQLException
	 */
	public static String createXmlDSForGrid(final RowSet rowset) throws SQLException {
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData md = rowset.getMetaData();
		sb.append("<" + GeneralXMLHelper.XML_DATASET_TAG + ">");
		while (rowset.next()) {
			sb.append("<" + GeneralXMLHelper.RECORD_TAG + ">");
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (GeneralXMLHelper.PROPERTIES_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {

					String value = rowset.getString(md.getColumnLabel(i));
					if ((value == null) || value.isEmpty()) {
						value = "<properties></properties>";
					}
					sb.append(value);

				} else if (GeneralXMLHelper.ID_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {
					sb.append("<_x007e__x007e_" + GeneralXMLHelper.ID_TAG + ">"
							+ rowset.getString(md.getColumnLabel(i)) + "</_x007e__x007e_"
							+ GeneralXMLHelper.ID_TAG + ">");
				} else {
					String tagName = XMLUtils.escapeTagXml(md.getColumnLabel(i));
					String s = "<" + tagName;
					s =
						s + " " + GeneralXMLHelper.SQLTYPE_ATTR + "=\""
								+ String.valueOf(md.getColumnType(i)) + "\"";
					s = s + ">";
					String value;
					if (SQLUtils.isGeneralizedDateType(md.getColumnType(i))) {
						value = getStringValueOfDate(rowset, i);
					} else if (SQLUtils.isStringType(md.getColumnType(i))) {
						value = XMLUtils.escapeValueXml(rowset.getString(md.getColumnLabel(i)));
					} else {
						value = rowset.getString(md.getColumnLabel(i));
					}
					s = s + value + "</" + tagName + ">";
					sb.append(s);
				}
			}
			sb.append("</" + GeneralXMLHelper.RECORD_TAG + ">");
		}
		sb.append("</" + GeneralXMLHelper.XML_DATASET_TAG + ">");
		return sb.toString();
	}

	/**
	 * Создание xml данных для компонента chart.
	 * 
	 * @param rs
	 *            - sql ResultSet.
	 * @return - xml строка.
	 * @throws SQLException
	 */
	public static String createXmlDSForChart(final ResultSet rs) throws SQLException {
		RowSet rowset = SQLUtils.cacheResultSet(rs);
		return createXmlDSForChart(rowset);
	}

	/**
	 * Создание xml данных для компонента chart.
	 * 
	 * @param rowset
	 *            - sql RowSet.
	 * @return - xml строка.
	 * @throws SQLException
	 */
	public static String createXmlDSForChart(final RowSet rowset) throws SQLException {
		StringBuilder writer = new StringBuilder();

		ResultSetMetaData md = rowset.getMetaData();
		writer.append("<" + GeneralXMLHelper.XML_DATASET_TAG + ">");
		while (rowset.next()) {
			writer.append("<" + GeneralXMLHelper.RECORD_TAG + ">");
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (GeneralXMLHelper.PROPERTIES_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {
					writer.append(rowset.getString(md.getColumnLabel(i)));
				} else {
					String tagName = XMLUtils.escapeTagXml(md.getColumnLabel(i));
					writer.append("<" + tagName + ">" + rowset.getString(md.getColumnLabel(i))
							+ "</" + tagName + ">");
				}
			}
			writer.append("</" + GeneralXMLHelper.RECORD_TAG + ">");
		}
		writer.append("</" + GeneralXMLHelper.XML_DATASET_TAG + ">");

		return writer.toString();
	}

	private static String getStringValueOfDate(final RowSet rowset, final int colIndex)
			throws SQLException {
		ResultSetMetaData md = rowset.getMetaData();
		int sqltype = md.getColumnType(colIndex);
		String colName = md.getColumnLabel(colIndex);

		java.util.Date date = null;
		if (SQLUtils.isDateType(sqltype)) {
			date = rowset.getDate(colName);
		} else if (SQLUtils.isTimeType(sqltype)) {
			date = rowset.getTime(colName);
		} else if (SQLUtils.isDateTimeType(sqltype)) {
			date = rowset.getTimestamp(colName);
		}
		if (date == null) {
			return "";
		}

		DateTime dt = new DateTime(date);
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			String type = md.getColumnTypeName(colIndex);
			if (("date".equalsIgnoreCase(type)) || ("datetime2".equalsIgnoreCase(type))) {
				dt = dt.plusDays(2);
			}
		}
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		return fmt.print(dt);
	}

}
