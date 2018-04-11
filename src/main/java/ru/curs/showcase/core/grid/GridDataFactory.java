package ru.curs.showcase.core.grid;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.stream.*;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gwt.user.client.rpc.SerializationException;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementSubType;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.event.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для формирования данных гридов на основе XmlDS.
 * 
 */
public class GridDataFactory extends CompBasedElementFactory {

	/**
	 * Не локальная Locale по умолчанию :) Используется для передачи данных в
	 * приложение, которые плохо обрабатывают текущую Locale.
	 */
	private static final Locale DEF_NON_LOCAL_LOCALE = Locale.US;
	/**
	 * Тэг столбца события в гриде.
	 */
	private static final String EVENT_COLUMN_TAG = "column";
	/**
	 * Префикс имени события, определяющий событие в ячейке.
	 */
	private static final String CELL_PREFIX = "cell";

	private static final String ROWSTYLE = "rowstyle";

	private static final String KEYVALUES_SEPARATOR = "_D13k82F9g7_";
	private static final String ADDDATA_COLUMN = "addData" + KEYVALUES_SEPARATOR;

	/**
	 * Признак того, что нужно применять форматирование для дат и чисел при
	 * формировании грида. По умолчанию - нужно. Отключать эту опцию необходимо
	 * при экспорте в Excel.
	 */
	private Boolean applyLocalFormatting = true;

	/**
	 * Результат работы фабрики.
	 */
	private GridData result;

	private final ArrayList<HashMap<String, String>> records =
		new ArrayList<HashMap<String, String>>();

	private final List<GridEvent> events = new ArrayList<GridEvent>();

	private GridServerState state = null;

	private final ResourceBundle resourceBundleForGettext =
		UserDataUtils.getResourceBundleForGettext();

	public GridDataFactory(final RecordSetElementRawData aRaw, final GridServerState aState,
			final boolean aApplyLocalFormatting) {
		super(aRaw);
		state = aState;
		applyLocalFormatting = aApplyLocalFormatting;
	}

	public ArrayList<HashMap<String, String>> getRecords() {
		return records;
	}

	public GridServerState getState() {
		return state;
	}

	@Override
	public GridContext getCallContext() {
		return (GridContext) super.getCallContext();
	}

	@Override
	public GridData getResult() {
		return result;
	}

	@Override
	protected SAXTagHandler getConcreteHandler() {
		return null;
	}

	@Override
	protected String getSettingsErrorMes() {
		return null;
	}

	/**
	 * Построение данных грида (загрузка данных).
	 * 
	 * @return - данные.
	 */
	public GridData buildData() {

		initResult();

		prepareData();
		checkSourceError();
		releaseResources();

		fillResultByData();

		return result;

	}

	@Override
	protected void initResult() {
		result = new GridData();
	}

