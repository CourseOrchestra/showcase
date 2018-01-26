package ru.curs.showcase.core.chart;

import java.io.*;

import javax.xml.stream.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.api.element.ChildPosition;
import ru.curs.showcase.core.event.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.*;

import com.google.gson.Gson;

/**
 * Шаблонный класс построителя графика. Содержит некоторые шаблонные методы,
 * используемые для создания графика, а также функции установки настроек по
 * умолчанию.
 * 
 * @author den
 * 
 */
public class ChartFactory extends CompBasedElementFactory {
	/**
	 * Константа из шаблона для типа круговой диаграммы.
	 */
	private static final String PIE_CHART = "Pie";

	private static final String MAX_X_TAG = "maxX";

	private static final String SERIES_NAME_TAG = "seriesName";

	private static final String LABEL_X_TAG = "labelx";

	private static final String CHART_SETTINGS_ERROR_MES = "настройки графика";

	private static final String SELECTOR_TAG = "selectorColumn";

	private static final String LABEL_Y_TAG = "labely";

	private static final String LABEL_Y_TEXT = "text";

	protected static final String X_TAG = "x";
	/**
	 * Название столбца, в котором содержатся подписи по X или названия серий.
	 */
	private String selectorColumn;

	/**
	 * Признак того, что набор с данными нужно транспонировать при считывании.
	 */
	private boolean flip;

	private String getSelectorColumn() {
		return selectorColumn;
	}

	private boolean isFlip() {
		return flip;
	}

	/**
	 * Конструируемый график.
	 */
	private Chart result;

	/**
	 * Общий для всех подсказок на графике шаблон форматирования.
	 */
	private String hintFormat;

	public final String getHintFormat() {
		return hintFormat;
	}

	@Override
	public Chart getResult() {
		return result;
	}

	@Override
	public Chart build() throws Exception {
		return (Chart) super.build();
	}

	public ChartFactory(final RecordSetElementRawData aSource) {
		super(aSource);
	}

	@Override
	protected void initResult() {
		result = new Chart(getElementInfo());
		result.setJavaDynamicData(new ChartData());
	}

	@Override
	protected void prepareSettings() {
		super.prepareSettings();
		setXmlDS(getSource().getXmlDS());
	}

	@Override
	protected void prepareData() {
		if (getXmlDS() == null) {
			setXmlDS(getSource().getXmlDS());
		}
	}

	@Override
	protected void fillResultByData() {
		fillLabelsXAndSeriesByXmlDS();
	}

	/**
	 * Класс считывателя настроек графика.
	 * 
	 * @author den
	 * 
	 */
	private class ChartDynamicSettingsReader extends SAXTagHandler {

		/**
		 * Стартовые тэги, которые будут обработаны.
		 */
		private final String[] startTags = { TEMPLATE_TAG, PROPS_TAG, LABEL_Y_TAG };

		/**
		 * Закрывающие тэги, которые будут обрабатываться.
		 */
		private final String[] endTags = { TEMPLATE_TAG };

		/**
		 * Признак чтения шаблона.
		 */
		private transient boolean readingTemplate = false;

		@Override
		public Object handleStartTag(final String namespaceURI, final String lname,
				final String qname, final Attributes attrs) {
			String value;
			Integer intValue = null;
			if (qname.equalsIgnoreCase(TEMPLATE_TAG)) {
				readingTemplate = true;
				getResult().setTemplate("");
				return null;
			}
			if (qname.equalsIgnoreCase(PROPS_TAG)) {
				value = attrs.getValue(LEGEND_TAG);
				value = value.toUpperCase().trim();
				getResult().setLegendPosition(ChildPosition.valueOf(value));

				value = attrs.getValue(WIDTH_TAG);
				intValue = TextUtils.getIntSizeValue(value);
				getResult().getJavaDynamicData().setWidth(intValue);
				value = attrs.getValue(HEIGHT_TAG);
				intValue = TextUtils.getIntSizeValue(value);
				getResult().getJavaDynamicData().setHeight(intValue);
				value = attrs.getValue(SELECTOR_TAG);
				selectorColumn = value;
				value = attrs.getValue(FLIP_TAG);
				flip = Boolean.valueOf(value);
				if (attrs.getIndex(HINT_FORMAT_TAG) > -1) {
					hintFormat = attrs.getValue(HINT_FORMAT_TAG);
				}
				if (attrs.getIndex(FIRE_GENERAL_AND_CONCRETE_EVENTS_TAG) > -1) {
					getResult().getEventManager().setFireGeneralAndConcreteEvents(
							Boolean.valueOf(attrs.getValue(FIRE_GENERAL_AND_CONCRETE_EVENTS_TAG)));
				}
				return null;
			}
			if (qname.equalsIgnoreCase(LABEL_Y_TAG)) {
				ChartLabel currentLabel = new ChartLabel();
				getResult().getJavaDynamicData().getLabelsY().add(currentLabel);
				value = attrs.getValue(VALUE_TAG);
				currentLabel.setValue(Double.parseDouble(value));
				value = attrs.getValue(LABEL_Y_TEXT);
				currentLabel.setText(value);
				return null;
			}
			return null;
		}

