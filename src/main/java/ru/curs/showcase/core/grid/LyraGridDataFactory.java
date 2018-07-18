package ru.curs.showcase.core.grid;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.*;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.celesta.CelestaException;
import ru.curs.celesta.dbutils.Cursor;
import ru.curs.celesta.score.Table;
import ru.curs.fastxl.*;
import ru.curs.lyra.*;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.event.EventFactory;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания данных лирагридов.
 * 
 */
public class LyraGridDataFactory {

	private static final String DEF_NUM_COL_DECIMAL_SEPARATOR = "def.num.column.decimal.separator";
	private static final String DEF_NUM_COL_GROUPING_SEPARATOR =
		"def.num.column.grouping.separator";
	private static final String DEF_DATE_VALUES_FORMAT = "def.date.values.format";

	private static final String GRID_DEFAULT_PROFILE = "default.properties";
	private static final int COLUMN_DEFAULT_PRECISION = 2;

	private final LyraGridContext context;
	private final DataPanelElementInfo elInfo;
	private BasicGridForm basicGridForm = null;

	private GridData result;

	private LyraGridServerState state = null;

	private String profile = null;
	private ProfileReader gridProps = null;

	@SuppressWarnings("unused")
	private static final String ID_TAG = "id";
	private static final String KEYVALUES_SEPARATOR = "_D13k82F9g7_";
	private static final String ADDDATA_COLUMN = "addData" + KEYVALUES_SEPARATOR;
	private static final String EVENT_COLUMN_TAG = "column";
	private static final String CELL_PREFIX = "cell";
	private static final String PROPERTIES = "_properties_";
	private static final String ROWSTYLE = "rowstyle";
	private static final String CHECK_ACTION_ERROR =
		"Некорректное описание действия в элементе инф. панели: ";

	/**
	 * Не локальная Locale по умолчанию. Используется для передачи данных в
	 * приложения, которые плохо обрабатывают текущую Locale (экспорт в Excel).
	 */
	private static final Locale DEF_NON_LOCAL_LOCALE = Locale.US;

	private final List<GridEvent> events = new ArrayList<GridEvent>();

	private GridToExcelExportType excelExportType = null;

	public LyraGridDataFactory(final LyraGridContext aContext,
			final DataPanelElementInfo aElInfo) {
		context = aContext;
		elInfo = aElInfo;
	}

	/**
	 * Построение данных лирагрида.
	 * 
	 * @return - GridData.
	 * @throws CelestaException
	 */
	public GridData buildData() throws CelestaException {
		initResult();

		fillResultByData();

		return result;
	}

	private void initResult() {
		result = new GridData();
	}