	@Override
	protected void prepareData() {
		if (getXmlDS() == null) {

			// Начало перевода с помощью Gettext.
			InputStream is = getSource().getXmlDS();
			String str = "";
			try {
				str = TextUtils.streamToString(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			str = UserDataUtils.modifyVariables(str);
			is = TextUtils.stringToStream(str);
			setXmlDS(is);
			// Окончание перевода с помощью Gettext.

			// setXmlDS(getSource().getXmlDS());
		}
	}

	@Override
	protected void checkSourceError() {
		super.checkSourceError();
		if (getXmlDS() == null) {
			throw new DBQueryException(getElementInfo(), NO_RESULTSET_ERROR);
		}
	}

	private static final String SAX_ERROR_MES = "XML-датасет грида";

	@Override
	protected void fillResultByData() {

		XmlDSHandler handler = new XmlDSHandler();
		SimpleSAX sax = new SimpleSAX(getXmlDS(), handler, SAX_ERROR_MES);
		sax.parse();

		try {
			getXmlDS().close();
			setXmlDS(null);
			getSource().setXmlDS(null);
		} catch (IOException e) {
			throw new SAXError(e);
		}

		postProcessingByXmlDS();

	}

	/**
	 * Формирует грид на основе XML-датасета.
	 */
	private final class XmlDSHandler extends DefaultHandler {

		private boolean processRecord = false;
		private boolean processValue = false;

		private String curColId = "";
		private ByteArrayOutputStream osValue = null;
		private XMLStreamWriter writerValue = null;

		private HashMap<String, String> rec = null;

		private XmlDSHandler() {
			super();

		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equals(localName)) {
				processRecord = true;
				rec = new HashMap<String, String>();
				return;
			}

			if (!processRecord) {
				return;
			}

			if (processValue) {
				try {
					writerValue.writeStartElement(localName);
					for (int i = 0; i < atts.getLength(); i++) {
						writerValue.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			} else {
				// Здесь осуществляется перевод с помощью Gettext.
				curColId = UserDataUtils.modifyVariables(XMLUtils.unEscapeTagXml(localName),
						resourceBundleForGettext);
				// curColId = XMLUtils.unEscapeTagXml(localName);

				processValue = true;
				osValue = new ByteArrayOutputStream();
				try {
					writerValue = XMLOutputFactory.newInstance().createXMLStreamWriter(osValue,
							TextUtils.DEF_ENCODING);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (processValue) {
				try {
					writerValue.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}
		}

		// CHECKSTYLE:OFF
		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (RECORD_TAG.equals(localName)) {
				if (applyLocalFormatting && (rec.get(PROPS_TAG) != null)) {
					readEvents(rec,
							"<" + PROPS_TAG + ">" + rec.get(PROPS_TAG) + "</" + PROPS_TAG + ">");
					rec.remove(PROPS_TAG);
				}

				if ((getCallContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID)
						&& (!(getCallContext().getPartialUpdate()
								|| getCallContext().getUpdateParents()))) {
					rec.put("parentId", getCallContext().getParentId());
				}

				records.add(rec);
				processRecord = false;
				return;
			}

			if (processValue) {
				// Здесь осуществляется перевод с помощью Gettext.
				String colId = UserDataUtils.modifyVariables(XMLUtils.unEscapeTagXml(localName),
						resourceBundleForGettext);
				// String colId = XMLUtils.unEscapeTagXml(localName);
				try {
					if (colId.equals(curColId)) {
						String value = osValue.toString(TextUtils.DEF_ENCODING);

						String englId;
						GridServerColumnConfig gcc = state.getColumns().get(curColId);
						if (gcc == null) {
							if (("~~" + ID_TAG).equalsIgnoreCase(curColId)) {
								englId = ID_TAG;
							} else {
								englId = curColId;
							}

							if (getCallContext()
									.getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
								if ("hasChildren".equalsIgnoreCase(englId)) {
									englId = "hasChildren";
								}
								if (isTreeGridIcon(englId)) {
									value = getTreeGridIcon(value);
								}
							}

						} else {
							englId = gcc.getId();
							value = getCellValueForXmlDS(value, gcc);
						}
						rec.put(englId, value);

						writerValue.close();
						processValue = false;
					} else {
						writerValue.writeEndElement();
					}
				} catch (XMLStreamException | UnsupportedEncodingException e) {
					throw new SAXError(e);
				}
			}
		}
		// CHECKSTYLE:ON
	}

	private boolean isTreeGridIcon(final String englId) {
		return "TreeGridNodeCloseIcon".equalsIgnoreCase(englId)
				|| "TreeGridNodeOpenIcon".equalsIgnoreCase(englId)
				|| "TreeGridNodeLeafIcon".equalsIgnoreCase(englId);
	}

	private String getTreeGridIcon(final String value) {
		String res = String.format("%s/%s",
				UserDataUtils.getRequiredProp(UserDataUtils.IMAGES_IN_GRID_DIR), value);

		res = "<a><img border=\"0\" src=\"" + XMLUtils.unEscapeTagXml(res) + "\"></a>";

		return res;
	}

	/**
	 * Считывает события для записи и ее ячеек, а также доп. параметры записи.
	 * Один из таких параметров - стиль CSS. Есть возможность задать несколько
	 * стилей для каждой записи.
	 */
	private void readEvents(final HashMap<String, String> rec, final String data) {
		EventFactory<GridEvent> factory =
			new EventFactory<GridEvent>(GridEvent.class, getCallContext());
		factory.initForGetSubSetOfEvents(EVENT_COLUMN_TAG, CELL_PREFIX,
				getElementInfo().getType().getPropsSchemaName());
		SAXTagHandler recPropHandler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				if (aQname.equalsIgnoreCase(GeneralConstants.STYLE_CLASS_TAG)) {
					String newValue = attrs.getValue(NAME_TAG);
					String value = rec.get(ROWSTYLE);
					if (value == null) {
						value = newValue;
					} else {
						value = value + " " + newValue;
					}
					rec.put(ROWSTYLE, value);
				}
				if (aQname.equalsIgnoreCase(GeneralConstants.READONLY_TAG)) {
					String newValue = attrs.getValue(VALUE_TAG);
					boolean readonly = Boolean.parseBoolean(newValue);
					if (readonly) {
						String rusColId = attrs.getValue(EVENT_COLUMN_TAG);
						rusColId = XMLUtils.unEscapeTagXml(rusColId);
						if (rusColId == null) {
							newValue = "all;";
						} else {
							newValue = state.getColumns().get(rusColId).getId() + ";";
						}

						String value = rec.get(GeneralConstants.READONLY_TAG);
						if (value == null) {
							value = newValue;
						} else {
							value = value + newValue;
						}

						rec.put(GeneralConstants.READONLY_TAG, value);
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

		events.addAll(factory.getSubSetOfEvents(new ID(rec.get(ID_TAG)), data));

	}

	private String getCellValueForXmlDS(final String aValue, final GridServerColumnConfig col) {
		String value = aValue;
		if (value == null) {
			value = "";
		}
		if (value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
			value = "";
			return value;
		}

		if (col.getValueType().isGeneralizedString()) {
			value = XMLUtils.unEscapeValueXml(value);
		}

		if (col.getValueType() == GridValueType.IMAGE) {
			value = String.format("%s/%s",
					UserDataUtils.getRequiredProp(UserDataUtils.IMAGES_IN_GRID_DIR), value);
		} else if (col.getValueType() == GridValueType.LINK) {
			value = UserDataUtils.replaceVariables(value);
			value = normalizeLink(value);
			value = makeSafeXMLAttrValues(value);
			value = getLink(value);
		} else if (col.getValueType() == GridValueType.DOWNLOAD) {
			value = UserDataUtils.replaceVariables(value);
		} else if (col.getValueType().isDate()) {
			DateTime dt = new DateTime(value);
			java.util.Date date = dt.toDate();
			value = getStringValueOfDate(date, col);
		} else if (col.getValueType().isNumber()) {
			value = getStringValueOfNumber(Double.valueOf(value), col);
		}
		return value;
	}

	private static String normalizeLink(final String aValue) {
		String value = aValue.trim();
		value = value.replace("></" + GridValueType.LINK.toString().toLowerCase() + ">", "/>");
		return value;
	}

	/**
	 * Функция для замены служебных символов XML (только XML, не HTML!) в
	 * описании ссылки в гриде.
	 * 
	 * @param value
	 *            - текст ссылки.
	 * @return - исправленный текст ссылки.
	 */
	public static String makeSafeXMLAttrValues(final String value) {
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

	private String getStringValueOfNumber(final Double value, final GridServerColumnConfig col) {
		NumberFormat nf;
		if (applyLocalFormatting) {
			nf = NumberFormat.getNumberInstance();
		} else {
			nf = NumberFormat.getNumberInstance(DEF_NON_LOCAL_LOCALE);
		}
		if (col.getFormat() != null) {
			nf.setMinimumFractionDigits(Integer.parseInt(col.getFormat()));
			nf.setMaximumFractionDigits(Integer.parseInt(col.getFormat()));
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

	private String getStringValueOfDate(final java.util.Date date,
			final GridServerColumnConfig col) {
		DateFormat df = null;

		Integer style = DateFormat.DEFAULT;
		String value = state.getDateValuesFormat();
		if (value != null) {
			style = DateTimeFormat.valueOf(value).ordinal();
		}
		if (col.getValueType() == GridValueType.DATE) {
			df = DateFormat.getDateInstance(style);
		} else if (col.getValueType() == GridValueType.TIME) {
			df = DateFormat.getTimeInstance(style);
		} else if (col.getValueType() == GridValueType.DATETIME) {
			df = DateFormat.getDateTimeInstance(style, style);
		} else {
			df = DateFormat.getDateTimeInstance(style, style);
		}

		return df.format(date);
	}

	// CHECKSTYLE:OFF
	@SuppressWarnings("unchecked")
	private void postProcessingByXmlDS() {

		if (applyLocalFormatting) {

			for (Event event : events) {
				Action action = event.getAction();
				action.actualizeBy(getCallContext());
			}

			for (Event event : events) {
				Action action = event.getAction();
				if (!action.isCorrect()) {
					throw new IncorrectElementException(CHECK_ACTION_ERROR, action);
				}
			}

			if ((records.size() > 0) && (events.size() > 0)) {
				try {
					String stringEvents =
						com.google.gwt.user.server.rpc.RPC.encodeResponseForSuccess(
								FakeService.class.getMethod("serializeEvents"), events);
					records.get(0).put("events", stringEvents);
				} catch (SerializationException | NoSuchMethodException e) {
					throw GeneralExceptionFactory.build(e);
				}
			}

			JSONArray data = new JSONArray();
			for (HashMap<String, String> rec : records) {
				data.add(rec);
			}

			JSONObject objAddData = null;
			if (state.isForceLoadSettings()) {
				GridAddData addData = new GridAddData();
				addData.setHeader(state.getHeader());
				addData.setFooter(state.getFooter());
				try {
					String stringAddData =
						com.google.gwt.user.server.rpc.RPC.encodeResponseForSuccess(
								FakeService.class.getMethod("serializeGridAddData"), addData);
					if (data.size() > 0) {
						((HashMap<String, String>) data.get(0)).put(ADDDATA_COLUMN, stringAddData);
					} else {
						objAddData = new JSONObject();
						objAddData.put(ADDDATA_COLUMN, stringAddData);
					}
				} catch (SerializationException | NoSuchMethodException e) {
					throw GeneralExceptionFactory.build(e);
				}
			}

			if (data.size() > 0) {
				result.setData(data.toJSONString());
			} else {
				if (objAddData != null) {
					result.setData(objAddData.toJSONString());
				} else {
					result.setData(data.toJSONString());
				}
			}

		} else {

			String imageCols = "";
			for (GridServerColumnConfig col : state.getColumns().values()) {
				if (col.getValueType() == GridValueType.IMAGE) {
					imageCols = imageCols + col.getId() + ";";
				}
			}

			for (HashMap<String, String> rec : records) {
				for (String colId : rec.keySet()) {

					String title = null;

					String value = rec.get(colId);

					if (value != null) {
						if (value.toLowerCase().trim().startsWith("<div")) {
							title = exportToExcelGetTitleFromDiv(value);
						} else {
							if (imageCols.contains(colId + ";")) {
								title = exportToExcelGetTitleFromImage(value);
							}
						}
					}

					if (title != null) {
						title = XMLUtils.xmlServiceSymbolsToNormal(title);
						rec.put(colId, title);
					} else {
						if (value != null) {
							value = XMLUtils.xmlServiceSymbolsToNormal(value);
							rec.put(colId, value);
						}
					}

				}
			}

		}

		if (getCallContext().getSubtype() == DataPanelElementSubType.JS_TREE_GRID) {
			getCallContext().getLiveInfo().setOffset(0);
			getCallContext().getLiveInfo().setLimit(records.size());
			getCallContext().getLiveInfo().setTotalCount(records.size());
		} else {
			getCallContext().getLiveInfo().setTotalCount(state.getTotalCount());
		}

	}

	// CHECKSTYLE:ON

	private String exportToExcelGetTitleFromDiv(final String value) {
		/**
		 * Получает title из div.
		 */
		final class DivHandler extends DefaultHandler {
			private String title = null;

			public String getTitle() {
				return title;
			}

			@Override
			public void startElement(final String uri, final String localName, final String name,
					final Attributes atts) {
				if (atts.getValue("title") != null) {
					title = atts.getValue("title");
				}
			}
		}

		DivHandler handler = new DivHandler();
		SimpleSAX sax = new SimpleSAX(TextUtils.stringToStream(value), handler, "парсинг div");
		sax.parse();

		return handler.getTitle();
	}

	private String exportToExcelGetTitleFromImage(final String value) {
		String title = null;
		final String sep = ":";
		if (value.indexOf(sep) > -1) {
			title = value.substring(value.indexOf(sep) + 1, value.length());
		}
		return title;
	}

}