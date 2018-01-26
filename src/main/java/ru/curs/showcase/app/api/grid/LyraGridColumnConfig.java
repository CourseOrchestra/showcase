package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

/**
 * Столбец.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LyraGridColumnConfig extends GridColumnConfig {
	private static final long serialVersionUID = -5109839708829566161L;

	private boolean sortingAvailable = false;

	public boolean isSortingAvailable() {
		return sortingAvailable;
	}

	public void setSortingAvailable(final boolean aSortingAvailable) {
		sortingAvailable = aSortingAvailable;
	}

}