	@SuppressWarnings("unchecked")
	private void fillResultByData() throws CelestaException, ValidateException {

		state = (LyraGridServerState) AppInfoSingleton.getAppInfo()
				.getLyraGridCacheState(SessionUtils.getCurrentSessionId(), elInfo, context);

		if (state != null) {
			context.setOrderBy(state.getOrderBy());
		}

		LyraGridGateway lgateway = new LyraGridGateway();
		basicGridForm = lgateway.getLyraFormInstance(context, elInfo);

		if (basicGridForm.getChangeNotifier() == null) {
			throw new ValidateException(new UserMessage(
					"Внимание! Произошло обновление скриптов решения. Для корректной работы необходимо перегрузить грид.",
					MessageType.INFO, "Сообщение"));
		}

		ensureLyraGridServerState();

		if (context.isSortingChanged() || context.isExternalSortingOrFilteringChanged()) {
			((LyraGridScrollBack) basicGridForm.getChangeNotifier())
					.setLyraGridAddInfo(new LyraGridAddInfo());
		}

		if (context.getGridSorting() != null) {
			if (context.isSortingChanged() || (state.getOrderBy() == null)) {
				ArrayList<String> al = new ArrayList<>(2);
				String s = context.getGridSorting().getSortColId();
				if (context.getGridSorting().getSortColDirection() == Sorting.ASC) {
					s = s + " ASC";
					al.add(s);
				} else {
					s = s + " DESC";
					al.add(s);

					if (basicGridForm.meta() instanceof Table) {
						String s2 =
							((Table) basicGridForm.meta()).getPrimaryKey().keySet().toArray()[0]
									+ " DESC";
						if (!s.equalsIgnoreCase(s2)) {
							al.add(s2);
						}
					}
				}

				String[] orderBy = new String[al.size()];
				for (int i = 0; i < al.size(); i++) {
					orderBy[i] = al.get(i);
				}

				state.setOrderBy(orderBy);
				AppInfoSingleton.getAppInfo().storeLyraGridCacheState(
						SessionUtils.getCurrentSessionId(), elInfo, context, state);

				context.setOrderBy(orderBy);

				basicGridForm = lgateway.getLyraFormInstance(context, elInfo);
			}
		}

		// ---------------------------------------------------

		LyraGridAddInfo lyraGridAddInfo =
			((LyraGridScrollBack) basicGridForm.getChangeNotifier()).getLyraGridAddInfo();

		lyraGridAddInfo.setExcelExportType(null);

		int position = -1;
		int lyraApproxTotalCount = basicGridForm.getApproxTotalCount();
		int dgridDelta = context.getLiveInfo().getOffset() - context.getDgridOldPosition();

		List<LyraFormData> records;

		System.out.println("context.isFirstLoad(): " + context.isFirstLoad());

		if (context.isFirstLoad()) {

			records = basicGridForm.getRows();

		} else {

			if (context.getRefreshId() == null) {

				if (context.getLiveInfo().getOffset() == 0) {

					position = 0;

				} else {

					if (lyraApproxTotalCount <= LyraGridScrollBack.DGRID_MAX_TOTALCOUNT) {

						position = context.getLiveInfo().getOffset();

					} else {

						if (Math.abs(dgridDelta) < LyraGridScrollBack.DGRID_SMALLSTEP) {

							position = basicGridForm.getTopVisiblePosition() + dgridDelta;

						} else {

							if (Math.abs(context.getLiveInfo().getOffset()
									- LyraGridScrollBack.DGRID_MAX_TOTALCOUNT) < LyraGridScrollBack.DGRID_SMALLSTEP) {

								position = lyraApproxTotalCount - context.getLiveInfo().getLimit();

							} else {

								double d = lyraApproxTotalCount;
								d = d / LyraGridScrollBack.DGRID_MAX_TOTALCOUNT;
								d = d * context.getLiveInfo().getOffset();
								position = (int) d;

							}

						}

					}

				}

				records = basicGridForm.getRows(position);
			} else {
				records = basicGridForm.setPosition(getKeyValuesById(context.getRefreshId()));

			}

		}

		if (records.size() < context.getLiveInfo().getLimit()) {
			context.getLiveInfo().setTotalCount(records.size());
		} else {
			if (basicGridForm.getApproxTotalCount() <= LyraGridScrollBack.DGRID_MAX_TOTALCOUNT) {
				context.getLiveInfo().setTotalCount(basicGridForm.getApproxTotalCount());
			} else {
				context.getLiveInfo().setTotalCount(LyraGridScrollBack.DGRID_MAX_TOTALCOUNT);
			}
		}

		System.out.println("LyraGridDataFactory.ddddddddddddd1");
		System.out.println("className: " + basicGridForm.getClass().getSimpleName());
		System.out.println("position: " + position);
		System.out.println("lyraNewPosition: " + basicGridForm.getTopVisiblePosition());
		System.out.println("lyraOldPosition: " + lyraGridAddInfo.getLyraOldPosition());
		System.out.println("lyraApproxTotalCount(before getRows): " + lyraApproxTotalCount);
		System.out.println(
				"getApproxTotalCount(after getRows): " + basicGridForm.getApproxTotalCount());
		System.out.println("records.size(): " + records.size());
		System.out.println("dGridLimit(): " + context.getLiveInfo().getLimit());
		System.out.println("dGridTotalCount: " + context.getLiveInfo().getTotalCount());

		lyraGridAddInfo.setLyraOldPosition(basicGridForm.getTopVisiblePosition());
		lyraGridAddInfo.setDgridOldTotalCount(context.getLiveInfo().getTotalCount());

		// --------------------------------------------------------

		JSONArray data = new JSONArray();

		int length = Math.min(records.size(), context.getLiveInfo().getLimit());
		for (int i = 0; i < length; i++) {
			LyraFormData rec = records.get(i);

			String properties = null;
			JSONObject obj = new JSONObject();
			for (LyraFieldValue lyraFieldValue : rec.getFields()) {

				int colPrecision;
				if (lyraFieldValue.meta().getScale() == 0) {
					colPrecision = COLUMN_DEFAULT_PRECISION;
				} else {
					colPrecision = lyraFieldValue.meta().getScale();
				}

				if (PROPERTIES.equalsIgnoreCase(lyraFieldValue.getName())) {
					properties = lyraFieldValue.getValue().toString();
				} else {
					obj.put(lyraFieldValue.getName(), getCellValue(lyraFieldValue, colPrecision));
				}

			}

			String recId = getIdByKeyValues(rec.getKeyValues());

			if ((properties != null) && (!properties.trim().isEmpty())) {
				if (recId != null) {
					String rowstyle = readEvents(recId, properties);
					if (rowstyle != null) {
						obj.put(ROWSTYLE, rowstyle);
					}
				}
			}

			obj.put("recversion", String.valueOf(rec.getRecversion()));
			obj.put("id" + KEYVALUES_SEPARATOR, recId);
			data.add(obj);

		}

		for (Event event : events) {
			Action action = event.getAction();
			action.actualizeBy(context);
		}

		for (Event event : events) {
			Action action = event.getAction();
			if (!action.isCorrect()) {
				throw new IncorrectElementException(CHECK_ACTION_ERROR, action);
			}
		}

		if ((data.size() > 0) && (events.size() > 0)) {
			try {
				String stringEvents = com.google.gwt.user.server.rpc.RPC.encodeResponseForSuccess(
						FakeService.class.getMethod("serializeEvents"), events);
				((JSONObject) data.get(0)).put("events", stringEvents);
			} catch (SerializationException | NoSuchMethodException e) {
				throw GeneralExceptionFactory.build(e);
			}
		}

		if ((data.size() > 0) && lyraGridAddInfo.isNeedRecreateWebsocket()) {
			((JSONObject) data.get(0)).put("needRecreateWebsocket", true);
			lyraGridAddInfo.setNeedRecreateWebsocket(false);
		}

		// Позиционирование по ключу записи
		if (context.isFirstLoad() && (data.size() > 0)
				&& (basicGridForm.getTopVisiblePosition() > 0)) {

			double d = basicGridForm.getTopVisiblePosition();
			d = (d / basicGridForm.getApproxTotalCount())
					* lyraGridAddInfo.getDgridOldTotalCount();
			int dgridNewPosition = (int) d;
			((JSONObject) data.get(0)).put("dgridNewPosition", dgridNewPosition);

			basicGridForm.externalAction(c -> {
				Object[] keyValues = ((Cursor) c).getCurrentKeyValues();
				String recId = getIdByKeyValues(keyValues);
				((JSONObject) data.get(0)).put("dgridNewPositionId", recId);
				return null;
			}, null);

		}

		JSONObject objAddData = null;
		GridAddData addData = new GridAddData();
		addData.setHeader(basicGridForm.getFormProperties().getHeader());
		addData.setFooter(basicGridForm.getFormProperties().getFooter());
		try {
			String stringAddData = com.google.gwt.user.server.rpc.RPC.encodeResponseForSuccess(
					FakeService.class.getMethod("serializeGridAddData"), addData);
			if (data.size() > 0) {
				((JSONObject) data.get(0)).put(ADDDATA_COLUMN, stringAddData);
			} else {
				objAddData = new JSONObject();
				objAddData.put(ADDDATA_COLUMN, stringAddData);
			}
		} catch (SerializationException | NoSuchMethodException e) {
			throw GeneralExceptionFactory.build(e);
		}

		if (data.size() > 0) {
			result.setData(data.toJSONString());
		} else {
			if (objAddData != null) {
				result.setData(objAddData.toJSONString());
			}
		}

	}

