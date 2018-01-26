package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.SerializableElement;
import ru.curs.showcase.app.api.grid.GridValueType;

/**
 * Столбец.
 */

public class GridServerColumnConfig implements SerializableElement {
	private static final long serialVersionUID = -1529797457839030699L;

	private String id = null;
	private GridValueType valueType = null;
	private String format = null;

	public GridServerColumnConfig(final String aId, final GridValueType aValueType,
			final String aFormat) {
		id = aId;
		valueType = aValueType;
		format = aFormat;
	}

	public GridValueType getValueType() {
		return valueType;
	}

	public String getId() {
		return id;
	}

	public String getFormat() {
		return format;
	}

}
