package ru.curs.showcase.test.chart;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.ChildPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.chart.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.ReflectionUtils;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Тесты фабрики графиков.
 * 
 * @author den
 * 
 */
public class ChartFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String FIRST_COL_CAPTION = "3кв. 2005г.";
	private static final String SELECTOR_COL_FIRST_VALUE =
		"Запасы на конец отчетного периода - Всего";
	private static final String FIRST_PERIOD_CAPTION = "Период 1";

	/**
	 * Основной тест.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	@Ignore
	// !!!
	public void testGetData() throws Exception {
		final int seriesCount = 9;
		final ChildPosition defaultPos = ChildPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 200;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "2", "22");

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
		assertEquals(0, chart.getEventManager().getEvents().size());
	}

	/**
	 * Тест, проверяющий возврат событий для графика.
	 * 
	 */
	@Test
	@Ignore
	// !!!
	public void testGetEventsAndColors() throws Exception {
		final String seriesName = "Алтайский край";
		final ID secondGridId = new ID("4");

		CompositeContext context = getTestContext3();
		// график со второй вкладки в панели a.xml
		DataPanelElementInfo element = getTestChartInfo();

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();

		assertNotNull(chart.getDefaultAction());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, chart.getDefaultAction()
				.getDataPanelActionType());

		assertEquals(secondGridId, chart.getDefaultAction().getDataPanelLink().getElementLinks()
				.get(0).getId());
		assertNotNull(chart.getEventManager().getEvents());
		assertTrue(chart.getEventManager().getEvents().size() > 0);

		Event event = chart.getEventManager().getEvents().get(0);
		assertEquals(InteractionType.SINGLE_CLICK, event.getInteractionType());
		assertNull(event.getId2());
		assertEquals(seriesName, event.getId1().getString());

		assertNotNull(event.getAction());
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, event.getAction()
				.getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, event.getAction().getNavigatorActionType());
		CompositeContext calcContext = element.getContext(event.getAction());
		assertNotNull(calcContext);
		assertEquals(context.getMain(), calcContext.getMain());
		assertNull(calcContext.getSession());

		// второй грид со второй вкладки в панели a.xml
		DataPanelElementInfo secondGrid = getDPElement(TEST_XML, "2", secondGridId.getString());
		calcContext = secondGrid.getContext(event.getAction());
		assertNotNull(calcContext);
		assertTrue(ReflectionUtils.equals(context, calcContext));

		// проверяем цвета
		assertEquals("#00FFFF", chart.getJavaDynamicData().getSeries().get(0).getColor());
	}

	/**
	 * Тест, проверяющий возврат подписей для графика.
	 * 
	 */
	@Test
	@Ignore
	// !!!
	public void testGetHints() throws Exception {
		CompositeContext context = getTestContext3();
		// график со второй вкладки в панели a.xml
		DataPanelElementInfo element = getTestChartInfo();

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();
		ChartData data = chart.getJavaDynamicData();
		ChartSeriesValue value = data.getSeries().get(0).getData().get(0);
		assertEquals(
				String.format("%d (%s): %s", 1, data.getLabelsX().get(1).getText(), value.getY()),
				value.getTooltip());
		assertEquals(data.getLabelsX().get(1).getText(), value.getLegend());
	}

	/**
	 * Проверка работы фабрики, если данные транспонированы.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	// !!!
	public void testFlipedData() throws Exception {
		final int seriesCount = 24;
		final int labelsXCount = 9;
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "2", "210");

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, element);
		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();

		assertEquals(seriesCount, chart.getJavaDynamicData().getSeries().size());
		assertNotNull(chart.getJavaDynamicData().getSeriesById(FIRST_COL_CAPTION));
		assertEquals(labelsXCount + 1, chart.getJavaDynamicData().getLabelsX().size());
		assertEquals("", chart.getJavaDynamicData().getLabelsX().get(0).getText());
		assertEquals(SELECTOR_COL_FIRST_VALUE, chart.getJavaDynamicData().getLabelsX().get(1)
				.getText());
		assertEquals(0, chart.getJavaDynamicData().getLabelsY().size());
		assertNotNull(chart.getEventManager().getEvents());
		assertEquals(0, chart.getEventManager().getEvents().size());
	}

	@Test
	@Ignore
	// !!!
	public void testGetDataFormJython() throws Exception {
		CompositeContext context = getTestContext1();
		context.setSession("<" + XMLSessionContextGenerator.SESSION_CONTEXT_TAG + "/>");
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		elInfo.setProcName("chart/ChartSimple.py");
		generateTestTabWithElement(elInfo);

		RecordSetElementGateway<CompositeContext> gateway = new RecordSetElementJythonGateway();
		RecordSetElementRawData raw = gateway.getRawData(context, elInfo);

		ChartFactory factory = new ChartFactory(raw);
		Chart chart = factory.build();

		assertNotNull(chart);
		assertEquals(1, chart.getJavaDynamicData().getSeries().size());
	}

	/**
	 * Тест, проверяющий формирование графика на основе xml-датасета.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	@Ignore
	// !!!
	public void testLoadByXmlDs() throws Exception {
		final int seriesCount = 9;
		final ChildPosition defaultPos = ChildPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 300;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "5", "51");

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
	 * Тест, проверяющий формирование графика на основе xml-датасета, если
	 * данные транспонированы.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	@Ignore
	// !!!
	public void testLoadByXmlDsFliped() throws Exception {
		final int seriesCount = 24;
		final int labelsXCount = 9;
		final ChildPosition defaultPos = ChildPosition.BOTTOM;
		final int defaultWidth = 500;
		final int defaultHeight = 300;
		final int labelyCount = 2;

		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "5", "52");

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

}