	private void exportExcelPrepare() {
		state = (LyraGridServerState) AppInfoSingleton.getAppInfo()
				.getLyraGridCacheState(SessionUtils.getCurrentSessionId(), elInfo, context);

		if (state != null) {
			context.setOrderBy(state.getOrderBy());
		}

		LyraGridGateway lgateway = new LyraGridGateway();
		basicGridForm = lgateway.getLyraFormInstance(context, elInfo);

		if (basicGridForm.getChangeNotifier() == null) {
			throw new ValidateException(new UserMessage(
					"Внимание! Произошло обновление скриптов решения. Для корректной работы необходимо перегрузить грид.",
					MessageType.INFO, "Сообщение"));
		}

		ensureLyraGridServerState();

		LyraGridAddInfo lyraGridAddInfo =
			((LyraGridScrollBack) basicGridForm.getChangeNotifier()).getLyraGridAddInfo();

		lyraGridAddInfo.setExcelExportType(excelExportType);
	}

	public List<HashMap<String, String>> exportExcelCurrentPage() throws CelestaException {

		excelExportType = GridToExcelExportType.CURRENTPAGE;

		exportExcelPrepare();

		ArrayList<HashMap<String, String>> excelRecords = new ArrayList<HashMap<String, String>>();

		List<LyraFormData> records =
			basicGridForm.setPosition(getKeyValuesById(context.getRefreshId()));
		// lyraGridAddInfo.setLyraOldPosition(basicGridForm.getTopVisiblePosition());

		// int length = Math.min(records.size(),
		// context.getLiveInfo().getLimit());
		int length = records.size();
		for (int i = 0; i < length; i++) {
			LyraFormData rec = records.get(i);

			HashMap<String, String> obj = new HashMap<String, String>();
			for (LyraFieldValue lyraFieldValue : rec.getFields()) {

				int colPrecision;
				if (lyraFieldValue.meta().getScale() == 0) {
					colPrecision = COLUMN_DEFAULT_PRECISION;
				} else {
					colPrecision = lyraFieldValue.meta().getScale();
				}

				if (!PROPERTIES.equalsIgnoreCase(lyraFieldValue.getName())) {
					obj.put(lyraFieldValue.getName(), getCellValue(lyraFieldValue, colPrecision));
				}

			}

			excelRecords.add(obj);
		}

		return excelRecords;

	}

