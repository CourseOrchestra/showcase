package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.ExcludeFromSerialization;

/**
 * Класс, содержащий детальный контекст лирыгрида.
 * 
 */
@XmlRootElement(name = "lyraGridContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class LyraGridContext extends GridContext {
	private static final long serialVersionUID = 8898993042175526645L;

	private int dgridOldPosition = 0;

	private String refreshId = null;

	private boolean sortingChanged = false;
	private boolean externalSortingOrFilteringChanged = false;

	@XmlTransient
	@ExcludeFromSerialization
	private String[] orderBy = null;

	/**
	 * Создает дефолтные настройки для грида - нужны для первоначальной
	 * отрисовки грида и для тестов.
	 */
	public static LyraGridContext createFirstLoadDefault() {
		LyraGridContext result = new LyraGridContext();
		result.setIsFirstLoad(true);
		return result;
	}

	public int getDgridOldPosition() {
		return dgridOldPosition;
	}

	public void setDgridOldPosition(final int aDgridOldPosition) {
		dgridOldPosition = aDgridOldPosition;
	}

	public String getRefreshId() {
		return refreshId;
	}

	public void setRefreshId(final String aRefreshId) {
		refreshId = aRefreshId;
	}

	public boolean isSortingChanged() {
		return sortingChanged;
	}

	public void setSortingChanged(final boolean aSortingChanged) {
		sortingChanged = aSortingChanged;
	}

	public boolean isExternalSortingOrFilteringChanged() {
		return externalSortingOrFilteringChanged;
	}

	public void setExternalSortingOrFilteringChanged(
			final boolean aExternalSortingOrFilteringChanged) {
		externalSortingOrFilteringChanged = aExternalSortingOrFilteringChanged;
	}

	public String[] getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(final String[] aOrderBy) {
		orderBy = aOrderBy;
	}

}
