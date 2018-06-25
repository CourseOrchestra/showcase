package ru.curs.showcase.core.grid;

import java.util.*;

import ru.curs.celesta.score.*;
import ru.curs.lyra.*;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.*;
import ru.curs.showcase.runtime.ProfileReader;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Фабрика для создания метаданных лирагридов.
 * 
 */
public class LyraVueGridMetaFactory {
	private static final String DEF_COL_HOR_ALIGN = "def.column.hor.align";
	private static final String DEF_COL_WIDTH = "def.column.width";
	private static final String DEF_VAL_FONT_COLOR = "def.value.font.color";
	private static final String DEF_VAL_BG_COLOR = "def.value.bg.color";
	private static final String DEF_VAL_FONT_SIZE = "def.value.font.size";
	private static final String DEF_VAL_FONT_BOLD = "def.value.font.bold";
	private static final String DEF_VAL_FONT_IT = "def.value.font.italic";
	private static final String DEF_VAL_FONT_UL = "def.value.font.underline";
	private static final String DEF_VAL_FONT_ST = "def.value.font.strikethrough";
	private static final String DEF_STR_COL_HOR_ALIGN = "def.str.column.hor.align";
	private static final String DEF_NUM_COL_HOR_ALIGN = "def.num.column.hor.align";
	private static final String DEF_DATE_COL_HOR_ALIGN = "def.date.column.hor.align";
	private static final String DEF_IMAGE_COL_HOR_ALIGN = "def.image.column.hor.align";
	private static final String DEF_LINK_COL_HOR_ALIGN = "def.link.column.hor.align";

	private static final String GRID_DEFAULT_PROFILE = "default.properties";
	private static final String GRID_WIDTH_DEF_VALUE = "95%";
	private static final int GRID_HEIGHT_DEF_VALUE = 400;
	private static final int COLUMN_WIDTH_DEF_VALUE = 100;

	private static final String STRING_SELECTED_RECORD_IDS_SEPARATOR = "D13&82#9g7";

	private final LyraGridContext context;
	private final DataPanelElementInfo elInfo;
	private BasicGridForm basicGridForm = null;

	private LyraGridMetadata gridMetadata;

	private String profile = null;
	private ProfileReader gridProps = null;

	private org.json.simple.JSONObject metadata;

	public LyraVueGridMetaFactory(final LyraGridContext aContext,
			final DataPanelElementInfo aElInfo) {
		context = aContext;
		elInfo = aElInfo;
	}

	public GridMeta buildMetadata() {
		initResult();

		setupDynamicSettings();
		setupStaticSettings();

		setupColumns();

		postProcessingResult();

		GridMeta md = new GridMeta();
		md.setMeta(metadata.toJSONString());

		return md;
	}

	public List<GridColumnConfig> buildColumnsForExportToExcel() {
		initResult();

		setupDynamicSettings();
		setupStaticSettings();

		setupColumns();

		return gridMetadata.getColumns();
	}

	private void initResult() {

		gridMetadata = new LyraGridMetadata(elInfo);

		LyraGridGateway lgateway = new LyraGridGateway();
		basicGridForm = lgateway.getLyraFormInstance(context, elInfo);

		final int maxExactScrollValue = 120;
		basicGridForm.setMaxExactScrollValue(maxExactScrollValue);
		if (basicGridForm.getChangeNotifier() == null) {
			LyraGridScrollBack scrollBack = new LyraGridScrollBack();
			scrollBack.setBasicGridForm(basicGridForm);
			basicGridForm.setChangeNotifier(scrollBack);
			gridMetadata.setNeedCreateWebSocket(true);
		}

	}

	private void setupDynamicSettings() {

		if (basicGridForm.getFormProperties().getGridwidth() == null) {
			gridMetadata.getUISettings().setGridWidth(GRID_WIDTH_DEF_VALUE);
		} else {
			gridMetadata.getUISettings()
					.setGridWidth(basicGridForm.getFormProperties().getGridwidth());
		}

		if (basicGridForm.getFormProperties().getGridheight() == null) {
			gridMetadata.getUISettings().setGridHeight(GRID_HEIGHT_DEF_VALUE);
		} else {
			gridMetadata.getUISettings().setGridHeight(
					TextUtils.getIntSizeValue(basicGridForm.getFormProperties().getGridheight()));
		}

		if (basicGridForm.getFormProperties().getHeader() != null) {
			gridMetadata.setHeader(basicGridForm.getFormProperties().getHeader());
		}
		if (basicGridForm.getFormProperties().getFooter() != null) {
			gridMetadata.setFooter(basicGridForm.getFormProperties().getFooter());
		}

		gridMetadata.getLiveInfo().setOffset(0);
		gridMetadata.getLiveInfo().setLimit(basicGridForm.getGridHeight());
		gridMetadata.getLiveInfo().setTotalCount(basicGridForm.getApproxTotalCount());

	}

