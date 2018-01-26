package ru.curs.showcase.test.chart;

import org.junit.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.chart.ChartDBGateway;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Тесты для шлюза данных для графиков.
 * 
 * @author den
 * 
 */
public class ChartGatewayTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основная функция тестирования шлюза.
	 */
	@Test
	public void testGetData() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestChartInfo();

		RecordSetElementGateway<CompositeContext> gateway = new ChartDBGateway();
		gateway.getRawData(context, element);
	}

	@Test
	@Ignore
	// !!!
	public void testGetDataFormJython() {
		CompositeContext context = getTestContext1();
		context.setSession("<" + XMLSessionContextGenerator.SESSION_CONTEXT_TAG + "/>");
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		elInfo.setProcName("chart/ChartSimple.py");
		generateTestTabWithElement(elInfo);

		RecordSetElementGateway<CompositeContext> gateway = new RecordSetElementJythonGateway();
		gateway.getRawData(context, elInfo);
	}
}
