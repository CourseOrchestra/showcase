package ru.curs.showcase.test.plugin;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Модульные тесты для класса UI плагина.
 * 
 * @author den
 * 
 */
public class PluginTest extends AbstractTest {

	private static final String PLUGIN_NAME = "flash";
	private static final String PROC_NAME = "getPluginInfo";
	private static final String JYTHON_PROC_NAME = "handleFlash";

	@Test
	public void pluginShouldHaveEmptyPropValuesAfterCreation() {
		Plugin plugin = new Plugin();

		assertNull(plugin.getCreateProc());
		assertNull(plugin.getDefaultAction());
		assertEquals(0, plugin.getParams().size());
		assertEquals(0, plugin.getRequiredJS().size());
		assertEquals(0, plugin.getRequiredCSS().size());
		assertEquals(0, plugin.getEventManager().getEvents().size());
		assertNull(plugin.getStringSize().getHeight());
		assertNull(plugin.getStringSize().getWidth());
		assertTrue(plugin.getStringSize().getAutoSize());
	}

	@Test
	public void correctPluginInfoShouldHavePluginProp() {
		PluginInfo pi = new PluginInfo("id", null, PROC_NAME);
		assertFalse(pi.isCorrect());
		pi.setPlugin(PLUGIN_NAME);
		assertTrue(pi.isCorrect());
	}

	@Test
	public void pluginInfoIsDataPanelElementInfoWithPluginData() {
		PluginInfo pi = generateTestPluginInfo();

		assertTrue(pi instanceof DataPanelElementInfo);
		assertEquals(PROC_NAME, pi.getProcName());
		assertEquals(PLUGIN_NAME, pi.getPlugin());
		assertEquals(1, pi.getProcs().size());
		assertEquals(JYTHON_PROC_NAME, pi.getProcByType(DataPanelElementProcType.POSTPROCESS)
				.getName());
		assertEquals(pi.getPostProcessProcName(),
				pi.getProcByType(DataPanelElementProcType.POSTPROCESS).getName());
	}

	@Test
	public void pluginInfoShouldSerializeToXML() throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		PluginInfo element = generateTestPluginInfo();

		Document doc = XMLUtils.objectToXML(element);
		assertEquals("pluginElement", doc.getDocumentElement().getNodeName());
		PluginInfo el2 =
			(PluginInfo) XMLUtils.xmlToObject(doc.getDocumentElement(), PluginInfo.class);
		assertTrue(ReflectionUtils.equals(element, el2));
	}

	@Test
	public void pluginInfoShouldSerializeToJSON() {
		PluginInfo element = generateTestPluginInfo();
		ObjectSerializer serializer = new JSONObjectSerializer();
		String json = serializer.serialize(element);

		assertTrue(json.contains("\"type\": \"PLUGIN\""));
		assertTrue(json.contains("\"plugin\": \"flash\""));
	}

	@Test
	public void pluginShouldSerializeToXML() {
		Plugin plugin = new Plugin();

		Document doc = XMLUtils.objectToXML(plugin);
		assertEquals("plugin", doc.getDocumentElement().getNodeName());
	}

	@Test
	public void pluginShouldSerializeToJSON() {
		Plugin plugin = new Plugin();
		plugin.setCreateProc("createFlash");

		ObjectSerializer serializer = new JSONObjectSerializer();
		String json = serializer.serialize(plugin);

		assertTrue(json.contains("\"createProc\": \"createFlash\""));
		// !!! assertTrue(json.contains("\"width\": -999"));
	}

	private PluginInfo generateTestPluginInfo() {
		PluginInfo element = new PluginInfo("id", PLUGIN_NAME, PROC_NAME);
		element.addPostProcessProc(JYTHON_PROC_NAME, JYTHON_PROC_NAME);
		return element;
	}

}
