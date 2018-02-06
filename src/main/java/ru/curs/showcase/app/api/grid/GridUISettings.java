package ru.curs.showcase.app.api.grid;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * 
 * Настройки UI для грида. Как правило, задаются по умолчанию для всех гридов в
 * файле настроек приложения.
 * 
 * При изменении интерфейса требуется доработка функции assign!
 * 
 */
public class GridUISettings implements SerializableElement {

	private static final long serialVersionUID = 4462719170116564085L;

	public static final boolean DEF_SELECT_ONLY_RECORDS = false;
	public static final int DEF_PAGES_COUNT = 2;
	public static final int DEF_DOUBLE_CLICK_TIME = 300;
	/**
	 * Количество выводимых номеров страниц в блоке постраничной навигации.
	 */
	private int pagesButtonCount = DEF_PAGES_COUNT;
	/**
	 * Режим выделения только строк. При включенном режиме (true) выделяются
	 * только строки целиком (то есть при клике мышкой вместо выделения ячейки
	 * выделяется строка целиком).
	 * 
	 */
	private boolean selectOnlyRecords = DEF_SELECT_ONLY_RECORDS;

	/**
	 * Время, в течении которого повторный клик считается двойным кликом.
	 */
	private int doubleClickTime = DEF_DOUBLE_CLICK_TIME;
	/**
	 * Управление режимом работы относительно одинарного клика перед двойным
	 * кликом. Принимается во внимание только при включенном режиме двойного
	 * клика (doubleClickEnabled = true).
	 * 
	 * Если singleClickBeforeDoubleClick = false, то перед двойным кликом
	 * одинарного клика нет, и при этом каждый одинарный клик возникает через
	 * время doubleClickTime. Если singleClickBeforeDoubleClick = true, то
	 * одинарный клик
	 * 
	 */
	private boolean singleClickBeforeDoubleClick = false;

	/**
	 * Url картинки на кнопку загрузки файлов из грида.
	 * 
	 */
	private String urlImageFileDownload = null;

	public String getUrlImageFileDownload() {
		return urlImageFileDownload;
	}

	public void setUrlImageFileDownload(final String aUrlImageFileDownload) {
		urlImageFileDownload = aUrlImageFileDownload;
	}

	/**
	 * Видимость Pager'a.
	 */
	private boolean visiblePager = true;
	/**
	 * Видимость ToolBar'a.
	 */
	private boolean visibleToolBar = true;
	/**
	 * Видимость кнопки "Экспорт в Excel текущей страницы".
	 */
	private boolean visibleExportToExcelCurrentPage = true;
	/**
	 * Видимость кнопки "Экспорт в Excel всей таблицы".
	 */
	private boolean visibleExportToExcelAll = true;
	/**
	 * Видимость кнопки "Копировать в буфер обмена".
	 */
	private boolean visibleCopyToClipboard = true;
	/**
	 * Видимость кнопки "Фильтр".
	 */
	private boolean visibleFilter = false;
	/**
	 * Видимость кнопки "Сохранить изменения".
	 */
	private boolean visibleSave = true;
	/**
	 * Видимость поля "Сохранить изменения".
	 */
	private boolean visibleFieldSave = false;
	/**
	 * Видимость кнопки "Отменить изменения".
	 */
	private boolean visibleRevert = true;
	/**
	 * Видимость заголовка стобцов.
	 */
	private boolean visibleColumnsHeader = true;

	/**
	 * Возможность выделения текста в ячейке.
	 */
	private boolean allowTextSelection = false;

	private ColumnValueDisplayMode displayMode = null;

	/**
	 * Видимость Pager'a.
	 */
	public boolean isVisiblePager() {
		return visiblePager;
	}

	/**
	 * Видимость Pager'a.
	 */
	public void setVisiblePager(final boolean aVisiblePager) {
		visiblePager = aVisiblePager;
	}

	/**
	 * Видимость ToolBar'a.
	 */
	public boolean isVisibleToolBar() {
		return visibleToolBar;
	}

	/**
	 * Видимость ToolBar'a.
	 */
	public void setVisibleToolBar(final boolean aVisibleToolBar) {
		visibleToolBar = aVisibleToolBar;
	}

