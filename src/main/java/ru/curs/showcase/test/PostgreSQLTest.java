package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.ChildPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.chart.*;
import ru.curs.showcase.core.geomap.*;
import ru.curs.showcase.core.html.plugin.PluginCommand;
import ru.curs.showcase.core.html.webtext.WebTextGetCommand;
import ru.curs.showcase.core.html.xform.XFormGetCommand;
import ru.curs.showcase.core.primelements.navigator.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Тесты работы с PostgreSQL.
 * 
 */
public class PostgreSQLTest extends AbstractTest {

	private static final String PG_USERDATA = "pg";
	private static final String PG_XML_SP = "funcs.xml";
	private static final String PG_XML_SP_2 = "funcs2.xml";
	private static final String PG_XML_SCRIPTS = "scripts.xml";

	private static final Integer CITIES_COUNT = 10_428;
	private static final String FIRST_COL_CAPTION = "3кв. 2005г.";
	private static final String SELECTOR_COL_FIRST_VALUE =
		"Запасы на конец отчетного периода - Всего";
	private static final String FIRST_PERIOD_CAPTION = "Период 1";

	private static final String PLUGIN_WIDTH = "800px";
	private static final String PLUGIN_HEIGHT = "600px";

	private void setPGUserData() {
		AppInfoSingleton.getAppInfo().setCurUserDataId(PG_USERDATA);
	}

	@Before
	public void setUp() {
		setPGUserData();
	}

	/**
	 * Проверка получения навигатора.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testNavigatorFromSP() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator nav = command.execute();
		final int groupsCount = 2;
		assertEquals(groupsCount, nav.getGroups().size());
		final int elementsCount = 1;
		assertEquals(elementsCount, nav.getGroups().get(0).getElements().size());
		final int subElementsCount = 9;
		assertEquals(subElementsCount, nav.getGroups().get(0).getElements().get(0).getElements()
				.size());
	}

	@Test
	@Ignore
	// !!!
			public
			void testNavigatorFromScript() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Map<String, String> map = new HashMap<>();
		map.put(NavigatorSelector.NAVIGATOR_PROCNAME_PARAM, "navigator/generationtree.sql");
		command.setProps(map);
		Navigator nav = command.execute();
		assertEquals("200px", nav.getWidth());
	}

	/**
	 * Проверка получения вебтекста.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testWebTextFromSP() {
		runWebText(PG_XML_SP);
	}

	@Test
	@Ignore
	// !!!
			public
			void testWebTextFromScript() {
		runWebText(PG_XML_SCRIPTS);
	}

	private void runWebText(final String fileName) {
		CompositeContext context = getTestContext2();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(fileName, "3", "77");

		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();

		assertNotNull(context.getSession());
		assertEquals(1, wt.getEventManager().getEvents().size());
		assertNull(wt.getDefaultAction());
		assertNotNull(wt.getData());
	}

	/**
	 * Проверка получения XForm.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testXFormFromSP() {
		runXForm(PG_XML_SP);
	}

	@Test
	@Ignore
	// !!!
			public
			void testXFormFromScript() {
		runXForm(PG_XML_SCRIPTS);
	}

	private void runXForm(final String fileName) {
		XFormContext xcontext = new XFormContext(getTestContext1());
		xcontext.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(fileName, "6", "61");

		XFormGetCommand command = new XFormGetCommand(xcontext, element);
		XForm xforms = command.execute();

		assertNotNull(xcontext.getSession());
		Action action = xforms.getActionForDependentElements();
		assertNotNull(action);
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId().getString());
		assertEquals("xforms default action", action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());

		assertEquals(2, xforms.getEventManager().getEvents().size());
		action = xforms.getEventManager().getEvents().get(0).getAction();
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId().getString());
		assertEquals("save click on xforms (with filtering)", action.getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());

		assertNotNull(xforms.getXFormParts());
		assertTrue(xforms.getXFormParts().size() > 0);
	}

	/**
	 * Проверка получения плагина.
	 */
	@Test
	@Ignore
	// !!!
			public
			void testPluginFromSP() {
		testPlugin(PG_XML_SP);
	}

