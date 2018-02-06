package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.ProfileBasedSettingsApplyStrategy;
import ru.curs.showcase.runtime.ProfileReader;

/**
 * Стратегия применения настроек по умолчанию.
 * 
 */
public final class DefaultGridSettingsApplyStrategy extends ProfileBasedSettingsApplyStrategy {
	private static final String DEF_VISIBLE_PAGES_COUNT = "def.visible.pages.count";
	private static final String DEF_SELECT_WHOLE_RECORD = "def.select.whole.record";
	private static final String DEF_VISIBLE_PAGER = "def.visible.pager";
	private static final String DEF_VISIBLE_TOOLBAR = "def.visible.toolbar";
	private static final String DEF_VISIBLE_EXPORTTOEXCEL_CURRENTPAGE =
		"def.visible.exporttoexcel.currentpage";
	private static final String DEF_VISIBLE_EXPORTTOEXCEL_ALL = "def.visible.exporttoexcel.all";
	private static final String DEF_VISIBLE_COPYTOCLIPBOARD = "def.visible.copytoclipboard";
	private static final String DEF_VISIBLE_FILTER = "def.visible.filter";
	private static final String DEF_VISIBLE_SAVE = "def.visible.save";
	private static final String DEF_VISIBLE_FIELD_SAVE = "def.visible.field.save";
	private static final String DEF_VISIBLE_REVERT = "def.visible.revert";
	private static final String DEF_VISIBLE_COLUMNS_HEADER = "def.visible.columns.header";
	private static final String SINGLE_CLICK_BEFORE_DOUBLE = "single.click.before.double";
	private static final String DEF_SELECT_ALLOW_TEXT_SELECTION =
		"def.select.allow.text.selection";
	private static final String DEF_COLUMN_HEADER_HOR_ALIGN = "def.columnheader.hor.align";
	private static final String DEF_COL_VALUE_DISPLAY_MODE = "def.column.value.display.mode";
	private static final String URL_IMAGE_FILE_DOWNLOAD = "resources/internal/fileDownload.PNG";
	private static final String DEF_TOOLBAR_CLASSNAME = "def.toolbar.classname";
	private static final String DEF_TOOLBAR_STYLE = "def.toolbar.style";
	private static final String DEF_TOOLBAR_CREATEIMMEDIATELY = "def.toolbar.createimmediately";

	/**
	 * Настройки грида.
	 */
	private final GridUISettings settings;

	public DefaultGridSettingsApplyStrategy(final ProfileReader aGridPropsReader,
			final GridUISettings aUiSettings) {
		super(aGridPropsReader);
		settings = aUiSettings;
	}

	@Override
	protected void applyByDefault() {
		settings.setUrlImageFileDownload(URL_IMAGE_FILE_DOWNLOAD);
	}

	@Override
	protected void applyFromProfile() {
		Integer intValue = reader().getIntValue(DEF_VISIBLE_PAGES_COUNT);
		if (intValue != null) {
			settings.setPagesButtonCount(intValue);
		}

		Boolean boolValue = reader().getBoolValue(DEF_SELECT_WHOLE_RECORD);
		if (boolValue != null) {
			settings.setSelectOnlyRecords(boolValue);
		}
		boolValue = reader().getBoolValue(SINGLE_CLICK_BEFORE_DOUBLE);
		if (boolValue != null) {
			settings.setSingleClickBeforeDoubleClick(boolValue);
		}

		String stringValue = reader().getStringValue(DEF_COLUMN_HEADER_HOR_ALIGN);
		if (stringValue != null) {
			settings.setHaColumnHeader(HorizontalAlignment.valueOf(stringValue));
		}

		stringValue = reader().getStringValue(DEF_COL_VALUE_DISPLAY_MODE);
		if (stringValue != null) {
			settings.setDisplayMode(ColumnValueDisplayMode.valueOf(stringValue));
		}

		stringValue = reader().getStringValue(DEF_TOOLBAR_CLASSNAME);
		if (stringValue != null) {
			settings.setToolbarClassName(stringValue);
		}

		stringValue = reader().getStringValue(DEF_TOOLBAR_STYLE);
		if (stringValue != null) {
			settings.setToolbarStyle(stringValue);
		}

		boolValue = reader().getBoolValue(DEF_TOOLBAR_CREATEIMMEDIATELY);
		if (boolValue != null) {
			settings.setToolbarCreateImmediately(boolValue);
		}

		applyVisibilitySettings();
	}

	private void applyVisibilitySettings() {
		Boolean boolValue = reader().getBoolValue(DEF_VISIBLE_PAGER);
		if (boolValue != null) {
			settings.setVisiblePager(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_TOOLBAR);
		if (boolValue != null) {
			settings.setVisibleToolBar(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_EXPORTTOEXCEL_CURRENTPAGE);
		if (boolValue != null) {
			settings.setVisibleExportToExcelCurrentPage(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_EXPORTTOEXCEL_ALL);
		if (boolValue != null) {
			settings.setVisibleExportToExcelAll(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_COPYTOCLIPBOARD);
		if (boolValue != null) {
			settings.setVisibleCopyToClipboard(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_FILTER);
		if (boolValue != null) {
			settings.setVisibleFilter(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_SAVE);
		if (boolValue != null) {
			settings.setVisibleSave(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_FIELD_SAVE);
		if (boolValue != null) {
			settings.setVisibleFieldSave(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_REVERT);
		if (boolValue != null) {
			settings.setVisibleRevert(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_VISIBLE_COLUMNS_HEADER);
		if (boolValue != null) {
			settings.setVisibleColumnsHeader(boolValue);
		}
		boolValue = reader().getBoolValue(DEF_SELECT_ALLOW_TEXT_SELECTION);
		if (boolValue != null) {
			settings.setAllowTextSelection(boolValue);
		}
	}

}
