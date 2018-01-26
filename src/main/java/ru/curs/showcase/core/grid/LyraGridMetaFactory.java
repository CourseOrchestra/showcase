package ru.curs.showcase.core.grid;

import java.util.*;

import org.xml.sax.helpers.DefaultHandler;

import ru.curs.celesta.CelestaException;
import ru.curs.celesta.score.*;
import ru.curs.lyra.*;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.event.ActionFactory;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.SimpleSAX;

/**
 * Фабрика для создания метаданных лирагридов.
 * 
 */
public class LyraGridMetaFactory {
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

	private static final String DEFAULT_ACTION_XML_ERROR = "настройки грида";
	private static final String CHECK_DEFAULT_ACTION_ERROR =
		"Некорректное описание действия в элементе инф. панели: ";

	private static final String GRID_DEFAULT_PROFILE = "default.properties";
	private static final String GRID_WIDTH_DEF_VALUE = "95%";
	private static final int GRID_HEIGHT_DEF_VALUE = 400;
	private static final int COLUMN_WIDTH_DEF_VALUE = 100;

	private final LyraGridContext context;
	private final DataPanelElementInfo elInfo;
	private BasicGridForm basicGridForm = null;

	private LyraGridMetadata result;

	private String profile = null;
	private ProfileReader gridProps = null;

	public LyraGridMetaFactory(final LyraGridContext aContext,
			final DataPanelElementInfo aElInfo) {
		context = aContext;
		elInfo = aElInfo;
	}

	/**
	 * Построение метаданных лирагрида.
	 * 
	 * @return - GridMetadata.
	 * @throws CelestaException
	 */
	public LyraGridMetadata buildMetadata() throws CelestaException {
		initResult();

		setupDynamicSettings();
		setupDefaultAction();
		setupStaticSettings();

		setupColumns();

		setupPluginSettings();

		setupUnused();

		return result;
	}

	private void initResult() {
		result = new LyraGridMetadata(elInfo);

		LyraGridServerState state = (LyraGridServerState) AppInfoSingleton.getAppInfo()
				.getLyraGridCacheState(SessionUtils.getCurrentSessionId(), elInfo, context);
		if (state != null) {
			context.setOrderBy(state.getOrderBy());
		}

		LyraGridGateway lgateway = new LyraGridGateway();
		basicGridForm = lgateway.getLyraFormInstance(context, elInfo);

		final int maxExactScrollValue = 120;
		basicGridForm.setMaxExactScrollValue(maxExactScrollValue);
		if (basicGridForm.getChangeNotifier() == null) {
			LyraGridScrollBack scrollBack = new LyraGridScrollBack();
			scrollBack.setBasicGridForm(basicGridForm);
			basicGridForm.setChangeNotifier(scrollBack);
			result.setNeedCreateWebSocket(true);
		}

	}

	private void setupDynamicSettings() throws CelestaException {

		if (basicGridForm.getFormProperties().getGridwidth() == null) {
			result.getUISettings().setGridWidth(GRID_WIDTH_DEF_VALUE);
		} else {
			result.getUISettings().setGridWidth(basicGridForm.getFormProperties().getGridwidth());
		}

		if (basicGridForm.getFormProperties().getGridheight() == null) {
			result.getUISettings().setGridHeight(GRID_HEIGHT_DEF_VALUE);
		} else {
			result.getUISettings().setGridHeight(
					TextUtils.getIntSizeValue(basicGridForm.getFormProperties().getGridheight()));
		}

		if (basicGridForm.getFormProperties().getHeader() != null) {
			result.setHeader(basicGridForm.getFormProperties().getHeader());
		}
		if (basicGridForm.getFormProperties().getFooter() != null) {
			result.setFooter(basicGridForm.getFormProperties().getFooter());
		}

		if (basicGridForm.orderByColumnNames().length > 1) {
			String s = "";
			for (int i = 0; i < basicGridForm.orderByColumnNames().length; i++) {
				if (i > 0) {
					s = s + ", ";
				}
				s = s + basicGridForm.orderByColumnNames()[i];
				if (basicGridForm.descOrders()[i]) {
					s = s + " desc";
				}
			}
			result.setLyraGridSorting(s);
		} else {
			result.setGridSorting(new GridSorting());
			result.getGridSorting()
					.setSortColId(basicGridForm.orderByColumnNames()[0].replace("\"", ""));
			if (basicGridForm.descOrders()[0]) {
				result.getGridSorting().setSortColDirection(Sorting.DESC);
			}
		}

		result.getLiveInfo().setOffset(0);
		result.getLiveInfo().setLimit(basicGridForm.getGridHeight());
		result.getLiveInfo().setTotalCount(basicGridForm.getApproxTotalCount());

	}

	private void setupDefaultAction() {
		if (basicGridForm.getFormProperties().getDefaultaction() == null) {
			return;
		}

		DefaultHandler myHandler = new DefaultHandler() {

			private final ActionFactory actionFactory = new ActionFactory(context);

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final org.xml.sax.Attributes attrs) {
				if (actionFactory.canHandleStartTag(qname)) {
					Action action =
						actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
					result.setDefaultAction(action);
					return;
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (actionFactory.canHandleEndTag(qname)) {
					Action action = actionFactory.handleEndTag(namespaceURI, lname, qname);
					result.setDefaultAction(action);
					return;
				}
			}

			@Override
			public void characters(final char[] arg0, final int arg1, final int arg2) {
				actionFactory.handleCharacters(arg0, arg1, arg2);
			}
		};

		SimpleSAX sax = new SimpleSAX(
				TextUtils.stringToStream(basicGridForm.getFormProperties().getDefaultaction()),
				myHandler, DEFAULT_ACTION_XML_ERROR);
		sax.parse();

		if (result.getDefaultAction() != null) {
			result.getDefaultAction().actualizeBy(context);
		}

		Action wrong = result.checkActions();
		if (wrong != null) {
			throw new IncorrectElementException(CHECK_DEFAULT_ACTION_ERROR, wrong);
		}

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
			new DefaultGridSettingsApplyStrategy(gridProps, result.getUISettings());
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
			result.setTextColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_BG_COLOR);
		if (value != null) {
			result.setBackgroundColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_FONT_SIZE);
		if (value != null) {
			result.setFontSize(value);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_BOLD);
		if (boolValue) {
			result.addFontModifier(FontModifier.BOLD);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_IT);
		if (boolValue) {
			result.addFontModifier(FontModifier.ITALIC);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_UL);
		if (boolValue) {
			result.addFontModifier(FontModifier.UNDERLINE);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_ST);
		if (boolValue) {
			result.addFontModifier(FontModifier.STRIKETHROUGH);
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

			result.getColumns().add(column);

		}
	}
	// CHECKSTYLE:ON

	private void setupPluginSettings() {
		String plugin = "lyraDGrid";

		result.getJSInfo().setCreateProc("create" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setRefreshProc("refresh" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setAddRecordProc("addRecord" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setSaveProc("save" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setRevertProc("revert" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setClipboardProc("clipboard" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setPartialUpdate("partialUpdate" + TextUtils.capitalizeWord(plugin));

	}

	private void setupUnused() {
		result.setVirtualColumns(null);

		result.setAutoSelectRecordId(null);
		// result.setAutoSelectRecordId("63028000006005400");
		// похоже не влияет
		// result.getEventManager().setFireGeneralAndConcreteEvents(true);
		result.setAutoSelectColumnId(null);

	}

}