	@Test
	@Ignore
	// !!!
			public
			void testPluginFromScript() {
		testPlugin(PG_XML_SCRIPTS);
	}

	private void testPlugin(final String fileName) {
		CompositeContext context = getSimpleTestContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(fileName, "7", "12");

		RequestData requestData = new RequestData();
		requestData.setContext(context);
		requestData.setElInfo((PluginInfo) element);

		PluginCommand command = new PluginCommand(requestData);
		Plugin plugin = command.execute();

		assertEquals(1, plugin.getParams().size());
		assertEquals("[{name: 'Russia', data1: 63.82, data2: 17.18, data3: 7.77},"
				+ "{name: 'Moscow', data1: 47.22, data2: 19.12, data3: 20.21},"
				+ "{name: 'Piter', data1: 58.77, data2: 13.06, data3: 15.22},]", plugin
				.getParams().get(0));
		assertEquals("createRadar", plugin.getCreateProc());
		assertEquals(PLUGIN_HEIGHT, plugin.getStringSize().getHeight());
		assertEquals(PLUGIN_WIDTH, plugin.getStringSize().getWidth());

	}

	/**
	 * Проверка получения графика на основе xml-датасета.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testChartXmlDsFromSP() throws Exception {
		runChartXmlDs(PG_XML_SP_2);
	}

	private void runChartXmlDs(final String fileName) throws Exception {
		final int seriesCount = 9;
		final ChildPosition defaultPos = ChildPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 300;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(fileName, "5", "0");

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();

		assertFalse(chart.getHeader().isEmpty());
		assertTrue(chart.getFooter().isEmpty());
		assertEquals(seriesCount, chart.getJavaDynamicData().getSeries().size());
		assertTrue(chart.getTemplate().length() > 0);
		assertEquals(defaultWidth, chart.getJavaDynamicData().getWidth().intValue());
		assertEquals(defaultHeight, chart.getJavaDynamicData().getHeight().intValue());
		assertTrue(chart.getJavaDynamicData().getLabelsX().size() > 0);
		assertEquals(labelyCount, chart.getJavaDynamicData().getLabelsY().size());
		assertEquals("", chart.getJavaDynamicData().getLabelsX().get(0).getText());
		assertEquals(FIRST_COL_CAPTION, chart.getJavaDynamicData().getLabelsX().get(1).getText());
		assertEquals(defaultPos, chart.getLegendPosition());
		assertEquals(SELECTOR_COL_FIRST_VALUE, chart.getJavaDynamicData().getSeries().get(0)
				.getName());
		assertNotNull(chart.getEventManager().getEvents());
		assertTrue(chart.getEventManager().getEvents().size() > 0);

		assertNull(chart.getDefaultAction());

		Event event = chart.getEventManager().getEvents().get(0);
		assertEquals(InteractionType.SINGLE_CLICK, event.getInteractionType());
		assertNull(event.getId2());
		assertEquals(SELECTOR_COL_FIRST_VALUE, event.getId1().getString());

		assertNotNull(event.getAction());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, event.getAction()
				.getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, event.getAction().getNavigatorActionType());
		CompositeContext calcContext = element.getContext(event.getAction());
		assertNotNull(calcContext);
		assertEquals(context.getMain(), calcContext.getMain());
		assertNull(calcContext.getSession());

		assertEquals("#0000FF", chart.getJavaDynamicData().getSeries().get(0).getColor());

		ChartData data = chart.getJavaDynamicData();
		ChartSeriesValue value = data.getSeries().get(0).getData().get(0);
		assertEquals(data.getLabelsX().get(1).getText(), value.getLegend());
	}

	/**
	 * Проверка получения графика на основе xml-датасета, если данные
	 * транспонированы.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testChartXmlDsFlipedFromSP() throws Exception {
		runChartXmlDsFliped(PG_XML_SP_2);
	}

	private void runChartXmlDsFliped(final String fileName) throws Exception {
		final int seriesCount = 24;
		final int labelsXCount = 9;
		final ChildPosition defaultPos = ChildPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 300;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(fileName, "51", "051");

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();

		assertFalse(chart.getHeader().isEmpty());
		assertTrue(chart.getFooter().isEmpty());
		assertEquals(seriesCount, chart.getJavaDynamicData().getSeries().size());
		assertTrue(chart.getTemplate().length() > 0);
		assertEquals(defaultWidth, chart.getJavaDynamicData().getWidth().intValue());
		assertEquals(defaultHeight, chart.getJavaDynamicData().getHeight().intValue());
		assertTrue(chart.getJavaDynamicData().getLabelsX().size() > 0);
		assertEquals(labelyCount, chart.getJavaDynamicData().getLabelsY().size());
		assertEquals(defaultPos, chart.getLegendPosition());
		assertNotNull(chart.getEventManager().getEvents());
		assertEquals(0, chart.getEventManager().getEvents().size());

		assertNull(chart.getDefaultAction());
		assertNotNull(chart.getJavaDynamicData().getSeriesById(FIRST_COL_CAPTION));
		assertEquals(labelsXCount + 1, chart.getJavaDynamicData().getLabelsX().size());
		assertEquals("", chart.getJavaDynamicData().getLabelsX().get(0).getText());
		assertEquals(FIRST_PERIOD_CAPTION, chart.getJavaDynamicData().getLabelsX().get(1)
				.getText());

	}

	/**
	 * Проверка получения карты на основе xml-датасета.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testGeoMapXmlDsFromSP() throws Exception {
		runGeoMapXmlDs(PG_XML_SP_2);
	}

	private void runGeoMapXmlDs(final String fileName) throws Exception {

		CompositeContext context = getTestContext1();
		context.setSessionParamsMap(generateTestURLParamsForSL(PG_USERDATA));
		DataPanelElementInfo element = getDPElement(fileName, "8", "81");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		GeoMapFactory factory = new GeoMapFactory(raw);
		GeoMap map = factory.build();

		GeoMapData data = map.getJavaDynamicData();
		assertEquals(2, data.getLayers().size());
		assertNotNull(data.getLayerById("l1"));
		assertNotNull(data.getLayerById("l2"));
		assertNotNull(data.getLayerByObjectId("1849"));
		GeoMapLayer layer = data.getLayerByObjectId("2");
		assertNull(layer.getProjection());
		assertNull(data.getLayerByObjectId("fake"));
		assertNull(layer.getHintFormat());
		assertEquals(GeoMapFeatureType.POLYGON, layer.getType());
		final int areasCount = 83;
		assertEquals(areasCount, layer.getFeatures().size());
		assertEquals(1, layer.getIndicators().size());
		GeoMapFeature altay = layer.getObjectById("2");
		assertEquals("Республика Алтай - производство", altay.getTooltip());
		assertNull(altay.getStyle());
		assertEquals(altay.getGeometryId(), altay.getStyleClass());
		final double indValue1 = 3.8;
		final double delta = 0.01;
		assertNotSame("ind1", layer.getIndicators().get(0).getId().getString());
		assertEquals("mainInd", layer.getAttrIdByDBId("ind1").toString());
		assertEquals(true, layer.getIndicators().get(0).getIsMain());
		assertEquals("#2AAA2E", layer.getIndicators().get(0).getStyle());
		assertEquals(indValue1, altay.getValueForIndicator(layer.getIndicators().get(0))
				.doubleValue(), delta);
		assertEquals(layer.getMainIndicator(), layer.getIndicators().get(0));
		layer = data.getLayerById("l1");
		GeoMapFeature novgorod = layer.getObjectById("2532");
		assertEquals(String.format("%s - %s (%s) (%s - %s)", layer.getName(), novgorod.getName(),
				novgorod.getId(), novgorod.getLat(), novgorod.getLon()), novgorod.getTooltip());
		assertNull(novgorod.getStyleClass());

	}

}