	/**
	 * Видимость кнопки "Экспорт в Excel текущей страницы".
	 */
	public boolean isVisibleExportToExcelCurrentPage() {
		return visibleExportToExcelCurrentPage;
	}

	/**
	 * Видимость кнопки "Экспорт в Excel текущей страницы".
	 */
	public void
			setVisibleExportToExcelCurrentPage(final boolean aVisibleExportToExcelCurrentPage) {
		visibleExportToExcelCurrentPage = aVisibleExportToExcelCurrentPage;
	}

	/**
	 * Видимость кнопки "Экспорт в Excel всей таблицы".
	 */
	public boolean isVisibleExportToExcelAll() {
		return visibleExportToExcelAll;
	}

	/**
	 * Видимость кнопки "Экспорт в Excel всей таблицы".
	 */
	public void setVisibleExportToExcelAll(final boolean aVisibleExportToExcelAll) {
		visibleExportToExcelAll = aVisibleExportToExcelAll;
	}

	/**
	 * Видимость кнопки "Копировать в буфер обмена".
	 */
	public boolean isVisibleCopyToClipboard() {
		return visibleCopyToClipboard;
	}

	/**
	 * Видимость кнопки "Копировать в буфер обмена".
	 */
	public void setVisibleCopyToClipboard(final boolean aVisibleCopyToClipboard) {
		visibleCopyToClipboard = aVisibleCopyToClipboard;
	}

	/**
	 * Видимость кнопки "Фильтр".
	 */
	public boolean isVisibleFilter() {
		return visibleFilter;
	}

	/**
	 * Видимость кнопки "Фильтр".
	 */
	public void setVisibleFilter(final boolean aVisibleFilter) {
		visibleFilter = aVisibleFilter;
	}

	/**
	 * Видимость кнопки "Сохранить изменения".
	 */
	public boolean isVisibleSave() {
		return visibleSave;
	}

	/**
	 * Видимость кнопки "Сохранить изменения".
	 */
	public void setVisibleSave(final boolean aVisibleSave) {
		visibleSave = aVisibleSave;
	}

	public boolean isVisibleFieldSave() {
		return visibleFieldSave;
	}

	public void setVisibleFieldSave(final boolean aVisibleFieldSave) {
		visibleFieldSave = aVisibleFieldSave;
	}

	/**
	 * Видимость кнопки "Отменить изменения".
	 */
	public boolean isVisibleRevert() {
		return visibleRevert;
	}

	/**
	 * Видимость кнопки "Отменить изменения".
	 */
	public void setVisibleRevert(final boolean aVisibleRevert) {
		visibleRevert = aVisibleRevert;
	}

	/**
	 * Видимость заголовка стобцов.
	 */
	public boolean isVisibleColumnsHeader() {
		return visibleColumnsHeader;
	}

	/**
	 * Видимость заголовка стобцов.
	 */
	public void setVisibleColumnsHeader(final boolean aVisibleColumnsHeader) {
		visibleColumnsHeader = aVisibleColumnsHeader;
	}

	/**
	 * Возможность выделения текста в ячейке.
	 */
	public boolean isAllowTextSelection() {
		return allowTextSelection;
	}

	/**
	 * Возможность выделения текста в ячейке.
	 */
	public void setAllowTextSelection(final boolean aAllowTextSelection) {
		allowTextSelection = aAllowTextSelection;
	}

	/**
	 * Количество номеров страниц в навигации.
	 * 
	 * @return Количество номеров страниц в навигации.
	 */
	public int getPagesButtonCount() {
		return pagesButtonCount;
	}

	/**
	 * Количество номеров страниц в навигации.
	 * 
	 * @param pagesButtonCount
	 *            Количество номеров страниц в навигации.
	 */
	public void setPagesButtonCount(final int aPagesButtonCount) {
		pagesButtonCount = aPagesButtonCount;
	}

	/**
	 * Выделять только записи.
	 * 
	 * @return Выделять только записи.
	 */
	public boolean isSelectOnlyRecords() {
		return selectOnlyRecords;
	}

	/**
	 * Выделять только записи.
	 * 
	 * @param selectOnlyRecords
	 *            Выделять только записи.
	 */
	public void setSelectOnlyRecords(final boolean aSelectOnlyRecords) {
		selectOnlyRecords = aSelectOnlyRecords;
	}

