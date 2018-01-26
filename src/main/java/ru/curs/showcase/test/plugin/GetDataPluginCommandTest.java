package ru.curs.showcase.test.plugin;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.plugin.RequestData;
import ru.curs.showcase.core.plugin.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тест команды получения данных для плагина.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginCommandTest extends AbstractTestWithDefaultUserData {

	private final String pluginname = "extJsTree";

	private PluginInfo getTestPluginInfo() {
		PluginInfo pluginElInfo =
			new PluginInfo("PluginInfoId", pluginname, "plugin/extJsTree.py");
		pluginElInfo.addPostProcessProc("PostProcessProcId", "plugin/handleExtJsTree.py");

		generateTestTabWithElement(pluginElInfo);
		return pluginElInfo;
	}

	private RequestData getRequestData() {
		CompositeContext context = getTestContext1();
		PluginInfo elInfo = getTestPluginInfo();

		RequestData requestData = new RequestData();
		requestData.setContext(context);
		requestData.setElInfo(elInfo);

		requestData.setXmlParams("<params><id>parentId</id></params>");
		return requestData;
	}

	/**
	 * с использованием Jython.
	 */
	@Test
	public void testJythonSource() {
		RequestData requestData = getRequestData();
		PluginInfo elInfo = new PluginInfo("id", pluginname, null);
		elInfo.setGetDataProcName("plugin/extJsTreeGetData.py");
		requestData.setElInfo(elInfo);

		GetDataPluginCommand command = new GetDataPluginCommand(requestData);
		ResultPluginData result = command.execute();
		assertNotNull(result);
		assertTrue(result.getData() != null && !result.getData().isEmpty());
	}

	/**
	 * с использованием хранимой процедуры.
	 */
	@Test
	public void testDBSource() {
		RequestData requestData = getRequestData();
		PluginInfo elInfo = new PluginInfo("id", pluginname, null);
		elInfo.setGetDataProcName("extJsTree_getData");
		requestData.setElInfo(elInfo);

		GetDataPluginCommand command = new GetDataPluginCommand(requestData);
		ResultPluginData result = command.execute();
		assertNotNull(result);
		assertTrue(result.getData() != null && !result.getData().isEmpty());
	}
}
