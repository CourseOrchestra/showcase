package ru.curs.fastxl;

import static java.sql.Types.*;

import java.sql.*;

/**
 * Набор данных, построенный на резалтсете.
 * 
 */
public class JDBCRecordSet implements GridRecordSet {

	private final ResultSet rs;
	private final ResultSetMetaData md;

	public JDBCRecordSet(final ResultSet resultSet) throws SQLException {
		this.rs = resultSet;
		md = resultSet.getMetaData();
	}

	@Override
	public boolean next() throws EFastXLRuntime {
		try {
			return rs.next();
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
	}

	@Override
	public boolean isInteger(final int i) throws EFastXLRuntime {
		int ct;
		try {
			ct = md.getColumnType(i);
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
		return ct == TINYINT || ct == SMALLINT || ct == INTEGER;
	}

	@Override
	public boolean isFloat(final int i) throws EFastXLRuntime {
		int ct;
		try {
			ct = md.getColumnType(i);
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
		return ct == FLOAT || ct == REAL || ct == DOUBLE || ct == NUMERIC || ct == DECIMAL;
	}

	@Override
	public String getColumnName(final int i) throws EFastXLRuntime {
		try {
			return md.getColumnName(i);
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
	}

	@Override
	public int getColumnCount() throws EFastXLRuntime {
		try {
			return md.getColumnCount();
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
	}

	@Override
	public double getDouble(final int i) throws EFastXLRuntime {
		try {
			return rs.getDouble(i);
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
	}

	@Override
	public int getInt(final int i) throws EFastXLRuntime {
		try {
			return rs.getInt(i);
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
	}

	@Override
	public String getString(final int i) throws EFastXLRuntime {
		try {
			return rs.getString(i);
		} catch (SQLException e) {
			throw new EFastXLRuntime(e);
		}
	}

}
