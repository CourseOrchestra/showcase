package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.grid.Sorting;

/**
 * Класс сортировки на столбце. Нужен только лишь для совместимости с передачей
 * сортировки в Jython и Челесту.
 */

public class SortColumn {

	private String id = null;
	private Sorting sorting;

	public SortColumn() {
	}

	public SortColumn(final String aId, final Sorting aSorting) {
		id = aId;
		sorting = aSorting;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public Sorting getSorting() {
		return sorting;
	}

	public void setSorting(final Sorting aSorting) {
		sorting = aSorting;
	}

}