		@Override
		public Object handleEndTag(final String namespaceURI, final String lname,
				final String qname) {
			if (qname.equalsIgnoreCase(TEMPLATE_TAG)) {
				readingTemplate = false;
				return null;
			}
			return null;
		}

		@Override
		public void handleCharacters(final char[] arg0, final int arg1, final int arg2) {
			if (readingTemplate) {
				getResult().setTemplate(
						getResult().getTemplate() + String.copyValueOf(arg0, arg1, arg2));
				return;
			}
		}

		@Override
		protected String[] getStartTags() {
			return startTags;
		}

		@Override
		protected String[] getEndTrags() {
			return endTags;
		}
	}

	@Override
	protected void setupDynamicSettings() {
		super.setupDynamicSettings();
		correctFlipMode();
	}

	/**
	 * Для Pie графика удобнее задавать данные в виде нескольких сессий, в
	 * каждой из которых - одно значение. Но увы - используемая нами компонента
	 * не понимает данные в таком формате, так что применяем обратное
	 * транспонирование.
	 */
	private void correctFlipMode() {
		boolean realFlip = flip;
		Gson gson = new Gson();
		FakeChartTemplate template =
			gson.fromJson(getResult().getTemplate(), FakeChartTemplate.class);
		if ((template.getPlot() != null)
				&& (PIE_CHART.equalsIgnoreCase(template.getPlot()[0].getType()))) {
			realFlip = !realFlip;
		}
		flip = realFlip;
	}

	@Override
	protected SAXTagHandler getConcreteHandler() {
		return new ChartDynamicSettingsReader();
	}

	@Override
	protected String getSettingsErrorMes() {
		return CHART_SETTINGS_ERROR_MES;
	}

	@Override
	protected void correctSettingsAndData() {
		super.correctSettingsAndData();
		setupTooltips();
		setupBarLabels();
	}

	private void setupTooltips() {
		if (hintFormat == null) {
			return;
		}
		for (ChartSeries series : result.getJavaDynamicData().getSeries()) {
			int x = 1;
			for (ChartSeriesValue value : series.getData()) {
				String toolTip = hintFormat;
				ChartLabel label = result.getJavaDynamicData().getLabelsX().get(x);
				if (label != null) {
					toolTip = TextUtils.replaceCI(toolTip, "%" + LABEL_X_TAG, label.getText());
				}
				if (value.getY() != null) {
					label = result.getJavaDynamicData().getLabelsYByValue(value.getY());
					if (label != null) {
						toolTip = TextUtils.replaceCI(toolTip, "%" + LABEL_Y_TAG, label.getText());
					}
					toolTip =
						TextUtils.replaceCI(toolTip, "%" + VALUE_TAG, value.getY().toString());
				}
				toolTip = TextUtils.replaceCI(toolTip, "%" + X_TAG, String.valueOf(x));
				toolTip = TextUtils.replaceCI(toolTip, "%" + SERIES_NAME_TAG, series.getName());
				toolTip =
					TextUtils.replaceCI(toolTip, "%" + MAX_X_TAG,
							String.valueOf(series.getData().size()));
				value.setTooltip(toolTip);
				x++;
			}
		}
	}

	private void setupBarLabels() {
		for (ChartSeries series : result.getJavaDynamicData().getSeries()) {
			int x = 1;
			for (ChartSeriesValue value : series.getData()) {
				ChartLabel label = result.getJavaDynamicData().getLabelsX().get(x);
				if (label != null) {
					value.setLegend(label.getText());
				}
				if (value.getY() != null) {
					label = result.getJavaDynamicData().getLabelsYByValue(value.getY());
					if (label != null) {
						if (value.getLegend() == null) {
							value.setLegend(label.getText());
						}
					}
				}
				x++;
			}
		}
	}

	// -------------------------------------------------

	@Override
	protected void checkSourceError() {
		super.checkSourceError();
		if (getXmlDS() == null) {
			throw new DBQueryException(getElementInfo(), NO_RESULTSET_ERROR);
		}
	}

	private void addZeroLabelForX() {
		ChartLabel curLabel;
		curLabel = new ChartLabel();
		curLabel.setValue(0);
		curLabel.setText("");
		getResult().getJavaDynamicData().getLabelsX().add(curLabel);
	}

	private void addValueToSeries(final ChartSeries series, final String value) {
		if ((value == null) || ("null".equalsIgnoreCase(value))) {
			series.addValue(null);
		} else {
			series.addValue(Double.valueOf(value));
		}
	}