	public ByteArrayOutputStream exportExcelAll() throws Exception {

		excelExportType = GridToExcelExportType.ALL;

		exportExcelPrepare();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		basicGridForm.saveCursorPosition();

		basicGridForm.externalAction(c -> {
			GridRecordSet rs = new LyraGridRecordSet(c, basicGridForm);
			FastXLProcessor fastXLProcessor = new FastXLProcessor(rs, out);
			try {
				fastXLProcessor.execute();
			} catch (EFastXLRuntime e) {
				throw GeneralExceptionFactory.build(e);
			}
			return null;
		}, null);

		basicGridForm.restoreCursorPosition();

		return out;

	}

	private Object[] getKeyValuesById(final String refreshId) {
		String[] keyValues = refreshId.split(KEYVALUES_SEPARATOR);
		return keyValues;
	}

	private String getIdByKeyValues(final Object[] keyValues) {
		String refreshId = "";
		for (int i = 0; i < keyValues.length; i++) {
			if (i > 0) {
				refreshId = refreshId + KEYVALUES_SEPARATOR;
			}
			refreshId = refreshId + keyValues[i].toString();
		}
		return refreshId;
	}

	// CHECKSTYLE:OFF
	private String getCellValue(final LyraFieldValue lyraFieldValue, final Integer precision) {

		Object value = lyraFieldValue.getValue();
		if (value == null) {
			value = "";
		}

		String strValue = value.toString();

		if (strValue.trim().isEmpty() || "null".equalsIgnoreCase(strValue)) {
			return "";
		}

		switch (lyraFieldValue.meta().getType()) {
		case BLOB:
			return strValue;
		case BIT:
			return strValue;
		case DATETIME:
			return getStringValueOfDate((Date) value);
		case REAL:
			return getStringValueOfNumber((Double) value, precision);
		case INT:
			return strValue;
		case VARCHAR:
			strValue = XMLUtils.unEscapeValueXml(strValue);
			String subtype = lyraFieldValue.meta().getSubtype();
			if (subtype != null) {
				switch (subtype.toUpperCase()) {
				case "DOWNLOAD":
					return UserDataUtils.replaceVariables(strValue);

				case "LINK":
					strValue = UserDataUtils.replaceVariables(strValue);
					strValue = normalizeLink(strValue);
					strValue = makeSafeXMLAttrValues(strValue);
					strValue = getLink(strValue);
					return strValue;

				case "IMAGE":
					return String.format("%s/%s",
							UserDataUtils.getRequiredProp(UserDataUtils.IMAGES_IN_GRID_DIR),
							strValue);

				default:
					break;
				}
			}

			return strValue;
		default:
			return strValue;
		}
	}
	// CHECKSTYLE:ON

