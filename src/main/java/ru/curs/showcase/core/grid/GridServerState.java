package ru.curs.showcase.core.grid;

import java.util.HashMap;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Динамические настройки грида, загружаемые из БД, которые требуются для
 * формирования грида на сервере.
 * 
 */
public class GridServerState implements SerializableElement {
	private static final long serialVersionUID = -1419798447839020679L;

	private HashMap<String, GridServerColumnConfig> columns = null;

	private Integer totalCount;
	private boolean forceLoadSettings = false;

	private String decimalSeparator = null;
	private String groupingSeparator = null;
	private String dateValuesFormat = null;

	public String getDateValuesFormat() {
		return dateValuesFormat;
	}

	public void setDateValuesFormat(final String aDateValuesFormat) {
		dateValuesFormat = aDateValuesFormat;
	}

	public HashMap<String, GridServerColumnConfig> getColumns() {
		return columns;
	}

	public void setColumns(final HashMap<String, GridServerColumnConfig> aColumns) {
		columns = aColumns;
	}

	public boolean isForceLoadSettings() {
		return forceLoadSettings;
	}

	public void setForceLoadSettings(final boolean aForceLoadSettings) {
		forceLoadSettings = aForceLoadSettings;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(final String aDecimalSeparator) {
		decimalSeparator = aDecimalSeparator;
	}

	public String getGroupingSeparator() {
		return groupingSeparator;
	}

	public void setGroupingSeparator(final String aGroupingSeparator) {
		groupingSeparator = aGroupingSeparator;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(final Integer aTotalCount) {
		totalCount = aTotalCount;
	}
}
