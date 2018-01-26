package ru.curs.showcase.test.geomap;

import org.junit.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.geomap.GeoMapDBGateway;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Тест шлюза получения данных карты.
 * 
 * @author den
 * 
 */
public class GeoMapGatewayTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основная функция тестирования шлюза.
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testGetData() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		gateway.getRawData(context, element);
	}

	@Test
	@Ignore
	// !!!
	public void testGetDataJython() {
		CompositeContext context = getTestContext1();
		context.setSession("</" + XMLSessionContextGenerator.SESSION_CONTEXT_TAG + ">");
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.GEOMAP);
		element.setProcName("geomap/GeoMapSimple.py");
		generateTestTabWithElement(element);

		RecordSetElementGateway<CompositeContext> gateway = new RecordSetElementJythonGateway();
		gateway.getRawData(context, element);
	}
}
