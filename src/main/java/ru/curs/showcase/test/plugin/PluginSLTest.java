package ru.curs.showcase.test.plugin;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.html.plugin.PluginCommand;
import ru.curs.showcase.test.AbstractTest;

/**
 * Тесты для команды создания UI плагина.
 * 
 * @author den
 * 
 */
public class PluginSLTest extends AbstractTest {

	@Test
	@Ignore
	// !!!
			public
			void pluginCommandShouldReadDataFromDBAndCanRunPostProcessJython() {
		PluginInfo elInfo = new PluginInfo("id", RADAR_COMP, PLUGIN_RADAR_PROC);
		String jythonProcName = PLUGIN_HANDLE_RADAR_PY;
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = getPluginCommand(elInfo);
		Plugin plugin = command.execute();

		checkRadarInfo(plugin);
	}

	protected void checkRadarInfo(final Plugin plugin) {
		assertNotNull(plugin);
		assertNull(plugin.getDefaultAction());
		assertEquals("createRadar", plugin.getCreateProc());
		final String width = "800px";
		final String height = "600px";
		assertEquals(width, plugin.getStringSize().getWidth());
		assertEquals(height, plugin.getStringSize().getHeight());
		assertEquals("one param expected", 1, plugin.getParams().size());
		assertEquals("[{name: 'Russia', data1: 63.82, data2: 17.18, data3: 7.77},"
				+ "{name: 'Moscow', data1: 47.22, data2: 19.12, data3: 20.21},"
				+ "{name: 'Piter', data1: 58.77, data2: 13.06, data3: 15.22},]", plugin
				.getParams().get(0));
	}

	// !!! corrected
	@Test
	@Ignore
	// !!!
			public
			void pluginCommandShouldAddLibraryJS() {
		PluginInfo elInfo = new PluginInfo("id", RADAR_COMP, PLUGIN_RADAR_PROC);
		String jythonProcName = PLUGIN_HANDLE_RADAR_PY;
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = getPluginCommand(elInfo);
		Plugin plugin = command.execute();

		assertEquals(2, plugin.getRequiredJS().size());
		assertEquals("solutions/general/plugins/radar/radar.js", plugin.getRequiredJS().get(0));
		assertEquals("solutions/general/libraries/extJS/ext-all.js", plugin.getRequiredJS().get(1));
		assertEquals(1, plugin.getRequiredCSS().size());
	}

	@Test
	@Ignore
	// !!!
			public
			void pluginCommandShouldHandleEmptyImportFile() {
		PluginInfo elInfo = new PluginInfo("id", "flashD", PLUGIN_RADAR_PROC);
		String jythonProcName = "plugin/handleFlashD.py";
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = getPluginCommand(elInfo);
		Plugin plugin = command.execute();

		assertEquals(1, plugin.getRequiredJS().size());
		assertEquals(0, plugin.getRequiredCSS().size());
	}

	@Test
	@Ignore
	// !!!
			public
			void pluginCommandShouldHandleNoImportFile() {
		PluginInfo elInfo = new PluginInfo("id", "fake1", PLUGIN_RADAR_PROC);

		PluginCommand command = getPluginCommand(elInfo);
		Plugin plugin = command.execute();

		assertEquals(1, plugin.getRequiredJS().size());
		assertEquals(0, plugin.getRequiredCSS().size());
	}

	@Test
	@Ignore
	// !!!
			public
			void pluginCommandShouldAddLibraryCSS() {
		PluginInfo elInfo = new PluginInfo("id", "fakeLibPlugin", PLUGIN_RADAR_PROC);

		PluginCommand command = getPluginCommand(elInfo);
		Plugin plugin = command.execute();

		assertEquals(1, plugin.getRequiredJS().size());
		assertEquals(1, plugin.getRequiredCSS().size());
	}

	@Test
	@Ignore
	// !!!
			public
			void pluginCommandCanBeExecutedWithoutPostProcess() {
		PluginInfo elInfo = new PluginInfo("id", RADAR_COMP, PLUGIN_RADAR_PROC);
		PluginCommand command = getPluginCommand(elInfo);
		Plugin plugin = command.execute();

		assertNull(plugin.getDefaultAction());
		assertEquals("createRadar", plugin.getCreateProc());
		assertEquals("one param expected", 1, plugin.getParams().size());
		assertTrue(plugin.getParams().get(0).startsWith("<root>"));
	}

	// @Test
	public void pluginCommandCanReadDataFromSQLScript() {
		PluginInfo elInfo = new PluginInfo("id", RADAR_COMP, "plugin/radarInfo.sql");
		String jythonProcName = PLUGIN_HANDLE_RADAR_PY;
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = getPluginCommand(elInfo);
		Plugin plugin = command.execute();

		checkRadarInfo(plugin);
	}

	private PluginCommand getPluginCommand(final PluginInfo elInfo) {
		RequestData requestData = new RequestData();
		requestData.setContext(getSimpleTestContext());
		requestData.setElInfo(elInfo);

		PluginCommand command = new PluginCommand(requestData);
		return command;
	}
}