	private static String normalizeLink(final String aValue) {
		String value = aValue.trim();
		value = value.replace("></" + GridValueType.LINK.toString().toLowerCase() + ">", "/>");
		return value;
	}

	private static String makeSafeXMLAttrValues(final String value) {
		String res = value.trim();

		Pattern pattern = Pattern.compile("(\\&(?!quot;)(?!lt;)(?!gt;)(?!amp;)(?!apos;))");
		Matcher matcher = pattern.matcher(res);
		res = matcher.replaceAll("&amp;");

		pattern = Pattern.compile(
				"(?<!=)(\")(?!\\s*openInNewTab)(?!\\s*text)(?!\\s*href)(?!\\s*image)(?!\\s*/\\>)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&quot;");

		pattern = Pattern.compile("(?<!^)(\\<)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&lt;");

		pattern = Pattern.compile("(\\>)(?!$)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&gt;");

		res = res.replace("'", "&apos;");

		return res;
	}

	private static String getLink(final String value) {
		String result = null;

		try {
			org.w3c.dom.Element el =
				ru.curs.showcase.util.xml.XMLUtils.stringToDocument(value).getDocumentElement();

			String href = el.getAttribute("href");
			String text = el.getAttribute("text");
			if ((text == null) || text.isEmpty()) {
				text = href;
			}
			String image = el.getAttribute("image");
			String openInNewTab = el.getAttribute("openInNewTab");
			String target = null;
			if (Boolean.parseBoolean(openInNewTab)) {
				target = "_blank";
			}

			result = "<a class=\"gwt-Anchor\" href=\"" + href + "\" ";
			if (target != null) {
				result = result + "target=\"_blank\"";
			}
			result = result + ">";
			if ((image == null) || image.isEmpty()) {
				result = result + text;
			} else {
				String alt = text != null ? " alt=\"" + text + "\"" : "";
				result = result + "<img border=\"0\" src=\"" + XMLUtils.unEscapeTagXml(image)
						+ "\"" + alt + "/>";
			}
			result = result + "</a>";

		} catch (SAXException | IOException e) {
			result = null;
		}

		return result;
	}