	/**
	 * Время, в течении которого повторный клик считается двойным кликом.
	 * 
	 * @return Время, в течении которого повторный клик считается двойным
	 *         кликом.
	 */
	public int getDoubleClickTime() {
		return doubleClickTime;
	}

	/**
	 * Время, в течении которого повторный клик считается двойным кликом.
	 * 
	 * @param doubleClickTime
	 *            Время, в течении которого повторный клик считается двойным
	 *            кликом.
	 */
	public void setDoubleClickTime(final int aDoubleClickTime) {
		doubleClickTime = aDoubleClickTime;
	}

	/**
	 * Управление режимом работы относительно одинарного клика перед двойным
	 * кликом.
	 * 
	 * @return Управление режимом работы относительно одинарного клика перед
	 *         двойным кликом.
	 */
	public boolean isSingleClickBeforeDoubleClick() {
		return singleClickBeforeDoubleClick;
	}

	/**
	 * Управление режимом работы относительно одинарного клика перед двойным
	 * кликом.
	 * 
	 * @param singleClickBeforeDoubleClick
	 *            Управление режимом работы относительно одинарного клика перед
	 *            двойным кликом.
	 */
	public void setSingleClickBeforeDoubleClick(final boolean aSingleClickBeforeDoubleClick) {
		singleClickBeforeDoubleClick = aSingleClickBeforeDoubleClick;
	}

	/**
	 * Ширина грида.
	 */
	private String gridWidth = null;

	/**
	 * Высота грида.
	 */
	private Integer gridHeight = null;

	private String toolbarClassName = null;
	private String toolbarStyle = null;
	private boolean toolbarCreateImmediately = false;

	/**
	 * Горизонтальное выравнивание заголовков столбцов.
	 */
	private HorizontalAlignment haColumnHeader = null;

	public HorizontalAlignment getHaColumnHeader() {
		return haColumnHeader;
	}

	public void setHaColumnHeader(final HorizontalAlignment aHaColumnHeader) {
		haColumnHeader = aHaColumnHeader;
	}

	public String getGridWidth() {
		return gridWidth;
	}

	public void setGridWidth(final String aGridWidth) {
		gridWidth = aGridWidth;
	}

	public Integer getGridHeight() {
		return gridHeight;
	}

	public void setGridHeight(final Integer aGridHeight) {
		gridHeight = aGridHeight;
	}

	public String getToolbarClassName() {
		return toolbarClassName;
	}

	public void setToolbarClassName(final String aToolbarClassName) {
		toolbarClassName = aToolbarClassName;
	}

	public String getToolbarStyle() {
		return toolbarStyle;
	}

	public void setToolbarStyle(final String aToolbarStyle) {
		toolbarStyle = aToolbarStyle;
	}

	public boolean isToolbarCreateImmediately() {
		return toolbarCreateImmediately;
	}

	public void setToolbarCreateImmediately(final boolean aToolbarCreateImmediately) {
		toolbarCreateImmediately = aToolbarCreateImmediately;
	}

	public ColumnValueDisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(final ColumnValueDisplayMode aDisplayMode) {
		displayMode = aDisplayMode;
	}

	public void assign(final GridUISettings source) {
		doubleClickTime = source.doubleClickTime;
		singleClickBeforeDoubleClick = source.singleClickBeforeDoubleClick;
		pagesButtonCount = source.pagesButtonCount;
		selectOnlyRecords = source.selectOnlyRecords;
		visiblePager = source.visiblePager;
		visibleExportToExcelCurrentPage = source.visibleExportToExcelCurrentPage;
		visibleExportToExcelAll = source.visibleExportToExcelAll;
		visibleColumnsHeader = source.visibleColumnsHeader;
		visibleCopyToClipboard = source.visibleCopyToClipboard;
		visibleFilter = source.visibleFilter;
		visibleSave = source.visibleSave;
		visibleFieldSave = source.visibleFieldSave;
		visibleRevert = source.visibleRevert;
		allowTextSelection = source.allowTextSelection;
		urlImageFileDownload = source.urlImageFileDownload;
		gridWidth = source.gridWidth;
		gridHeight = source.gridHeight;
		haColumnHeader = source.haColumnHeader;
		displayMode = source.displayMode;
		toolbarClassName = source.toolbarClassName;
		toolbarStyle = source.toolbarStyle;

	}

}
