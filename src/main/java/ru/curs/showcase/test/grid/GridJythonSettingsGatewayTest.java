package ru.curs.showcase.test.grid;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.grid.GridJythonSettingsGateway;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тест шлюза для получения настроек элемента grid c помощью выполнения Jython
 * скрипта.
 * 
 * @author bogatov
 * 
 */
public class GridJythonSettingsGatewayTest extends AbstractTestWithDefaultUserData {

	private DataPanelElementInfo getTestGridJythonInfoMetadata() {
		DataPanelElementInfo elInfo =
			new DataPanelElementInfo("GridJython01", DataPanelElementType.GRID);
		elInfo.setPosition(1);
		elInfo.setProcName("testLiveGridJython.py");
		generateTestTabWithElement(elInfo);

		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("GridJythonMetadata01");
		proc.setName("testLiveGridJythonSettings.py");
		proc.setType(DataPanelElementProcType.METADATA);
		elInfo.getProcs().put(proc.getId(), proc);
		return elInfo;
	}

	@Test
	public void testLiveGridJythonSettings() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridJythonInfoMetadata();
		ElementSettingsGateway gateway = new GridJythonSettingsGateway();
		GridContext gc = new GridContext(context);
		RecordSetElementRawData res = gateway.getRawData(gc, elInfo);
		assertNotNull(res);
		assertNotNull(res.getSettings());
	}
}
