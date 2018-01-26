package ru.curs.showcase.test.plugin;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.html.plugin.PluginFactory;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.core.primelements.datapanel.DataPanelFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.*;

/**
 * Тесты UI плагинов на уровне фабрик.
 * 
 * @author den
 * 
 */
public class PluginFactoryTest extends AbstractTestWithDefaultUserData {

	// @Test
	public void pluginCommandShouldRaiseExceptionWhenNoAdapterFile() throws Exception {
		PluginInfo elInfo = new PluginInfo("id", "fake2", PLUGIN_RADAR_PROC);

		HTMLGateway wtgateway = new HtmlDBGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(getSimpleTestContext(), elInfo);
		PluginFactory factory = new PluginFactory(rawWT);
		try {
			factory.build();
			fail();
		} catch (SettingsFileOpenException e) {
			assertEquals(SettingsFileType.PLUGIN_ADAPTER, e.getFileType());
			assertEquals("plugins/fake2/fake2.js", e.getFileName());
			assertEquals("Адаптер для плагина 'plugins/fake2/fake2.js' не найден или поврежден",
					e.getLocalizedMessage());
		}
	}

	// @Test
	public void pluginCommandShouldRaiseExceptionWhenNoJSLibFile() throws Exception {
		PluginInfo elInfo = new PluginInfo("id", "fakeLibPlugin2", PLUGIN_RADAR_PROC);

		HTMLGateway wtgateway = new HtmlDBGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(getSimpleTestContext(), elInfo);
		PluginFactory factory = new PluginFactory(rawWT);
		try {
			factory.build();
			fail();
		} catch (SettingsFileOpenException e) {
			assertEquals(SettingsFileType.LIBRARY_ADAPTER, e.getFileType());
			assertEquals("libraries/fakeLib2/ext-all.js", e.getFileName());
			assertEquals(
					"Файл библиотеки 'libraries/fakeLib2/ext-all.js' не найден или поврежден",
					e.getLocalizedMessage());
		}
	}

	// @Test
	public void pluginCommandShouldRaiseExceptionWhenNoCompDir() throws Exception {
		PluginInfo elInfo = new PluginInfo("id", "fakeComp", PLUGIN_RADAR_PROC);

		HTMLGateway wtgateway = new HtmlDBGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(getSimpleTestContext(), elInfo);
		PluginFactory factory = new PluginFactory(rawWT);
		try {
			factory.build();
			fail();
			// CHECKSTYLE:OFF
		} catch (RuntimeException e) {
			// CHECKSTYLE:ON
			assertEquals("Компонент 'extJS2' не найден", e.getLocalizedMessage());
		}
	}

	@Test
	public void datapanelFactoryMustReadPluginInfo() {
		PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST2_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		final PluginInfo elementInfo =
			(PluginInfo) panel.getTabById("4").getElementInfoById("0401");

		assertNotNull(elementInfo);
		assertEquals(DataPanelElementType.PLUGIN, elementInfo.getType());
		assertEquals(RADAR_COMP, elementInfo.getPlugin());
		assertEquals("fake.xsl", elementInfo.getTransformName());
		assertEquals(PLUGIN_HANDLE_RADAR_PY, elementInfo.getPostProcessProcName());
	}
}