	private void readEvents(final ChartSeries series, final String value) {
		EventFactory<ChartEvent> factory =
			new EventFactory<ChartEvent>(ChartEvent.class, getCallContext());
		factory.initForGetSubSetOfEvents(X_TAG, VALUE_TAG, getElementInfo().getType()
				.getPropsSchemaName());
		SAXTagHandler colorHandler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				series.setColor(attrs.getValue(VALUE_TAG));
				return series;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags = { COLOR_TAG };
				return tags;
			}

		};
		factory.addHandler(colorHandler);
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(new ID(series.getName()), value));
	}

	private static final String SAX_ERROR_MES = "XML-датасет графика";

	private void fillLabelsXAndSeriesByXmlDS() {

		addZeroLabelForX();

		DefaultHandler xmlDSHandler;
		if (isFlip()) {
			xmlDSHandler = new XmlDSHandlerIfFlip();
		} else {
			xmlDSHandler = new XmlDSHandlerIfNotFlip();
		}

		SimpleSAX sax = new SimpleSAX(getXmlDS(), xmlDSHandler, SAX_ERROR_MES);
		sax.parse();

		try {
			getXmlDS().close();
			setXmlDS(null);
			getSource().setXmlDS(null);
		} catch (IOException e) {
			throw new SAXError(e);
		}

	}

	/**
	 * Формирует LabelsX и Series на основе XML-датасета в случае, если данные
	 * не транспонированы.
	 */
	private class XmlDSHandlerIfNotFlip extends DefaultHandler {

		private boolean processRecord = false;
		private boolean processValue = false;
		private boolean processProps = false;
		private boolean processSelectorColumn = false;

		private int counterLabel = 1;
		private int counterRecord = 0;
		private ChartSeries series = null;
		private String value = "";

		private ByteArrayOutputStream osProps = null;
		private XMLStreamWriter writerProps = null;

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equals(localName)) {
				counterRecord++;
				processRecord = true;
				series = new ChartSeries();
				return;
			}

			if (getSelectorColumn().equals(localName)) {
				processSelectorColumn = true;
				return;
			}

			if (PROPS_TAG.equals(localName)) {
				processProps = true;
				osProps = new ByteArrayOutputStream();
				try {
					writerProps =
						XMLOutputFactory.newInstance().createXMLStreamWriter(osProps,
								TextUtils.DEF_ENCODING);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (processProps) {
				try {
					writerProps.writeStartElement(localName);
					for (int i = 0; i < atts.getLength(); i++) {
						writerProps.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

			if (!processRecord || processProps || processSelectorColumn) {
				return;
			}

			processValue = true;
			value = "";

			if (counterRecord == 1) {
				ChartLabel curLabel = new ChartLabel();
				curLabel.setValue(counterLabel++);
				curLabel.setText(XMLUtils.unEscapeTagXml(localName));
				getResult().getJavaDynamicData().getLabelsX().add(curLabel);
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (processSelectorColumn) {
				String name = new String(ch, start, length);
				if (series.getName() != null) {
					name = series.getName() + name;
				}
				series.setName(name);
				return;
			}

			if (processProps) {
				try {
					writerProps.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

			if (processValue) {
				value = value + new String(ch, start, length);
				return;
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (getSelectorColumn().equals(localName)) {
				processSelectorColumn = false;
				return;
			}

			if (processProps) {
				try {
					writerProps.writeEndElement();
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (PROPS_TAG.equals(localName)) {
				try {
					readEvents(series, osProps.toString(TextUtils.DEF_ENCODING));
					writerProps.close();
				} catch (UnsupportedEncodingException | XMLStreamException e) {
					throw new SAXError(e);
				}
				processProps = false;
				return;
			}

			if (RECORD_TAG.equals(localName)) {
				processRecord = false;
				getResult().getJavaDynamicData().getSeries().add(series);
				return;
			}

			if (processValue) {
				addValueToSeries(series, value);
				processValue = false;
			}
		}
	}

	/**
	 * Формирует LabelsX и Series на основе XML-датасета в случае, если данные
	 * транспонированы.
	 */
	private class XmlDSHandlerIfFlip extends DefaultHandler {

		private boolean processRecord = false;
		private boolean processValue = false;
		private boolean processSelectorColumn = false;

		private int counterLabel = 1;
		private int counterRecord = 0;
		private ChartSeries series = null;
		private String label = "";
		private String value = "";

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equals(localName)) {
				counterRecord++;
				processRecord = true;
				return;
			}

			if (getSelectorColumn().equals(localName)) {
				processSelectorColumn = true;
				label = "";
				return;
			}

			if (!processRecord || processSelectorColumn) {
				return;
			}

			if (counterRecord == 1) {
				series = new ChartSeries();
				series.setName(XMLUtils.unEscapeTagXml(localName));
				getResult().getJavaDynamicData().getSeries().add(series);
			} else {
				series =
					getResult().getJavaDynamicData().getSeriesById(
							XMLUtils.unEscapeTagXml(localName));
			}

			processValue = true;
			value = "";

		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (processSelectorColumn) {
				label = label + new String(ch, start, length);
				return;
			}

			if (processValue) {
				value = value + new String(ch, start, length);
				return;
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (getSelectorColumn().equals(localName)) {
				processSelectorColumn = false;
				ChartLabel curLabel = new ChartLabel();
				curLabel.setValue(counterLabel++);
				curLabel.setText(label);
				getResult().getJavaDynamicData().getLabelsX().add(curLabel);
				return;
			}

			if (RECORD_TAG.equals(localName)) {
				processRecord = false;
				return;
			}

			if (processValue) {
				addValueToSeries(series, value);
				processValue = false;
			}
		}
	}

}