	private void setupStaticSettings() {

		if (basicGridForm.getFormProperties().getProfile() == null) {
			profile = GRID_DEFAULT_PROFILE;
		} else {
			profile = basicGridForm.getFormProperties().getProfile();
		}

		gridProps = new ProfileReader(profile, SettingsFileType.GRID_PROPERTIES);
		try {
			gridProps.init();
		} catch (Exception e) {
			if (e.getMessage() == null) {
				throw new ValidateException(new UserMessage(
						String.format("Файл свойств грида \"%s\" не существует.", profile),
						MessageType.ERROR, "Ошибка"));
			} else {
				throw e;
			}
		}

		ProfileBasedSettingsApplyStrategy strategy =
			new DefaultGridSettingsApplyStrategy(gridProps, gridMetadata.getUISettings());
		strategy.apply();

		setupViewSettings();
	}

	/**
	 * Считывает из файла настроек и устанавливает стандартные свойства вида
	 * информации в гриде.
	 * 
	 */
	private void setupViewSettings() {
		String value;
		boolean boolValue;
		value = gridProps.getStringValue(DEF_VAL_FONT_COLOR);
		if (value != null) {
			gridMetadata.setTextColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_BG_COLOR);
		if (value != null) {
			gridMetadata.setBackgroundColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_FONT_SIZE);
		if (value != null) {
			gridMetadata.setFontSize(value);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_BOLD);
		if (boolValue) {
			gridMetadata.addFontModifier(FontModifier.BOLD);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_IT);
		if (boolValue) {
			gridMetadata.addFontModifier(FontModifier.ITALIC);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_UL);
		if (boolValue) {
			gridMetadata.addFontModifier(FontModifier.UNDERLINE);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_ST);
		if (boolValue) {
			gridMetadata.addFontModifier(FontModifier.STRIKETHROUGH);
		}
	}

	// CHECKSTYLE:OFF
	private void setupColumns() {

		List<String> lyraGridAvailableSorting = new ArrayList<String>();
		if (basicGridForm.meta() instanceof Table) {
			for (Index index : ((Table) basicGridForm.meta()).getIndices()) {
				if (index.getColumns().size() == 1) {
					lyraGridAvailableSorting
							.add((String) index.getColumns().keySet().toArray()[0]);
				}
			}
		}

		Map<String, LyraFormField> lyraFields = basicGridForm.getFieldsMeta();

		for (LyraFormField field : lyraFields.values()) {

			if ("_properties_".equalsIgnoreCase(field.getName())) {
				continue;
			}

			LyraGridColumnConfig column = new LyraGridColumnConfig();

			column.setId(field.getName());
			column.setCaption(field.getCaption());

			column.setVisible(field.isVisible());
			column.setReadonly(!field.isEditable());

			column.setValueType(GridUtils.getGridValueTypeByLyraFieldType(field.getType(),
					field.getSubtype()));
			column.setLinkId(field.getLinkId());

			String val;

			int colWidthDefValue;
			val = gridProps.getStringValue(DEF_COL_WIDTH);
			if (val != null) {
				colWidthDefValue = TextUtils.getIntSizeValue(val);
			} else {
				colWidthDefValue = COLUMN_WIDTH_DEF_VALUE;
			}

			if (column.getHorizontalAlignment() == null) {
				if (column.getValueType().isString()) {
					val = gridProps.getStringValue(DEF_STR_COL_HOR_ALIGN);
				} else if (column.getValueType().isNumber()) {
					val = gridProps.getStringValue(DEF_NUM_COL_HOR_ALIGN);
				} else if (column.getValueType().isDate()) {
					val = gridProps.getStringValue(DEF_DATE_COL_HOR_ALIGN);
				} else if (column.getValueType() == GridValueType.IMAGE) {
					val = gridProps.getStringValue(DEF_IMAGE_COL_HOR_ALIGN);
				} else if (column.getValueType() == GridValueType.LINK) {
					val = gridProps.getStringValue(DEF_LINK_COL_HOR_ALIGN);
				} else {
					val = gridProps.getStringValue(DEF_COL_HOR_ALIGN);
				}
				if (val != null) {
					column.setHorizontalAlignment(HorizontalAlignment.valueOf(val));
				}
			}

			if (field.getWidth() < 0) {
				column.setWidth(colWidthDefValue);
			} else {
				column.setWidth(field.getWidth());
			}

			if (lyraGridAvailableSorting.contains(column.getId())) {
				column.setSortingAvailable(true);
			}

			column.setFormat(null);
			column.setParentId(null);
			column.setEditor(null);

			gridMetadata.getColumns().add(column);

		}
	}
	// CHECKSTYLE:ON

