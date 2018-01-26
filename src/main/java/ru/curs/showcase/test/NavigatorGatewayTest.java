package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.core.primelements.navigator.NavigatorSelector;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.*;

/**
 * Класс тестов для шлюза навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorGatewayTest extends AbstractTestWithDefaultUserData {

	/**
	 * Тест функции получения данных для навигатора для default userdata.
	 * 
	 */
	@Test
	public void testGetData() throws SAXException, IOException {
		DocumentBuilder builder = XMLUtils.createBuilder();
		Document doc = null;
		NavigatorSelector selector = new NavigatorSelector();
		try (PrimElementsGateway gw = selector.getGateway()) {
			DataFile<InputStream> xml =
				gw.getRawData(new CompositeContext(), selector.getSourceName());
			doc = builder.parse(xml.getData());
		}
		assertEquals(GeneralXMLHelper.NAVIGATOR_TAG, doc.getDocumentElement().getNodeName());
	}

	@Test
	@Ignore
	// !!!
	public void testJythonNavigator() {
		AppInfoSingleton.getAppInfo().setCurUserDataId(TEST1_USERDATA);
		CompositeContext context = new CompositeContext();
		context.setSession("<sessioninfo/>");
		PrimElementsGateway gateway = new PrimElementsJythonGateway();
		DataFile<InputStream> file = gateway.getRawData(context, "navigator/NavJythonProc.py");

		assertNotNull(file);
		assertNotNull(file.getData());
	}
}
