package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Кеш для лирыгрида.
 * 
 */
public class LyraGridServerState implements SerializableElement {
	private static final long serialVersionUID = -4515880852717340699L;

	private String decimalSeparator = null;
	private String groupingSeparator = null;
	private String dateValuesFormat = null;

	private String[] orderBy = null;

	public String getDateValuesFormat() {
		return dateValuesFormat;
	}

	public void setDateValuesFormat(final String aDateValuesFormat) {
		dateValuesFormat = aDateValuesFormat;
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

	public String[] getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(final String[] aOrderBy) {
		orderBy = aOrderBy;
	}

}