	private String getStringValueOfNumber(final Double value, final Integer precision) {

		NumberFormat nf;
		if (excelExportType == null) {
			nf = NumberFormat.getNumberInstance();
		} else {
			nf = NumberFormat.getNumberInstance(DEF_NON_LOCAL_LOCALE);
		}

		if (precision != null) {
			nf.setMinimumFractionDigits(precision);
			nf.setMaximumFractionDigits(precision);
		} else {
			final int maximumFractionDigits = 20;
			nf.setMaximumFractionDigits(maximumFractionDigits);
		}

		String decimalSeparator = state.getDecimalSeparator();
		String groupingSeparator = state.getGroupingSeparator();
		if ((decimalSeparator != null) || (groupingSeparator != null)) {
			DecimalFormat df = (DecimalFormat) nf;
			DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
			if (decimalSeparator != null) {
				dfs.setDecimalSeparator(decimalSeparator.charAt(0));
			}
			if (groupingSeparator != null) {
				if (groupingSeparator.isEmpty()) {
					nf.setGroupingUsed(false);
				} else {
					dfs.setGroupingSeparator(groupingSeparator.charAt(0));
				}
			}
			df.setDecimalFormatSymbols(dfs);
		}

		return nf.format(value);
	}

	private String getStringValueOfDate(final java.util.Date date) {
		DateFormat df = null;

		Integer style = DateFormat.DEFAULT;
		String value = state.getDateValuesFormat();
		if (value != null) {
			style = DateTimeFormat.valueOf(value).ordinal();
		}

		df = DateFormat.getDateTimeInstance(style, style);

		return df.format(date);
	}

	private void ensureLyraGridServerState() {
		if (state == null) {
			state = new LyraGridServerState();

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

			String decimalSeparator = gridProps.getStringValue(DEF_NUM_COL_DECIMAL_SEPARATOR);
			if (decimalSeparator != null) {
				if (decimalSeparator.contains(" ")) {
					decimalSeparator = " ";
				}
				if (decimalSeparator.isEmpty()) {
					decimalSeparator = ".";
				}
			}
			state.setDecimalSeparator(decimalSeparator);

			String groupingSeparator = gridProps.getStringValue(DEF_NUM_COL_GROUPING_SEPARATOR);
			if (groupingSeparator != null) {
				if (groupingSeparator.contains(" ")) {
					groupingSeparator = " ";
				}
			}
			state.setGroupingSeparator(groupingSeparator);

			state.setDateValuesFormat(gridProps.getStringValue(DEF_DATE_VALUES_FORMAT));

			AppInfoSingleton.getAppInfo().storeLyraGridCacheState(
					SessionUtils.getCurrentSessionId(), elInfo, context, state);
		}
	}

	private String readEvents(final String recId, final String data) {
		final List<String> rowstyle = new ArrayList<String>(1);
		rowstyle.add(null);
		EventFactory<GridEvent> factory = new EventFactory<GridEvent>(GridEvent.class, context);
		factory.initForGetSubSetOfEvents(EVENT_COLUMN_TAG, CELL_PREFIX,
				elInfo.getType().getPropsSchemaName());
		SAXTagHandler recPropHandler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				if (aQname.equalsIgnoreCase(GeneralConstants.STYLE_CLASS_TAG)) {
					String newValue = attrs.getValue(NAME_TAG);
					if (rowstyle.get(0) == null) {
						rowstyle.set(0, newValue);
					} else {
						rowstyle.set(0, rowstyle.get(0) + " " + newValue);
					}
				}
				return null;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags =
					{ GeneralConstants.STYLE_CLASS_TAG, GeneralConstants.READONLY_TAG };
				return tags;
			}
		};
		factory.addHandler(recPropHandler);

		events.addAll(factory.getSubSetOfEvents(new ID(recId), data));

		return rowstyle.get(0);

	}

}