	// CHECKSTYLE:OFF
	@SuppressWarnings("unchecked")
	private void postProcessingResult() {
		metadata = new org.json.simple.JSONObject();

		org.json.simple.JSONObject common = new org.json.simple.JSONObject();
		common.put("gridWidth", gridMetadata.getUISettings().getGridWidth());
		common.put("gridHeight", gridMetadata.getUISettings().getGridHeight());

		common.put("limit", String.valueOf(gridMetadata.getLiveInfo().getLimit()));
		common.put("totalCount", String.valueOf(gridMetadata.getLiveInfo().getTotalCount()));

		String selectionModel = "CELL";
		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			selectionModel = "RECORDS";
		}
		common.put("selectionModel", selectionModel);

		if (gridMetadata.getUISettings().isVisibleColumnsHeader()) {
			common.put("isVisibleColumnsHeader", "true");
		}

		if (gridMetadata.getUISettings().isAllowTextSelection()) {
			common.put("isAllowTextSelection", "true");
		}

		common.put("stringSelectedRecordIdsSeparator", STRING_SELECTED_RECORD_IDS_SEPARATOR);

		if (gridMetadata.getUISettings().getHaColumnHeader() != null) {
			common.put("haColumnHeader",
					gridMetadata.getUISettings().getHaColumnHeader().toString().toLowerCase());
		}

		if (gridMetadata.isNeedCreateWebSocket()) {
			common.put("isNeedCreateWebSocket", "true");
		}

		if (basicGridForm.meta() instanceof Table) {
			Object[] arr = ((Table) basicGridForm.meta()).getPrimaryKey().keySet().toArray();

			String s = "";
			for (int i = 0; i < arr.length; i++) {
				if (i > 0) {
					s = s + ",";
				}
				s = s + arr[i];
			}

			common.put("primaryKey", s);
		}

		String summaryRow = basicGridForm.getSummaryRow();
		if (summaryRow != null) {
			common.put("summaryRow", summaryRow);
		}

		metadata.put("common", common);

		int count = 0;
		org.json.simple.JSONObject columns = new org.json.simple.JSONObject();
		for (final GridColumnConfig egcc : gridMetadata.getColumns()) {

			org.json.simple.JSONObject column = new org.json.simple.JSONObject();
			column.put("id", egcc.getId());
			column.put("caption", egcc.getCaption());

			column.put("visible", egcc.isVisible());

			String value = "STRING";
			if (egcc.getValueType() != null) {
				value = egcc.getValueType().toString();
			}
			column.put("valueType", value);

			if (egcc.getLinkId() != null) {
				value = egcc.getLinkId().toString();
				column.put("linkId", value);
			}

			column.put("style", getCommonColumnStyle() + getColumnStyle(egcc));

			column.put("urlImageFileDownload",
					gridMetadata.getUISettings().getUrlImageFileDownload());

			if (((LyraGridColumnConfig) egcc).isSortingAvailable()) {
				column.put("sortingAvailable", String.valueOf("true"));
			}

			columns.put(++count, column);

		}
		metadata.put("columns", columns);

	}
	// CHECKSTYLE:ON

	private String getCommonColumnStyle() {
		String style = "";

		if (gridMetadata.getTextColor() != null) {
			style = style + "color:" + gridMetadata.getTextColor() + ";";
		}

		if (gridMetadata.getBackgroundColor() != null) {
			style = style + "background-color:" + gridMetadata.getBackgroundColor() + ";";
		}

		if (gridMetadata.getFontSize() != null) {
			style = style + "font-size:" + gridMetadata.getFontSize() + ";";
		}

		if ((gridMetadata.getFontSize() != null) && (gridMetadata.getFontModifiers() != null)) {
			for (FontModifier fm : gridMetadata.getFontModifiers()) {
				switch (fm) {
				case ITALIC:
					style = style + "font-style:italic;";
					continue;
				case BOLD:
					style = style + "font-weight:bold;";
					continue;
				case STRIKETHROUGH:
					style = style + "text-decoration:line-through;";
					continue;
				case UNDERLINE:
					style = style + "text-decoration:underline;";
					continue;
				default:
					continue;
				}
			}
		}

		if (gridMetadata.getUISettings().getDisplayMode() != ColumnValueDisplayMode.SINGLELINE) {
			style = style + "white-space:normal;";
		} else {
			style = style + "white-space:nowrap;";
		}

		return style;
	}

	private String getColumnStyle(final GridColumnConfig egcc) {
		String style = "";

		style = style + "width:" + egcc.getWidth() + "px;";

		if (egcc.getHorizontalAlignment() != null) {
			style = style + "text-align:" + egcc.getHorizontalAlignment().toString().toLowerCase()
					+ ";";
		}

		return style;
	}

}