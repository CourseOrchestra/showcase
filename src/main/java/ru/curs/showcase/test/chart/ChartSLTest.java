package ru.curs.showcase.test.chart;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.chart.ChartGetCommand;
import ru.curs.showcase.test.AbstractTest;

/**
 * Тесты фабрики графиков.
 * 
 * @author den
 * 
 */
public class ChartSLTest extends AbstractTest {

	/**
	 * Проверка работы адаптера в JSON.
	 */
	@Test
	@Ignore
	// !!!
	public void testAdaptChartForJS() {
		CompositeContext context = getTestContext3();
		DataPanelElementInfo element = getTestChartInfo();

		ChartGetCommand command = new ChartGetCommand(context, element);
		Chart chart = command.execute();

		assertNotNull(context.getSession());
		assertNull(chart.getJavaDynamicData());
		assertNotNull(chart.getJsDynamicData());
		assertTrue(chart.getJsDynamicData().startsWith("{"));
		assertTrue(chart.getJsDynamicData().endsWith("}"));
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test
	public void testWrongElement1() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		ChartGetCommand command = new ChartGetCommand(new CompositeContext(), element);
		try {
			command.execute();
			fail();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test
	public void testWrongElement2() {
		DataPanelElementInfo element = new DataPanelElementInfo("id", null);
		element.setProcName("proc");

		ChartGetCommand command = new ChartGetCommand(new CompositeContext(), element);
		try {
			command.execute();
			fail();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	/**
	 * Проверка на то, что описание элемента не полностью заполнено.
	 * 
	 * @throws Throwable
	 * 
	 */
	@Test
	public void testWrongElement3() {
		ChartGetCommand command = new ChartGetCommand(new CompositeContext(), null);
		try {
			command.execute();
			fail();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class, e.getCause().getClass());
		}
	}

	@Test
	@Ignore
	// !!!
	public void testJython() {
		CompositeContext context = generateContextWithSessionInfo();
		context.setMain(MAIN_CONTEXT_TAG);
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		elInfo.setProcName("chart/ChartSimple.py");
		generateTestTabWithElement(elInfo);

		ChartGetCommand command = new ChartGetCommand(context, elInfo);
		Chart chart = command.execute();

		assertNotNull(chart.getJsDynamicData());
		assertEquals(0, chart.getEventManager().getEventForValue("series", 1).size());
	}

	@Test
	public void testIdJSONAdaptor() {
		GeoMap map = new GeoMap();
		map.setJavaDynamicData(new GeoMapData());
		map.getJavaDynamicData().addLayer(GeoMapFeatureType.LINESTRING).setId("test");
		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(map);
		assertTrue(map.getJsDynamicData().contains("\"id\":\"test\""));
	}
}
