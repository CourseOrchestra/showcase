package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Информация о сортировке в гриде.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GridSorting implements SerializableElement {
	private static final long serialVersionUID = -5251475095922570541L;

	@XmlAttribute
	private String sortColId = null;
	@XmlAttribute
	private Sorting sortColDirection = Sorting.ASC;

	public GridSorting() {
		super();
	}

	public GridSorting(final String aSortColId, final Sorting aSortColDirection) {
		sortColId = aSortColId;
		sortColDirection = aSortColDirection;
	}

	@Override
	public String toString() {
		return "GridSorting [sortColId=" + sortColId + ", sortColDirection=" + sortColDirection
				+ "]";
	}

	public String getSortColId() {
		return sortColId;
	}

	public void setSortColId(final String aSortColId) {
		sortColId = aSortColId;
	}

	public Sorting getSortColDirection() {
		return sortColDirection;
	}

	public void setSortColDirection(final Sorting aSortColDirection) {
		sortColDirection = aSortColDirection;
	}

}
